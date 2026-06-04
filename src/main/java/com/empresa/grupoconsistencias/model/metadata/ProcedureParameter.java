package com.empresa.grupoconsistencias.model.metadata;

public record ProcedureParameter(
        String name,
        Integer jdbcType,
        String typeName,
        Integer position,
        ParameterMode mode
) {
}
