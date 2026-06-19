package com.empresa.nucleovalidacao.model.estado;

public enum EstadoExecucao {
    AGUARDANDO,
    EXECUTANDO,
    SUCESSO,
    FALHA_VALIDACAO,
    FALHA_EXECUCAO,
    FALHA_AUTORIZACAO,
    TIMEOUT
}
