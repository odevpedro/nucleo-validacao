package com.empresa.grupoconsistencias.model.metadata;

import java.util.List;

public record ProcedureSignature(
        String schema,
        String packageName,
        String procedureName,
        String procedureRef,
        List<ProcedureParameter> parameters
) {
}
