package com.empresa.grupoconsistencias.model.dto;

import com.empresa.grupoconsistencias.model.estado.TipoConsistencia;
import java.util.List;

public record ConsistenciaDefinicaoDTO(
        Integer id,
        String nome,
        String descricao,
        String procedureRef,
        TipoConsistencia tipo,
        Integer timeoutMs,
        Boolean obrigatoriaParaAprovacao,
        Boolean abortarGrupoEmFalha,
        List<ParametroBindingDTO> parametros
) {
}
