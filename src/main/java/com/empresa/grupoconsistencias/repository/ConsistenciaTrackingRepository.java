package com.empresa.grupoconsistencias.repository;

import com.empresa.grupoconsistencias.util.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Map;

@Repository
public class ConsistenciaTrackingRepository {

    private static final Logger log = LoggerFactory.getLogger(ConsistenciaTrackingRepository.class);
    private final JdbcTemplate jdbcTemplate;

    public ConsistenciaTrackingRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public long criarRastroGrupo(Integer idGrupoDefinicao, String nomeGrupo, String estado,
                                  String parametrosEntradaJson, String correlationId) {
        var sql = "INSERT INTO BANK_CORE.RASTRO_GRUPO " +
                  "(ID_GRUPO_DEFINICAO, NOME_GRUPO, ESTADO, PARAMETROS_ENTRADA, DATA_INICIO, USUARIO_DB, CORRELATION_ID) " +
                  "VALUES (?, ?, ?, ?, ?, USER, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            var ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, idGrupoDefinicao);
            ps.setString(2, nomeGrupo);
            ps.setString(3, estado);
            if (parametrosEntradaJson != null) {
                ps.setString(4, parametrosEntradaJson);
            } else {
                ps.setNull(4, java.sql.Types.CLOB);
            }
            ps.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(6, correlationId);
            return ps;
        }, keyHolder);

        var keys = keyHolder.getKeys();
        if (keys != null && keys.containsKey("ID_GRUPO_SOLICITACAO")) {
            return ((Number) keys.get("ID_GRUPO_SOLICITACAO")).longValue();
        }
        throw new RuntimeException("Falha ao obter ID do rastro do grupo");
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void atualizarEstadoGrupo(Long idGrupoSolicitacao, String estado, String resultadoFinalJson) {
        var sql = "UPDATE BANK_CORE.RASTRO_GRUPO SET ESTADO = ?, DATA_FIM = ?, RESULTADO_FINAL_JSON = ? " +
                  "WHERE ID_GRUPO_SOLICITACAO = ?";
        jdbcTemplate.update(sql, estado, Timestamp.valueOf(LocalDateTime.now()), resultadoFinalJson, idGrupoSolicitacao);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void criarRastroConsistencia(Long idGrupoSolicitacao, Integer idConsistenciaDef,
                                         String nomeConsistencia, String procedureRef, String estadoTecnico) {
        var sql = "INSERT INTO BANK_CORE.RASTRO_CONSISTENCIA " +
                  "(ID_GRUPO_SOLICITACAO, ID_CONSISTENCIA_DEF, NOME_CONSISTENCIA, PROCEDURE_REF, " +
                  "ESTADO_TECNICO, DATA_EXECUCAO, USUARIO_DB) " +
                  "VALUES (?, ?, ?, ?, ?, ?, USER)";
        jdbcTemplate.update(sql, idGrupoSolicitacao, idConsistenciaDef, nomeConsistencia,
                procedureRef, estadoTecnico, Timestamp.valueOf(LocalDateTime.now()));
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void atualizarRastroConsistencia(Long idGrupoSolicitacao, Integer idConsistenciaDef,
                                             String estadoTecnico, String resultadoNegocio,
                                             Long tempoMs, String payloadRetorno, String sqlState,
                                             Integer sqlErrorCode, String mensagemErro) {
        var sql = "UPDATE BANK_CORE.RASTRO_CONSISTENCIA SET " +
                  "ESTADO_TECNICO = ?, RESULTADO_NEGOCIO = ?, TEMPO_MS = ?, " +
                  "PAYLOAD_RETORNO = ?, SQL_STATE = ?, SQL_ERROR_CODE = ?, MENSAGEM_ERRO = ? " +
                  "WHERE ID_GRUPO_SOLICITACAO = ? AND ID_CONSISTENCIA_DEF = ?";
        jdbcTemplate.update(sql, estadoTecnico, resultadoNegocio, tempoMs, payloadRetorno,
                sqlState, sqlErrorCode, mensagemErro, idGrupoSolicitacao, idConsistenciaDef);
    }
}
