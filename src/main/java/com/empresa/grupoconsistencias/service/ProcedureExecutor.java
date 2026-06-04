package com.empresa.grupoconsistencias.service;

import com.empresa.grupoconsistencias.model.dto.ConsistenciaDefinicaoDTO;
import com.empresa.grupoconsistencias.model.dto.ConsistenciaResultadoDTO;
import com.empresa.grupoconsistencias.model.estado.EstadoConsistencia;
import com.empresa.grupoconsistencias.model.estado.TipoConsistencia;
import com.empresa.grupoconsistencias.util.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;
import java.util.*;

@Component
public class ProcedureExecutor {

    private static final Logger log = LoggerFactory.getLogger(ProcedureExecutor.class);

    private final JdbcTemplate jdbcTemplate;
    private final SqlErrorMapper sqlErrorMapper;

    public ProcedureExecutor(JdbcTemplate jdbcTemplate, SqlErrorMapper sqlErrorMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.sqlErrorMapper = sqlErrorMapper;
    }

    public ConsistenciaResultadoDTO executar(ConsistenciaDefinicaoDTO consistencia,
                                              Map<String, Object> parametros,
                                              String usuarioDb) {
        var inicio = System.currentTimeMillis();
        var id = consistencia.id();
        var nome = consistencia.nome();
        var procedureRef = consistencia.procedureRef();
        var tipo = consistencia.tipo();

        try {
            var conn = jdbcTemplate.getDataSource().getConnection();
            try {
                var sql = montarChamada(procedureRef, consistencia);
                log.debug("Executando procedure: {}", sql);

                try (var cs = conn.prepareCall(sql)) {
                    int idx = 1;

                    for (var binding : consistencia.parametros()) {
                        var valor = parametros.get(binding.origem());
                        if (binding.destino() != null) {
                            cs.setObject(idx++, valor);
                        }
                    }

                    int outResultado = idx++;
                    int outMensagem = idx++;
                    int outPayload = idx++;

                    cs.registerOutParameter(outResultado, Types.VARCHAR);
                    cs.registerOutParameter(outMensagem, Types.VARCHAR);
                    cs.registerOutParameter(outPayload, Types.CLOB);

                    if (consistencia.timeoutMs() != null && consistencia.timeoutMs() > 0) {
                        cs.setQueryTimeout(consistencia.timeoutMs() / 1000);
                    }

                    cs.execute();

                    var resultadoNegocio = cs.getString(outResultado);
                    var mensagem = cs.getString(outMensagem);
                    var payloadStr = cs.getString(outPayload);

                    long tempoMs = System.currentTimeMillis() - inicio;

                    var payload = JsonUtils.parseJson(payloadStr);

                    return new ConsistenciaResultadoDTO(
                            id, nome, procedureRef,
                            tipo.name(), EstadoConsistencia.SUCESSO.name(),
                            resultadoNegocio, tempoMs,
                            mensagem != null ? List.of(mensagem) : List.of(),
                            payload
                    );
                }
            } finally {
                conn.close();
            }
        } catch (Exception e) {
            long tempoMs = System.currentTimeMillis() - inicio;
            var sqlEx = (e instanceof java.sql.SQLException) ? (java.sql.SQLException) e : null;

            var estadoTecnico = sqlEx != null
                    ? sqlErrorMapper.map(sqlEx)
                    : EstadoConsistencia.FALHA_EXECUCAO;

            String mensagemErro;
            String sqlState = null;
            Integer errorCode = null;

            if (sqlEx != null) {
                mensagemErro = sqlErrorMapper.extractMessage(sqlEx);
                sqlState = sqlEx.getSQLState();
                errorCode = sqlEx.getErrorCode();
                log.error("Erro SQL ao executar {}: errorCode={}, sqlState={}, msg={}",
                        procedureRef, errorCode, sqlState, mensagemErro);
            } else {
                mensagemErro = e.getMessage();
                log.error("Erro ao executar procedure {}: {}", procedureRef, mensagemErro, e);
            }

            var payload = Map.of(
                    "sqlState", sqlState != null ? sqlState : "N/A",
                    "errorCode", errorCode != null ? errorCode : 0,
                    "mensagem", mensagemErro != null ? mensagemErro : "Erro desconhecido",
                    "usuarioDb", usuarioDb != null ? usuarioDb : "APP_CONSISTENCIA"
            );

            return new ConsistenciaResultadoDTO(
                    id, nome, procedureRef,
                    tipo.name(), estadoTecnico.name(),
                    "INCONCLUSIVO", tempoMs,
                    List.of("Erro ao executar procedure " + procedureRef + ": " + mensagemErro),
                    payload
            );
        }
    }

    private String montarChamada(String procedureRef, ConsistenciaDefinicaoDTO consistencia) {
        var params = consistencia.parametros();
        int totalParams = (params != null ? params.size() : 0) + 3;
        var placeholders = "?,".repeat(totalParams);
        if (placeholders.endsWith(",")) {
            placeholders = placeholders.substring(0, placeholders.length() - 1);
        }
        return "{call BANK_CORE." + procedureRef + "(" + placeholders + ")}";
    }
}
