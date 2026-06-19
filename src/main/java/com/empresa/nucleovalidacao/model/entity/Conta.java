package com.empresa.nucleovalidacao.model.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "CONTAS", schema = "BANK_CORE")
@IdClass(ContaId.class)
public class Conta {

    @Id
    @Column(name = "AGENCIA", length = 10)
    private String agencia;

    @Id
    @Column(name = "CONTA", length = 20)
    private String conta;

    @Column(name = "COD_CLI", nullable = false)
    private Integer codCli;

    @Column(name = "SALDO_DISPONIVEL", precision = 15, scale = 2)
    private BigDecimal saldoDisponivel;

    @Column(name = "STATUS", length = 30)
    private String status;

    @Column(name = "LIMITE_PIX_DIARIO", precision = 15, scale = 2)
    private BigDecimal limitePixDiario;

    public Conta() {}

    public String getAgencia() { return agencia; }
    public void setAgencia(String agencia) { this.agencia = agencia; }
    public String getConta() { return conta; }
    public void setConta(String conta) { this.conta = conta; }
    public Integer getCodCli() { return codCli; }
    public void setCodCli(Integer codCli) { this.codCli = codCli; }
    public BigDecimal getSaldoDisponivel() { return saldoDisponivel; }
    public void setSaldoDisponivel(BigDecimal saldoDisponivel) { this.saldoDisponivel = saldoDisponivel; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public BigDecimal getLimitePixDiario() { return limitePixDiario; }
    public void setLimitePixDiario(BigDecimal limitePixDiario) { this.limitePixDiario = limitePixDiario; }
}
