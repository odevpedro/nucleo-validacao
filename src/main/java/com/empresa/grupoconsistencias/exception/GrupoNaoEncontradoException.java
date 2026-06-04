package com.empresa.grupoconsistencias.exception;

public class GrupoNaoEncontradoException extends RuntimeException {
    private final Integer idGrupoConsistencia;

    public GrupoNaoEncontradoException(Integer idGrupoConsistencia) {
        super("Grupo de consistência não encontrado: " + idGrupoConsistencia);
        this.idGrupoConsistencia = idGrupoConsistencia;
    }

    public Integer getIdGrupoConsistencia() {
        return idGrupoConsistencia;
    }
}
