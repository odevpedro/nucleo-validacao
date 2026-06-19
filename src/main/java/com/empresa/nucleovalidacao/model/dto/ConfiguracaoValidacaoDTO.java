package com.empresa.nucleovalidacao.model.dto;

import java.util.Map;

public record ConfiguracaoValidacaoDTO(
        Map<Integer, GrupoValidacaoDTO> validacao
) {
}
