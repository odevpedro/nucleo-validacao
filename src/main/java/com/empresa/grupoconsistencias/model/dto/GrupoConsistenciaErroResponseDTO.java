package com.empresa.grupoconsistencias.model.dto;

import java.util.List;

public record GrupoConsistenciaErroResponseDTO(
        Integer idGrupoConsistencia,
        String nomeGrupoConsistencia,
        String estadoGrupo,
        String mensagem,
        String correlationId,
        List<ErroValidacaoDTO> erros
) {
}
