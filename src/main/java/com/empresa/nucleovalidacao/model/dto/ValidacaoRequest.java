package com.empresa.nucleovalidacao.model.dto;

import java.util.List;

public record ValidacaoRequest(
        Integer idGrupoValidacao,
        String correlationId,
        List<ParametroEntradaDTO> parametros
) {
}
