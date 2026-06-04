package com.empresa.grupoconsistencias.model.dto;

import java.util.List;

public record GrupoConsistenciaResponseDTO(
        Long idGrupoSolicitacao,
        Integer idGrupoConsistencia,
        String nomeGrupoConsistencia,
        String estadoGrupo,
        String resultadoNegocioGrupo,
        String mensagemGrupoConsistencia,
        String correlationId,
        List<ConsistenciaResultadoDTO> consistencias
) {
}
