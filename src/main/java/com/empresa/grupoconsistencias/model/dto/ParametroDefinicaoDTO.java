package com.empresa.grupoconsistencias.model.dto;

import com.empresa.grupoconsistencias.model.estado.TipoParametro;

public record ParametroDefinicaoDTO(
        String nome,
        TipoParametro tipo,
        Boolean obrigatorio,
        String descricao,
        ValidacaoParametroDTO validacao
) {
}
