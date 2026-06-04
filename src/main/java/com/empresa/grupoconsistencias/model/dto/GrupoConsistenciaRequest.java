package com.empresa.grupoconsistencias.model.dto;

import java.util.List;

public record GrupoConsistenciaRequest(
        Integer idGrupoConsistencia,
        String correlationId,
        List<ParametroEntradaDTO> parametros
) {
}
