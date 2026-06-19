package com.empresa.nucleovalidacao.service;

import com.empresa.nucleovalidacao.model.estado.EstadoExecucao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.sql.SQLException;

@Component
public class SqlErrorMapper {

    private static final Logger log = LoggerFactory.getLogger(SqlErrorMapper.class);

    public EstadoExecucao map(SQLException e) {
        var errorCode = e.getErrorCode();
        var sqlState = e.getSQLState();
        var message = e.getMessage() != null ? e.getMessage().toLowerCase() : "";

        if (errorCode == 1031 || (message != null && message.contains("ora-01031"))) {
            return EstadoExecucao.FALHA_AUTORIZACAO;
        }
        if ("42501".equals(sqlState)) {
            return EstadoExecucao.FALHA_AUTORIZACAO;
        }
        if (errorCode == 1013 || message.contains("timeout") || message.contains("time out")) {
            return EstadoExecucao.TIMEOUT;
        }

        var next = e.getNextException();
        if (next != null && next != e) {
            log.debug("Verificando SQLException encadeada: {}", next.getMessage());
            return map(next);
        }

        return EstadoExecucao.FALHA_EXECUCAO;
    }

    public String extractMessage(SQLException e) {
        if (e.getMessage() != null) return e.getMessage();
        var next = e.getNextException();
        if (next != null && next != e) return extractMessage(next);
        return "Erro SQL desconhecido";
    }
}
