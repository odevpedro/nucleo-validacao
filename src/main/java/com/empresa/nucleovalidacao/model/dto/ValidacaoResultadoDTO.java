package com.empresa.nucleovalidacao.model.dto;

import java.util.List;

public record ValidacaoResultadoDTO(
        Integer idValidacao,
        String nomeValidacao,
        String procedureRef,
        String tipo,
        String estadoTecnico,
        String resultadoNegocio,
        Long tempoMs,
        List<String> mensagens,
        Object payload
) {
}
