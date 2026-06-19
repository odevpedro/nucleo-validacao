package com.empresa.nucleovalidacao.model.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "CHAVES_PIX", schema = "BANK_CORE")
public class ChavePix {

    @Id
    @Column(name = "CHAVE", length = 150)
    private String chave;

    @Column(name = "TIPO", length = 30)
    private String tipo;

    @Column(name = "COD_CLI", nullable = false)
    private Integer codCli;

    @Column(name = "STATUS", length = 30)
    private String status;

    public ChavePix() {}

    public String getChave() { return chave; }
    public void setChave(String chave) { this.chave = chave; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public Integer getCodCli() { return codCli; }
    public void setCodCli(Integer codCli) { this.codCli = codCli; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
