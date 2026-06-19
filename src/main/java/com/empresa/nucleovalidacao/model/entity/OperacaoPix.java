package com.empresa.nucleovalidacao.model.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "OPERACOES_PIX", schema = "BANK_CORE")
public class OperacaoPix {

    @Id
    @Column(name = "ID_OPERACAO", length = 100)
    private String idOperacao;

    @Column(name = "AGENCIA_ORIGEM", length = 10)
    private String agenciaOrigem;

    @Column(name = "CONTA_ORIGEM", length = 20)
    private String contaOrigem;

    @Column(name = "CHAVE_DESTINO", length = 150)
    private String chaveDestino;

    @Column(name = "VALOR", precision = 15, scale = 2)
    private BigDecimal valor;

    @Column(name = "STATUS", length = 30)
    private String status;

    @Column(name = "DATA_CRIACAO")
    private LocalDateTime dataCriacao;

    public OperacaoPix() {}

    public String getIdOperacao() { return idOperacao; }
    public void setIdOperacao(String idOperacao) { this.idOperacao = idOperacao; }
    public String getAgenciaOrigem() { return agenciaOrigem; }
    public void setAgenciaOrigem(String agenciaOrigem) { this.agenciaOrigem = agenciaOrigem; }
    public String getContaOrigem() { return contaOrigem; }
    public void setContaOrigem(String contaOrigem) { this.contaOrigem = contaOrigem; }
    public String getChaveDestino() { return chaveDestino; }
    public void setChaveDestino(String chaveDestino) { this.chaveDestino = chaveDestino; }
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }
}
