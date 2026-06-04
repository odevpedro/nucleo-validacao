package com.empresa.grupoconsistencias.repository;

import com.empresa.grupoconsistencias.model.entity.RastroConsistencia;
import com.empresa.grupoconsistencias.model.entity.RastroGrupo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Repository
public class ConsistenciaTrackingRepository {

    private static final Logger log = LoggerFactory.getLogger(ConsistenciaTrackingRepository.class);

    @PersistenceContext
    private EntityManager entityManager;

    private final RastroGrupoRepository rastroGrupoRepository;
    private final RastroConsistenciaRepository rastroConsistenciaRepository;

    public ConsistenciaTrackingRepository(RastroGrupoRepository rastroGrupoRepository,
                                           RastroConsistenciaRepository rastroConsistenciaRepository) {
        this.rastroGrupoRepository = rastroGrupoRepository;
        this.rastroConsistenciaRepository = rastroConsistenciaRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public long criarRastroGrupo(Integer idGrupoDefinicao, String nomeGrupo, String estado,
                                  String parametrosEntradaJson, String correlationId) {
        var rastro = new RastroGrupo();
        rastro.setIdGrupoDefinicao(idGrupoDefinicao);
        rastro.setNomeGrupo(nomeGrupo);
        rastro.setEstado(estado);
        rastro.setParametrosEntrada(parametrosEntradaJson);
        rastro.setDataInicio(LocalDateTime.now());
        rastro.setUsuarioDb(obterUsuarioDb());
        rastro.setCorrelationId(correlationId);
        rastro = rastroGrupoRepository.save(rastro);
        return rastro.getIdGrupoSolicitacao();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void atualizarEstadoGrupo(Long idGrupoSolicitacao, String estado, String resultadoFinalJson) {
        var rastro = rastroGrupoRepository.findById(idGrupoSolicitacao)
                .orElseThrow(() -> new RuntimeException("RastroGrupo nao encontrado: " + idGrupoSolicitacao));
        rastro.setEstado(estado);
        rastro.setDataFim(LocalDateTime.now());
        rastro.setResultadoFinalJson(resultadoFinalJson);
        rastroGrupoRepository.save(rastro);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void criarRastroConsistencia(Long idGrupoSolicitacao, Integer idConsistenciaDef,
                                         String nomeConsistencia, String procedureRef, String estadoTecnico) {
        var rastro = new RastroConsistencia();
        rastro.setIdGrupoSolicitacao(idGrupoSolicitacao);
        rastro.setIdConsistenciaDef(idConsistenciaDef);
        rastro.setNomeConsistencia(nomeConsistencia);
        rastro.setProcedureRef(procedureRef);
        rastro.setEstadoTecnico(estadoTecnico);
        rastro.setDataExecucao(LocalDateTime.now());
        rastro.setUsuarioDb(obterUsuarioDb());
        rastroConsistenciaRepository.save(rastro);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void atualizarRastroConsistencia(Long idGrupoSolicitacao, Integer idConsistenciaDef,
                                             String estadoTecnico, String resultadoNegocio,
                                             Long tempoMs, String payloadRetorno, String sqlState,
                                             Integer sqlErrorCode, String mensagemErro) {
        var rastro = rastroConsistenciaRepository
                .findByIdGrupoSolicitacao(idGrupoSolicitacao)
                .stream()
                .filter(r -> r.getIdConsistenciaDef().equals(idConsistenciaDef))
                .findFirst()
                .orElse(null);
        if (rastro == null) return;
        rastro.setEstadoTecnico(estadoTecnico);
        rastro.setResultadoNegocio(resultadoNegocio);
        rastro.setTempoMs(tempoMs);
        rastro.setPayloadRetorno(payloadRetorno);
        rastro.setSqlState(sqlState);
        rastro.setSqlErrorCode(sqlErrorCode);
        rastro.setMensagemErro(mensagemErro);
        rastroConsistenciaRepository.save(rastro);
    }

    private String obterUsuarioDb() {
        try {
            return (String) entityManager.createNativeQuery("SELECT USER FROM DUAL")
                    .getSingleResult();
        } catch (Exception e) {
            log.warn("Nao foi possivel obter usuario do banco: {}", e.getMessage());
            return "APP_CONSISTENCIA";
        }
    }
}
