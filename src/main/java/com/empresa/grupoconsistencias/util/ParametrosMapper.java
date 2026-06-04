package com.empresa.grupoconsistencias.util;

import com.empresa.grupoconsistencias.model.dto.ParametroEntradaDTO;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ParametrosMapper {

    private ParametrosMapper() {
    }

    public static Map<String, Object> toMap(List<ParametroEntradaDTO> parametros) {
        var map = new LinkedHashMap<String, Object>();
        if (parametros != null) {
            parametros.forEach(p -> map.put(p.nome(), p.valor()));
        }
        return map;
    }
}
