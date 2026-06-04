package com.empresa.grupoconsistencias.model.dto;

import java.math.BigDecimal;

public record AlertaSegurancaDTO(
        Integer idGrupoConsistencia,
        String nomeGrupoConsistencia,
        BigDecimal percentualFalhaAutorizacao,
        Long totalExecucoes,
        String mensagem
) {
}
