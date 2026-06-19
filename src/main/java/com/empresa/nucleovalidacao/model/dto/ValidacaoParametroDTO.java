package com.empresa.nucleovalidacao.model.dto;

import java.math.BigDecimal;
import java.util.List;

public record ValidacaoParametroDTO(
        BigDecimal min,
        BigDecimal max,
        String regex,
        List<String> valoresPermitidos
) {
}
