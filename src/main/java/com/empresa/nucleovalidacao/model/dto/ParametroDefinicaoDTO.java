package com.empresa.nucleovalidacao.model.dto;

import com.empresa.nucleovalidacao.model.estado.TipoParametro;

public record ParametroDefinicaoDTO(
        String nome,
        TipoParametro tipo,
        Boolean obrigatorio,
        String descricao,
        ValidacaoParametroDTO validacao
) {
}
