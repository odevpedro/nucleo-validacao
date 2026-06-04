package com.empresa.grupoconsistencias.model.dto;

import java.util.List;

public record ConsistenciaResultadoDTO(
        Integer idConsistencia,
        String nomeConsistencia,
        String procedureRef,
        String tipo,
        String estadoTecnico,
        String resultadoNegocio,
        Long tempoMs,
        List<String> mensagens,
        Object payload
) {
}
