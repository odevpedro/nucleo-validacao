package com.empresa.grupoconsistencias.model.estado;

public enum EstadoGrupo {
    CRIADO,
    VALIDANDO,
    EXECUTANDO,
    FINALIZADO_SUCESSO,
    FINALIZADO_PARCIAL,
    FALHA_VALIDACAO,
    FALHA_CRITICA,
    FALHA_AUTORIZACAO,
    CANCELADO
}
