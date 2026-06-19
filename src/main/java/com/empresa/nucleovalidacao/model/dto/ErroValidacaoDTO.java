package com.empresa.nucleovalidacao.model.dto;

public record ErroValidacaoDTO(
        String campo,
        String mensagem,
        Object valorRecebido
) {
}
