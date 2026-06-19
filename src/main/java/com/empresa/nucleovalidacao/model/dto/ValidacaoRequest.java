package com.empresa.nucleovalidacao.model.dto;

import java.util.List;

public record ValidacaoRequest(
        Integer idGrupoValidacao,
        List<ParametroEntradaDTO> parametros
) {
}
