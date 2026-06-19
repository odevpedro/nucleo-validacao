package com.empresa.nucleovalidacao.model.estado;

public enum EstadoValidacao {
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
