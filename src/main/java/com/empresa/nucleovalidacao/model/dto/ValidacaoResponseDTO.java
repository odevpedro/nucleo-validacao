package com.empresa.nucleovalidacao.model.dto;

import java.util.List;

public record ValidacaoResponseDTO(
        Long idGrupoSolicitacao,
        Integer idGrupoValidacao,
        String nomeGrupoValidacao,
        String estadoGrupo,
        String resultadoNegocioGrupo,
        String mensagemGrupoValidacao,
        String correlationId,
        List<ValidacaoResultadoDTO> validacoes
) {
}
