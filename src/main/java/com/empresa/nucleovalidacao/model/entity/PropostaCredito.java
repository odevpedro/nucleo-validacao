package com.empresa.nucleovalidacao.model.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "PROPOSTAS_CREDITO", schema = "BANK_CORE")
public class PropostaCredito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PROPOSTA_ID")
    private Long propostaId;

    @Column(name = "COD_CLI", nullable = false)
    private Integer codCli;

    @Column(name = "VALOR_SOLICITADO", precision = 15, scale = 2)
    private BigDecimal valorSolicitado;

    @Column(name = "QTD_PARCELAS")
    private Integer qtdParcelas;

    @Column(name = "TAXA_MENSAL", precision = 10, scale = 4)
    private BigDecimal taxaMensal;

    @Column(name = "CET", precision = 10, scale = 4)
    private BigDecimal cet;

    @Column(name = "STATUS", length = 30)
    private String status;

    @Column(name = "DATA_CRIACAO")
    private LocalDateTime dataCriacao;

    public PropostaCredito() {}

    public Long getPropostaId() { return propostaId; }
    public void setPropostaId(Long propostaId) { this.propostaId = propostaId; }
    public Integer getCodCli() { return codCli; }
    public void setCodCli(Integer codCli) { this.codCli = codCli; }
    public BigDecimal getValorSolicitado() { return valorSolicitado; }
    public void setValorSolicitado(BigDecimal valorSolicitado) { this.valorSolicitado = valorSolicitado; }
    public Integer getQtdParcelas() { return qtdParcelas; }
    public void setQtdParcelas(Integer qtdParcelas) { this.qtdParcelas = qtdParcelas; }
    public BigDecimal getTaxaMensal() { return taxaMensal; }
    public void setTaxaMensal(BigDecimal taxaMensal) { this.taxaMensal = taxaMensal; }
    public BigDecimal getCet() { return cet; }
    public void setCet(BigDecimal cet) { this.cet = cet; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }
}
