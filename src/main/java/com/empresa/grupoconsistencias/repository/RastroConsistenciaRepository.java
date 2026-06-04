package com.empresa.grupoconsistencias.repository;

import com.empresa.grupoconsistencias.model.entity.RastroConsistencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RastroConsistenciaRepository extends JpaRepository<RastroConsistencia, Long> {

    List<RastroConsistencia> findByIdGrupoSolicitacao(Long idGrupoSolicitacao);

    @Query("""
            SELECT rc.idConsistenciaDef AS idConsistenciaDef,
                   rc.nomeConsistencia AS nomeConsistencia,
                   COUNT(rc) AS totalExecucoes,
                   SUM(CASE WHEN rc.estadoTecnico = 'FALHA_AUTORIZACAO' THEN 1 ELSE 0 END) AS falhasAutorizacao
            FROM RastroConsistencia rc
            JOIN RastroGrupo rg ON rg.idGrupoSolicitacao = rc.idGrupoSolicitacao
            WHERE rg.dataInicio >= :dataCorte
            GROUP BY rc.idConsistenciaDef, rc.nomeConsistencia
            """)
    List<Object[]> findFalhasAutorizacaoPorGrupo(@Param("dataCorte") LocalDateTime dataCorte);
}
