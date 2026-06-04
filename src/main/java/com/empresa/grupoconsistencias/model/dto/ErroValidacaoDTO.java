package com.empresa.grupoconsistencias.model.dto;

public record ErroValidacaoDTO(
        String campo,
        String mensagem,
        Object valorRecebido
) {
}
