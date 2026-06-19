package com.empresa.nucleovalidacao.model.dto;

import java.util.List;

public record ValidacaoErroResponseDTO(
        Integer idGrupoValidacao,
        String nomeGrupoValidacao,
        String estadoGrupo,
        String mensagem,
        String correlationId,
        List<ErroValidacaoDTO> erros
) {
}
