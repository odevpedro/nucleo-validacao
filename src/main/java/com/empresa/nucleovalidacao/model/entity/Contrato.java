package com.empresa.nucleovalidacao.model.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "CONTRATOS", schema = "BANK_CORE")
public class Contrato {

    @Id
    @Column(name = "CONTRATO_ID")
    private Long contratoId;

    @Column(name = "COD_CLI", nullable = false)
    private Integer codCli;

    @Column(name = "TIPO_OPERACAO", length = 50)
    private String tipoOperacao;

    @Column(name = "STATUS", length = 30)
    private String status;

    @Column(name = "VALOR_TOTAL", precision = 15, scale = 2)
    private BigDecimal valorTotal;

    @Column(name = "QTD_PARCELAS")
    private Integer qtdParcelas;

    @Column(name = "DIAS_ATRASO")
    private Integer diasAtraso;

    public Contrato() {}

    public Long getContratoId() { return contratoId; }
    public void setContratoId(Long contratoId) { this.contratoId = contratoId; }
    public Integer getCodCli() { return codCli; }
    public void setCodCli(Integer codCli) { this.codCli = codCli; }
    public String getTipoOperacao() { return tipoOperacao; }
    public void setTipoOperacao(String tipoOperacao) { this.tipoOperacao = tipoOperacao; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public BigDecimal getValorTotal() { return valorTotal; }
    public void setValorTotal(BigDecimal valorTotal) { this.valorTotal = valorTotal; }
    public Integer getQtdParcelas() { return qtdParcelas; }
    public void setQtdParcelas(Integer qtdParcelas) { this.qtdParcelas = qtdParcelas; }
    public Integer getDiasAtraso() { return diasAtraso; }
    public void setDiasAtraso(Integer diasAtraso) { this.diasAtraso = diasAtraso; }
}
