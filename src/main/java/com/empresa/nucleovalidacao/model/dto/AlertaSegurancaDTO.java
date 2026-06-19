package com.empresa.nucleovalidacao.model.dto;

import java.math.BigDecimal;

public record AlertaSegurancaDTO(
        Integer idGrupoValidacao,
        String nomeGrupoValidacao,
        BigDecimal percentualFalhaAutorizacao,
        Long totalExecucoes,
        String mensagem
) {
}
