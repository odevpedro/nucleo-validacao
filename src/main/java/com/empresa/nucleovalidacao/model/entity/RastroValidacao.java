package com.empresa.nucleovalidacao.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "RASTRO_VALIDACAO", schema = "BANK_CORE")
public class RastroValidacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_GRUPO_SOLICITACAO")
    private Long idGrupoSolicitacao;

    @Column(name = "ID_GRUPO_DEFINICAO", nullable = false)
    private Integer idGrupoDefinicao;

    @Column(name = "NOME_GRUPO", nullable = false, length = 150)
    private String nomeGrupo;

    @Column(name = "ESTADO", nullable = false, length = 40)
    private String estado;

    @Column(name = "PARAMETROS_ENTRADA", columnDefinition = "CLOB")
    private String parametrosEntrada;

    @Column(name = "DATA_INICIO")
    private LocalDateTime dataInicio;

    @Column(name = "DATA_FIM")
    private LocalDateTime dataFim;

    @Column(name = "RESULTADO_FINAL_JSON", columnDefinition = "CLOB")
    private String resultadoFinalJson;

    @Column(name = "USUARIO_DB", length = 100)
    private String usuarioDb;

    @Column(name = "CORRELATION_ID", length = 100)
    private String correlationId;

    public RastroValidacao() {}

    public Long getIdGrupoSolicitacao() { return idGrupoSolicitacao; }
    public void setIdGrupoSolicitacao(Long idGrupoSolicitacao) { this.idGrupoSolicitacao = idGrupoSolicitacao; }
    public Integer getIdGrupoDefinicao() { return idGrupoDefinicao; }
    public void setIdGrupoDefinicao(Integer idGrupoDefinicao) { this.idGrupoDefinicao = idGrupoDefinicao; }
    public String getNomeGrupo() { return nomeGrupo; }
    public void setNomeGrupo(String nomeGrupo) { this.nomeGrupo = nomeGrupo; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getParametrosEntrada() { return parametrosEntrada; }
    public void setParametrosEntrada(String parametrosEntrada) { this.parametrosEntrada = parametrosEntrada; }
    public LocalDateTime getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDateTime dataInicio) { this.dataInicio = dataInicio; }
    public LocalDateTime getDataFim() { return dataFim; }
    public void setDataFim(LocalDateTime dataFim) { this.dataFim = dataFim; }
    public String getResultadoFinalJson() { return resultadoFinalJson; }
    public void setResultadoFinalJson(String resultadoFinalJson) { this.resultadoFinalJson = resultadoFinalJson; }
    public String getUsuarioDb() { return usuarioDb; }
    public void setUsuarioDb(String usuarioDb) { this.usuarioDb = usuarioDb; }
    public String getCorrelationId() { return correlationId; }
    public void setCorrelationId(String correlationId) { this.correlationId = correlationId; }
}
