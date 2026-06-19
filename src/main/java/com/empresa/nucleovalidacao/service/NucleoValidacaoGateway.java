package com.empresa.nucleovalidacao.service;

import com.empresa.nucleovalidacao.config.NucleoValidacaoYamlLoader;
import com.empresa.nucleovalidacao.exception.ValidacaoNaoEncontradaException;
import com.empresa.nucleovalidacao.exception.ValidacaoException;
import com.empresa.nucleovalidacao.model.dto.*;
import com.empresa.nucleovalidacao.model.estado.EstadoExecucao;
import com.empresa.nucleovalidacao.model.estado.EstadoValidacao;
import com.empresa.nucleovalidacao.repository.ValidacaoTrackingRepository;
import com.empresa.nucleovalidacao.util.JsonUtils;
import com.empresa.nucleovalidacao.util.ParametrosMapper;
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
public class NucleoValidacaoGateway {

    private static final Logger log = LoggerFactory.getLogger(NucleoValidacaoGateway.class);

    private final NucleoValidacaoYamlLoader yamlLoader;
    private final ParametroValidator parametroValidator;
    private final ValidacaoTrackingRepository trackingRepository;
    private final ProcedureExecutor procedureExecutor;
    private final ResultadoValidacaoConsolidador consolidador;
    private final DataSource dataSource;

    public NucleoValidacaoGateway(NucleoValidacaoYamlLoader yamlLoader,
                                     ParametroValidator parametroValidator,
                                     ValidacaoTrackingRepository trackingRepository,
                                     ProcedureExecutor procedureExecutor,
                                     ResultadoValidacaoConsolidador consolidador,
                                     DataSource dataSource) {
        this.yamlLoader = yamlLoader;
        this.parametroValidator = parametroValidator;
        this.trackingRepository = trackingRepository;
        this.procedureExecutor = procedureExecutor;
        this.consolidador = consolidador;
        this.dataSource = dataSource;
    }

    public ValidacaoResponseDTO executar(ValidacaoRequest request) {
        var correlationId = request.correlationId() != null ? request.correlationId() : UUID.randomUUID().toString();
        var parametros = ParametrosMapper.toMap(request.parametros());
        var idGrupo = request.idGrupoValidacao();

        var grupo = yamlLoader.getGrupo(idGrupo);
        if (grupo == null) {
            log.warn("Grupo {} nao encontrado", idGrupo);
            throw new ValidacaoNaoEncontradaException(idGrupo);
        }

        if (!Boolean.TRUE.equals(grupo.ativo())) {
            var erro = new ErroValidacaoDTO("idGrupoValidacao",
                    "Grupo de consistencia inativo", idGrupo);
            throw new ValidacaoException(List.of(erro));
        }

        var parametrosJson = JsonUtils.toJson(parametros);
        var idSolicitacao = trackingRepository.criarRastroValidacao(
                idGrupo, grupo.nome(), EstadoValidacao.CRIADO.name(), parametrosJson, correlationId);

        try {
            trackingRepository.atualizarEstadoValidacao(idSolicitacao, EstadoValidacao.VALIDANDO.name(), null);

            var erros = parametroValidator.validar(grupo, parametros);

            if (!erros.isEmpty()) {
                trackingRepository.atualizarEstadoValidacao(idSolicitacao, EstadoValidacao.FALHA_VALIDACAO.name(),
                        JsonUtils.toJson(Map.of("erros", erros)));

                ValidacaoErroResponseDTO erroResponse = new ValidacaoErroResponseDTO(
                        idGrupo, grupo.nome(), EstadoValidacao.FALHA_VALIDACAO.name(),
                        "Parametros de entrada invalidos", correlationId, erros
                );
                throw new ValidacaoException(erros);
            }

            trackingRepository.atualizarEstadoValidacao(idSolicitacao, EstadoValidacao.EXECUTANDO.name(), null);

            var resultados = new ArrayList<ValidacaoResultadoDTO>();
            boolean deveAbortar = false;
            String usuarioDb = obterUsuarioDb();

            for (var validacao : grupo.validacoes()) {
                if (deveAbortar) {
                    resultados.add(new ValidacaoResultadoDTO(
                            validacao.id(), validacao.nome(), validacao.procedureRef(),
                            validacao.tipo() != null ? validacao.tipo().name() : "LEITURA",
                            EstadoExecucao.AGUARDANDO.name(),
                            "INCONCLUSIVO", 0L,
                            List.of("Execucao abortada devido a falha anterior"), null
                    ));
                    continue;
                }

                if (validacao.tipo() == null) continue;

                log.debug("Executando validacao {}: {}", validacao.id(), validacao.nome());

                trackingRepository.criarRastroExecucao(
                        idSolicitacao, validacao.id(), validacao.nome(),
                        validacao.procedureRef(), EstadoExecucao.EXECUTANDO.name());

                var resultado = procedureExecutor.executar(validacao, parametros, usuarioDb);

                String sqlState = null;
                Integer errorCode = null;
                if (resultado.payload() instanceof Map<?, ?> m) {
                    sqlState = (String) m.getOrDefault("sqlState", null);
                    Object ec = m.get("errorCode");
                    if (ec instanceof Number n) errorCode = n.intValue();
                }

                trackingRepository.atualizarRastroExecucao(
                        idSolicitacao, validacao.id(), resultado.estadoTecnico(),
                        resultado.resultadoNegocio(), resultado.tempoMs(),
                        JsonUtils.toJson(resultado.payload()),
                        sqlState, errorCode,
                        resultado.mensagens() != null && !resultado.mensagens().isEmpty()
                                ? String.join("; ", resultado.mensagens()) : null
                );

                resultados.add(resultado);

                if (Boolean.TRUE.equals(validacao.abortarGrupoEmFalha())
                        && !EstadoExecucao.SUCESSO.name().equals(resultado.estadoTecnico())) {
                    deveAbortar = true;
                }
            }

            var consolidado = consolidador.consolidar(grupo, resultados);
            var responseJson = JsonUtils.toJson(resultados);

            trackingRepository.atualizarEstadoValidacao(idSolicitacao, consolidado.estadoGrupo().name(), responseJson);

            return new ValidacaoResponseDTO(
                    idSolicitacao, idGrupo, grupo.nome(),
                    consolidado.estadoGrupo().name(),
                    consolidado.resultadoNegocioGrupo().name(),
                    consolidado.mensagem(), correlationId, resultados
            );

        } catch (ValidacaoException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erro durante execucao do grupo {}", idGrupo, e);
            trackingRepository.atualizarEstadoValidacao(idSolicitacao,
                    EstadoValidacao.FALHA_CRITICA.name(), JsonUtils.toJson(Map.of("erro", e.getMessage())));

            return new ValidacaoResponseDTO(
                    idSolicitacao, idGrupo, grupo.nome(),
                    EstadoValidacao.FALHA_CRITICA.name(),
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
            return "APP_VALIDACAO";
        }
    }
}
