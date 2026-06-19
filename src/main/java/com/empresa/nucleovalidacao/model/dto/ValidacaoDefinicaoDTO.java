package com.empresa.nucleovalidacao.model.dto;

import com.empresa.nucleovalidacao.model.estado.TipoValidacao;
import java.util.List;

public record ValidacaoDefinicaoDTO(
        Integer id,
        String nome,
        String descricao,
        String procedureRef,
        TipoValidacao tipo,
        Integer timeoutMs,
        Boolean obrigatoriaParaAprovacao,
        Boolean abortarGrupoEmFalha,
        List<ParametroBindingDTO> parametros
) {
}
