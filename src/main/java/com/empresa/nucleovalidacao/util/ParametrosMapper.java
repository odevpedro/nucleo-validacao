package com.empresa.nucleovalidacao.util;

import com.empresa.nucleovalidacao.model.dto.ParametroEntradaDTO;

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
