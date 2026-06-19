package com.empresa.nucleovalidacao.model.dto;

import java.util.List;

public record GrupoValidacaoDTO(
        String nome,
        String descricao,
        Boolean ativo,
        List<ParametroDefinicaoDTO> parametrosEntrada,
        List<ValidacaoDefinicaoDTO> validacoes
) {
}
