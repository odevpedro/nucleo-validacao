package com.empresa.grupoconsistencias.service;

import com.empresa.grupoconsistencias.model.estado.EstadoConsistencia;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.sql.SQLException;

@Component
public class SqlErrorMapper {

    private static final Logger log = LoggerFactory.getLogger(SqlErrorMapper.class);

    public EstadoConsistencia map(SQLException e) {
        var errorCode = e.getErrorCode();
        var sqlState = e.getSQLState();
        var message = e.getMessage() != null ? e.getMessage().toLowerCase() : "";

        if (errorCode == 1031 || (message != null && message.contains("ora-01031"))) {
            return EstadoConsistencia.FALHA_AUTORIZACAO;
        }
        if ("42501".equals(sqlState)) {
            return EstadoConsistencia.FALHA_AUTORIZACAO;
        }
        if (errorCode == 1013 || message.contains("timeout") || message.contains("time out")) {
            return EstadoConsistencia.TIMEOUT;
        }

        var next = e.getNextException();
        if (next != null && next != e) {
            log.debug("Verificando SQLException encadeada: {}", next.getMessage());
            return map(next);
        }

        return EstadoConsistencia.FALHA_EXECUCAO;
    }

    public String extractMessage(SQLException e) {
        if (e.getMessage() != null) return e.getMessage();
        var next = e.getNextException();
        if (next != null && next != e) return extractMessage(next);
        return "Erro SQL desconhecido";
    }
}
