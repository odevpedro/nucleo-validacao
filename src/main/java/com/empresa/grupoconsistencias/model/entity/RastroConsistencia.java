package com.empresa.grupoconsistencias.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "RASTRO_CONSISTENCIA", schema = "BANK_CORE")
public class RastroConsistencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_LOG")
    private Long idLog;

    @Column(name = "ID_GRUPO_SOLICITACAO", nullable = false)
    private Long idGrupoSolicitacao;

    @Column(name = "ID_CONSISTENCIA_DEF", nullable = false)
    private Integer idConsistenciaDef;

    @Column(name = "NOME_CONSISTENCIA", nullable = false, length = 150)
    private String nomeConsistencia;

    @Column(name = "PROCEDURE_REF", nullable = false, length = 200)
    private String procedureRef;

    @Column(name = "ESTADO_TECNICO", nullable = false, length = 40)
    private String estadoTecnico;

    @Column(name = "RESULTADO_NEGOCIO", length = 40)
    private String resultadoNegocio;

    @Column(name = "TEMPO_MS")
    private Long tempoMs;

    @Column(name = "PAYLOAD_RETORNO", columnDefinition = "CLOB")
    private String payloadRetorno;

    @Column(name = "SQL_STATE", length = 20)
    private String sqlState;

    @Column(name = "SQL_ERROR_CODE")
    private Integer sqlErrorCode;

    @Column(name = "MENSAGEM_ERRO", length = 4000)
    private String mensagemErro;

    @Column(name = "USUARIO_DB", length = 100)
    private String usuarioDb;

    @Column(name = "DATA_EXECUCAO")
    private LocalDateTime dataExecucao;

    public RastroConsistencia() {}

    public Long getIdLog() { return idLog; }
    public void setIdLog(Long idLog) { this.idLog = idLog; }
    public Long getIdGrupoSolicitacao() { return idGrupoSolicitacao; }
    public void setIdGrupoSolicitacao(Long idGrupoSolicitacao) { this.idGrupoSolicitacao = idGrupoSolicitacao; }
    public Integer getIdConsistenciaDef() { return idConsistenciaDef; }
    public void setIdConsistenciaDef(Integer idConsistenciaDef) { this.idConsistenciaDef = idConsistenciaDef; }
    public String getNomeConsistencia() { return nomeConsistencia; }
    public void setNomeConsistencia(String nomeConsistencia) { this.nomeConsistencia = nomeConsistencia; }
    public String getProcedureRef() { return procedureRef; }
    public void setProcedureRef(String procedureRef) { this.procedureRef = procedureRef; }
    public String getEstadoTecnico() { return estadoTecnico; }
    public void setEstadoTecnico(String estadoTecnico) { this.estadoTecnico = estadoTecnico; }
    public String getResultadoNegocio() { return resultadoNegocio; }
    public void setResultadoNegocio(String resultadoNegocio) { this.resultadoNegocio = resultadoNegocio; }
    public Long getTempoMs() { return tempoMs; }
    public void setTempoMs(Long tempoMs) { this.tempoMs = tempoMs; }
    public String getPayloadRetorno() { return payloadRetorno; }
    public void setPayloadRetorno(String payloadRetorno) { this.payloadRetorno = payloadRetorno; }
    public String getSqlState() { return sqlState; }
    public void setSqlState(String sqlState) { this.sqlState = sqlState; }
    public Integer getSqlErrorCode() { return sqlErrorCode; }
    public void setSqlErrorCode(Integer sqlErrorCode) { this.sqlErrorCode = sqlErrorCode; }
    public String getMensagemErro() { return mensagemErro; }
    public void setMensagemErro(String mensagemErro) { this.mensagemErro = mensagemErro; }
    public String getUsuarioDb() { return usuarioDb; }
    public void setUsuarioDb(String usuarioDb) { this.usuarioDb = usuarioDb; }
    public LocalDateTime getDataExecucao() { return dataExecucao; }
    public void setDataExecucao(LocalDateTime dataExecucao) { this.dataExecucao = dataExecucao; }
}
