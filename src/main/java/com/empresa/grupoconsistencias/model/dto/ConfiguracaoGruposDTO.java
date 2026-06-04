package com.empresa.grupoconsistencias.model.dto;

import java.util.Map;

public record ConfiguracaoGruposDTO(
        Map<Integer, GrupoDefinicaoDTO> grupos
) {
}
