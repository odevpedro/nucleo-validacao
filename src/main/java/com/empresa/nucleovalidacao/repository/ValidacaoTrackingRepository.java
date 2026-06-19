package com.empresa.nucleovalidacao.repository;

import com.empresa.nucleovalidacao.model.entity.RastroExecucao;
import com.empresa.nucleovalidacao.model.entity.RastroValidacao;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Repository
public class ValidacaoTrackingRepository {

    private static final Logger log = LoggerFactory.getLogger(ValidacaoTrackingRepository.class);

    @PersistenceContext
    private EntityManager entityManager;

    private final RastroValidacaoRepository rastroValidacaoRepository;
    private final RastroExecucaoRepository rastroExecucaoRepository;

    public ValidacaoTrackingRepository(RastroValidacaoRepository rastroValidacaoRepository,
                                           RastroExecucaoRepository rastroExecucaoRepository) {
        this.rastroValidacaoRepository = rastroValidacaoRepository;
        this.rastroExecucaoRepository = rastroExecucaoRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public long criarRastroValidacao(Integer idGrupoDefinicao, String nomeGrupo, String estado,
                                  String parametrosEntradaJson, String correlationId) {
        var rastro = new RastroValidacao();
        rastro.setIdGrupoDefinicao(idGrupoDefinicao);
        rastro.setNomeGrupo(nomeGrupo);
        rastro.setEstado(estado);
        rastro.setParametrosEntrada(parametrosEntradaJson);
        rastro.setDataInicio(LocalDateTime.now());
        rastro.setUsuarioDb(obterUsuarioDb());
        rastro.setCorrelationId(correlationId);
        rastro = rastroValidacaoRepository.save(rastro);
        return rastro.getIdGrupoSolicitacao();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void atualizarEstadoValidacao(Long idGrupoSolicitacao, String estado, String resultadoFinalJson) {
        var rastro = rastroValidacaoRepository.findById(idGrupoSolicitacao)
                .orElseThrow(() -> new RuntimeException("RastroValidacao nao encontrado: " + idGrupoSolicitacao));
        rastro.setEstado(estado);
        rastro.setDataFim(LocalDateTime.now());
        rastro.setResultadoFinalJson(resultadoFinalJson);
        rastroValidacaoRepository.save(rastro);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void criarRastroExecucao(Long idGrupoSolicitacao, Integer idExecucaoDef,
                                         String nomeExecucao, String procedureRef, String estadoTecnico) {
        var rastro = new RastroExecucao();
        rastro.setIdGrupoSolicitacao(idGrupoSolicitacao);
        rastro.setIdExecucaoDef(idExecucaoDef);
        rastro.setNomeExecucao(nomeExecucao);
        rastro.setProcedureRef(procedureRef);
        rastro.setEstadoTecnico(estadoTecnico);
        rastro.setDataExecucao(LocalDateTime.now());
        rastro.setUsuarioDb(obterUsuarioDb());
        rastroExecucaoRepository.save(rastro);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void atualizarRastroExecucao(Long idGrupoSolicitacao, Integer idExecucaoDef,
                                             String estadoTecnico, String resultadoNegocio,
                                             Long tempoMs, String payloadRetorno, String sqlState,
                                             Integer sqlErrorCode, String mensagemErro) {
        var rastro = rastroExecucaoRepository
                .findByIdGrupoSolicitacao(idGrupoSolicitacao)
                .stream()
                .filter(r -> r.getIdExecucaoDef().equals(idExecucaoDef))
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
        rastroExecucaoRepository.save(rastro);
    }

    private String obterUsuarioDb() {
        try {
            return (String) entityManager.createNativeQuery("SELECT USER FROM DUAL")
                    .getSingleResult();
        } catch (Exception e) {
            log.warn("Nao foi possivel obter usuario do banco: {}", e.getMessage());
            return "APP_VALIDACAO";
        }
    }
}
