package com.empresa.grupoconsistencias.model.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "CLIENTES", schema = "BANK_CORE")
public class Cliente {

    @Id
    @Column(name = "COD_CLI")
    private Integer codCli;

    @Column(name = "CPF_CNPJ", nullable = false, length = 20)
    private String cpfCnpj;

    @Column(name = "NOME", nullable = false, length = 150)
    private String nome;

    @Column(name = "SCORE_INTERNO")
    private Integer scoreInterno;

    @Column(name = "STATUS_CADASTRAL", length = 30)
    private String statusCadastral;

    @Column(name = "RENDA_COMPROVADA", precision = 15, scale = 2)
    private BigDecimal rendaComprovada;

    @Column(name = "PERFIL_INVESTIDOR", length = 30)
    private String perfilInvestidor;

    @Column(name = "DATA_CADASTRO")
    private LocalDate dataCadastro;

    public Cliente() {}

    public Integer getCodCli() { return codCli; }
    public void setCodCli(Integer codCli) { this.codCli = codCli; }
    public String getCpfCnpj() { return cpfCnpj; }
    public void setCpfCnpj(String cpfCnpj) { this.cpfCnpj = cpfCnpj; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public Integer getScoreInterno() { return scoreInterno; }
    public void setScoreInterno(Integer scoreInterno) { this.scoreInterno = scoreInterno; }
    public String getStatusCadastral() { return statusCadastral; }
    public void setStatusCadastral(String statusCadastral) { this.statusCadastral = statusCadastral; }
    public BigDecimal getRendaComprovada() { return rendaComprovada; }
    public void setRendaComprovada(BigDecimal rendaComprovada) { this.rendaComprovada = rendaComprovada; }
    public String getPerfilInvestidor() { return perfilInvestidor; }
    public void setPerfilInvestidor(String perfilInvestidor) { this.perfilInvestidor = perfilInvestidor; }
    public LocalDate getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(LocalDate dataCadastro) { this.dataCadastro = dataCadastro; }
}
