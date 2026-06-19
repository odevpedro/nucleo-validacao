package com.empresa.nucleovalidacao.model.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "BOLETOS", schema = "BANK_CORE")
public class Boleto {

    @Id
    @Column(name = "NOSSO_NUMERO", length = 50)
    private String nossoNumero;

    @Column(name = "CPF_CNPJ_PAGADOR", length = 20)
    private String cpfCnpjPagador;

    @Column(name = "VALOR", precision = 15, scale = 2)
    private BigDecimal valor;

    @Column(name = "DATA_VENCIMENTO")
    private LocalDate dataVencimento;

    @Column(name = "STATUS", length = 30)
    private String status;

    public Boleto() {}

    public String getNossoNumero() { return nossoNumero; }
    public void setNossoNumero(String nossoNumero) { this.nossoNumero = nossoNumero; }
    public String getCpfCnpjPagador() { return cpfCnpjPagador; }
    public void setCpfCnpjPagador(String cpfCnpjPagador) { this.cpfCnpjPagador = cpfCnpjPagador; }
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
    public LocalDate getDataVencimento() { return dataVencimento; }
    public void setDataVencimento(LocalDate dataVencimento) { this.dataVencimento = dataVencimento; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
