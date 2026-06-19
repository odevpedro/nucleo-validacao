package com.empresa.nucleovalidacao.service;

import com.empresa.nucleovalidacao.model.dto.ValidacaoDefinicaoDTO;
import com.empresa.nucleovalidacao.model.dto.ValidacaoResultadoDTO;
import com.empresa.nucleovalidacao.model.estado.EstadoExecucao;
import com.empresa.nucleovalidacao.model.estado.TipoValidacao;
import com.empresa.nucleovalidacao.util.JsonUtils;
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

    public ValidacaoResultadoDTO executar(ValidacaoDefinicaoDTO validacao,
                                              Map<String, Object> parametros,
                                              String usuarioDb) {
        var inicio = System.currentTimeMillis();
        var id = validacao.id();
        var nome = validacao.nome();
        var procedureRef = validacao.procedureRef();
        var tipo = validacao.tipo();

        try {
            var conn = jdbcTemplate.getDataSource().getConnection();
            try {
                var sql = montarChamada(procedureRef, validacao);
                log.debug("Executando procedure: {}", sql);

                try (var cs = conn.prepareCall(sql)) {
                    int idx = 1;

                    for (var binding : validacao.parametros()) {
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

                    if (validacao.timeoutMs() != null && validacao.timeoutMs() > 0) {
                        cs.setQueryTimeout(validacao.timeoutMs() / 1000);
                    }

                    cs.execute();

                    var resultadoNegocio = cs.getString(outResultado);
                    var mensagem = cs.getString(outMensagem);
                    var payloadStr = cs.getString(outPayload);

                    long tempoMs = System.currentTimeMillis() - inicio;

                    var payload = JsonUtils.parseJson(payloadStr);

                    return new ValidacaoResultadoDTO(
                            id, nome, procedureRef,
                            tipo.name(), EstadoExecucao.SUCESSO.name(),
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
                    : EstadoExecucao.FALHA_EXECUCAO;

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
                    "usuarioDb", usuarioDb != null ? usuarioDb : "APP_VALIDACAO"
            );

            return new ValidacaoResultadoDTO(
                    id, nome, procedureRef,
                    tipo.name(), estadoTecnico.name(),
                    "INCONCLUSIVO", tempoMs,
                    List.of("Erro ao executar procedure " + procedureRef + ": " + mensagemErro),
                    payload
            );
        }
    }

    private String montarChamada(String procedureRef, ValidacaoDefinicaoDTO validacao) {
        var params = validacao.parametros();
        int totalParams = (params != null ? params.size() : 0) + 3;
        var placeholders = "?,".repeat(totalParams);
        if (placeholders.endsWith(",")) {
            placeholders = placeholders.substring(0, placeholders.length() - 1);
        }
        return "{call BANK_CORE." + procedureRef + "(" + placeholders + ")}";
    }
}
