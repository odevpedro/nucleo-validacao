package com.empresa.grupoconsistencias.model.dto;

import java.math.BigDecimal;
import java.util.List;

public record ValidacaoParametroDTO(
        BigDecimal min,
        BigDecimal max,
        String regex,
        List<String> valoresPermitidos
) {
}
