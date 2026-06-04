package com.empresa.grupoconsistencias.model.dto;

import java.util.List;

public record GrupoDefinicaoDTO(
        String nome,
        String descricao,
        Boolean ativo,
        List<ParametroDefinicaoDTO> parametrosEntrada,
        List<ConsistenciaDefinicaoDTO> consistencias
) {
}
