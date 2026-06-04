package com.empresa.grupoconsistencias.model.estado;

public enum EstadoConsistencia {
    AGUARDANDO,
    EXECUTANDO,
    SUCESSO,
    FALHA_VALIDACAO,
    FALHA_EXECUCAO,
    FALHA_AUTORIZACAO,
    TIMEOUT
}
