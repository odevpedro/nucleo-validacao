package com.empresa.nucleovalidacao.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class ComplianceRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public ComplianceRepository() {}

    public List<FalhaAutorizacaoGrupo> findFalhasAutorizacaoUltimos7Dias() {
        var dataCorte = LocalDateTime.now().minusDays(7);
        var sql = """
                SELECT rg.ID_GRUPO_DEFINICAO, rg.NOME_GRUPO,
                       COUNT(*) AS TOTAL_EXECUCOES,
                       SUM(CASE WHEN rc.ESTADO_TECNICO = 'FALHA_AUTORIZACAO' THEN 1 ELSE 0 END) AS FALHAS_AUTORIZACAO
                FROM BANK_CORE.RASTRO_VALIDACAO rg
                JOIN BANK_CORE.RASTRO_EXECUCAO rc ON rc.ID_GRUPO_SOLICITACAO = rg.ID_GRUPO_SOLICITACAO
                WHERE rg.DATA_INICIO >= :dataCorte
                GROUP BY rg.ID_GRUPO_DEFINICAO, rg.NOME_GRUPO
                """;

        var query = entityManager.createNativeQuery(sql);
        query.setParameter("dataCorte", java.sql.Timestamp.valueOf(dataCorte));

        @SuppressWarnings("unchecked")
        List<Object[]> rows = query.getResultList();

        return rows.stream().map(row -> new FalhaAutorizacaoGrupo(
                ((Number) row[0]).intValue(),
                (String) row[1],
                ((Number) row[2]).longValue(),
                row[3] != null ? ((Number) row[3]).longValue() : 0L
        )).toList();
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
