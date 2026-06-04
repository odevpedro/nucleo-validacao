package com.empresa.grupoconsistencias.service;

import com.empresa.grupoconsistencias.config.GrupoConsistenciaYamlLoader;
import com.empresa.grupoconsistencias.exception.GrupoNaoEncontradoException;
import com.empresa.grupoconsistencias.exception.ValidacaoException;
import com.empresa.grupoconsistencias.model.dto.*;
import com.empresa.grupoconsistencias.model.estado.EstadoConsistencia;
import com.empresa.grupoconsistencias.model.estado.EstadoGrupo;
import com.empresa.grupoconsistencias.repository.ConsistenciaTrackingRepository;
import com.empresa.grupoconsistencias.util.JsonUtils;
import com.empresa.grupoconsistencias.util.ParametrosMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class GrupoConsistenciaGateway {

    private static final Logger log = LoggerFactory.getLogger(GrupoConsistenciaGateway.class);

    private final GrupoConsistenciaYamlLoader yamlLoader;
    private final ParametroValidator parametroValidator;
    private final ConsistenciaTrackingRepository trackingRepository;
    private final ProcedureExecutor procedureExecutor;
    private final ResultadoGrupoConsolidador consolidador;
    private final DataSource dataSource;

    public GrupoConsistenciaGateway(GrupoConsistenciaYamlLoader yamlLoader,
                                     ParametroValidator parametroValidator,
                                     ConsistenciaTrackingRepository trackingRepository,
                                     ProcedureExecutor procedureExecutor,
                                     ResultadoGrupoConsolidador consolidador,
                                     DataSource dataSource) {
        this.yamlLoader = yamlLoader;
        this.parametroValidator = parametroValidator;
        this.trackingRepository = trackingRepository;
        this.procedureExecutor = procedureExecutor;
        this.consolidador = consolidador;
        this.dataSource = dataSource;
    }

    public GrupoConsistenciaResponseDTO executar(GrupoConsistenciaRequest request) {
        var correlationId = request.correlationId() != null ? request.correlationId() : UUID.randomUUID().toString();
        var parametros = ParametrosMapper.toMap(request.parametros());
        var idGrupo = request.idGrupoConsistencia();

        var grupo = yamlLoader.getGrupo(idGrupo);
        if (grupo == null) {
            log.warn("Grupo {} nao encontrado", idGrupo);
            throw new GrupoNaoEncontradoException(idGrupo);
        }

        if (!Boolean.TRUE.equals(grupo.ativo())) {
            var erro = new ErroValidacaoDTO("idGrupoConsistencia",
                    "Grupo de consistencia inativo", idGrupo);
            throw new ValidacaoException(List.of(erro));
        }

        var parametrosJson = JsonUtils.toJson(parametros);
        var idSolicitacao = trackingRepository.criarRastroGrupo(
                idGrupo, grupo.nome(), EstadoGrupo.CRIADO.name(), parametrosJson, correlationId);

        try {
            trackingRepository.atualizarEstadoGrupo(idSolicitacao, EstadoGrupo.VALIDANDO.name(), null);

            var erros = parametroValidator.validar(grupo, parametros);

            if (!erros.isEmpty()) {
                trackingRepository.atualizarEstadoGrupo(idSolicitacao, EstadoGrupo.FALHA_VALIDACAO.name(),
                        JsonUtils.toJson(Map.of("erros", erros)));

                GrupoConsistenciaErroResponseDTO erroResponse = new GrupoConsistenciaErroResponseDTO(
                        idGrupo, grupo.nome(), EstadoGrupo.FALHA_VALIDACAO.name(),
                        "Parametros de entrada invalidos", correlationId, erros
                );
                throw new ValidacaoException(erros);
            }

            trackingRepository.atualizarEstadoGrupo(idSolicitacao, EstadoGrupo.EXECUTANDO.name(), null);

            var resultados = new ArrayList<ConsistenciaResultadoDTO>();
            boolean deveAbortar = false;
            String usuarioDb = obterUsuarioDb();

            for (var consistencia : grupo.consistencias()) {
                if (deveAbortar) {
                    resultados.add(new ConsistenciaResultadoDTO(
                            consistencia.id(), consistencia.nome(), consistencia.procedureRef(),
                            consistencia.tipo() != null ? consistencia.tipo().name() : "LEITURA",
                            EstadoConsistencia.AGUARDANDO.name(),
                            "INCONCLUSIVO", 0L,
                            List.of("Execucao abortada devido a falha anterior"), null
                    ));
                    continue;
                }

                if (consistencia.tipo() == null) continue;

                log.debug("Executando consistencia {}: {}", consistencia.id(), consistencia.nome());

                trackingRepository.criarRastroConsistencia(
                        idSolicitacao, consistencia.id(), consistencia.nome(),
                        consistencia.procedureRef(), EstadoConsistencia.EXECUTANDO.name());

                var resultado = procedureExecutor.executar(consistencia, parametros, usuarioDb);

                String sqlState = null;
                Integer errorCode = null;
                if (resultado.payload() instanceof Map<?, ?> m) {
                    sqlState = (String) m.getOrDefault("sqlState", null);
                    Object ec = m.get("errorCode");
                    if (ec instanceof Number n) errorCode = n.intValue();
                }

                trackingRepository.atualizarRastroConsistencia(
                        idSolicitacao, consistencia.id(), resultado.estadoTecnico(),
                        resultado.resultadoNegocio(), resultado.tempoMs(),
                        JsonUtils.toJson(resultado.payload()),
                        sqlState, errorCode,
                        resultado.mensagens() != null && !resultado.mensagens().isEmpty()
                                ? String.join("; ", resultado.mensagens()) : null
                );

                resultados.add(resultado);

                if (Boolean.TRUE.equals(consistencia.abortarGrupoEmFalha())
                        && !EstadoConsistencia.SUCESSO.name().equals(resultado.estadoTecnico())) {
                    deveAbortar = true;
                }
            }

            var consolidado = consolidador.consolidar(grupo, resultados);
            var responseJson = JsonUtils.toJson(resultados);

            trackingRepository.atualizarEstadoGrupo(idSolicitacao, consolidado.estadoGrupo().name(), responseJson);

            return new GrupoConsistenciaResponseDTO(
                    idSolicitacao, idGrupo, grupo.nome(),
                    consolidado.estadoGrupo().name(),
                    consolidado.resultadoNegocioGrupo().name(),
                    consolidado.mensagem(), correlationId, resultados
            );

        } catch (ValidacaoException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erro durante execucao do grupo {}", idGrupo, e);
            trackingRepository.atualizarEstadoGrupo(idSolicitacao,
                    EstadoGrupo.FALHA_CRITICA.name(), JsonUtils.toJson(Map.of("erro", e.getMessage())));

            return new GrupoConsistenciaResponseDTO(
                    idSolicitacao, idGrupo, grupo.nome(),
                    EstadoGrupo.FALHA_CRITICA.name(),
                    "INCONCLUSIVO", "Erro critico na execucao do grupo: " + e.getMessage(),
                    correlationId, List.of()
            );
        }
    }

    private String obterUsuarioDb() {
        try (Connection conn = dataSource.getConnection()) {
            return conn.getMetaData().getUserName();
        } catch (SQLException e) {
            log.warn("Nao foi possivel obter usuario do banco: {}", e.getMessage());
            return "APP_CONSISTENCIA";
        }
    }
}
