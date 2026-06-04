package com.empresa.grupoconsistencias.model.entity;

import java.io.Serializable;
import java.util.Objects;

public class ContaId implements Serializable {

    private String agencia;
    private String conta;

    public ContaId() {}

    public ContaId(String agencia, String conta) {
        this.agencia = agencia;
        this.conta = conta;
    }

    public String getAgencia() { return agencia; }
    public void setAgencia(String agencia) { this.agencia = agencia; }
    public String getConta() { return conta; }
    public void setConta(String conta) { this.conta = conta; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ContaId contaId)) return false;
        return Objects.equals(agencia, contaId.agencia) && Objects.equals(conta, contaId.conta);
    }

    @Override
    public int hashCode() {
        return Objects.hash(agencia, conta);
    }
}
