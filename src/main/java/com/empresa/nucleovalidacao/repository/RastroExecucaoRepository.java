package com.empresa.nucleovalidacao.repository;

import com.empresa.nucleovalidacao.model.entity.RastroExecucao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RastroExecucaoRepository extends JpaRepository<RastroExecucao, Long> {

    List<RastroExecucao> findByIdGrupoSolicitacao(Long idGrupoSolicitacao);

    @Query("""
            SELECT rc.idExecucaoDef AS idExecucaoDef,
                   rc.nomeExecucao AS nomeExecucao,
                   COUNT(rc) AS totalExecucoes,
                   SUM(CASE WHEN rc.estadoTecnico = 'FALHA_AUTORIZACAO' THEN 1 ELSE 0 END) AS falhasAutorizacao
            FROM RastroExecucao rc
            JOIN RastroValidacao rg ON rg.idGrupoSolicitacao = rc.idGrupoSolicitacao
            WHERE rg.dataInicio >= :dataCorte
            GROUP BY rc.idExecucaoDef, rc.nomeExecucao
            """)
    List<Object[]> findFalhasAutorizacaoPorGrupo(@Param("dataCorte") LocalDateTime dataCorte);
}
