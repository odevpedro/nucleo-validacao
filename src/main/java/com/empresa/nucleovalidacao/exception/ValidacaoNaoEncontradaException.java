package com.empresa.nucleovalidacao.exception;

public class ValidacaoNaoEncontradaException extends RuntimeException {
    private final Integer idGrupoValidacao;

    public ValidacaoNaoEncontradaException(Integer idGrupoValidacao) {
        super("Grupo de validação não encontrado: " + idGrupoValidacao);
        this.idGrupoValidacao = idGrupoValidacao;
    }

    public Integer getIdGrupoValidacao() {
        return idGrupoValidacao;
    }
}
