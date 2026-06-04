package com.empresa.grupoconsistencias.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public class ComplianceRepository {

    private final JdbcTemplate jdbcTemplate;

    public ComplianceRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<FalhaAutorizacaoGrupo> findFalhasAutorizacaoUltimos7Dias() {
        var sql = """
                SELECT rg.ID_GRUPO_DEFINICAO, rg.NOME_GRUPO,
                       COUNT(*) AS TOTAL_EXECUCOES,
                       SUM(CASE WHEN rc.ESTADO_TECNICO = 'FALHA_AUTORIZACAO' THEN 1 ELSE 0 END) AS FALHAS_AUTORIZACAO
                FROM BANK_CORE.RASTRO_GRUPO rg
                JOIN BANK_CORE.RASTRO_CONSISTENCIA rc ON rc.ID_GRUPO_SOLICITACAO = rg.ID_GRUPO_SOLICITACAO
                WHERE rg.DATA_INICIO >= SYSDATE - 7
                GROUP BY rg.ID_GRUPO_DEFINICAO, rg.NOME_GRUPO
                """;
        return jdbcTemplate.query(sql, (rs, row) -> new FalhaAutorizacaoGrupo(
                rs.getInt("ID_GRUPO_DEFINICAO"),
                rs.getString("NOME_GRUPO"),
                rs.getLong("TOTAL_EXECUCOES"),
                rs.getLong("FALHAS_AUTORIZACAO")
        ));
    }

    public record FalhaAutorizacaoGrupo(
            Integer idGrupoDefinicao,
            String nomeGrupo,
            Long totalExecucoes,
            Long falhasAutorizacao
    ) {
        public BigDecimal percentualFalha() {
            if (totalExecucoes == 0) return BigDecimal.ZERO;
            return BigDecimal.valueOf(falhasAutorizacao * 100.0 / totalExecucoes);
        }
    }
}
