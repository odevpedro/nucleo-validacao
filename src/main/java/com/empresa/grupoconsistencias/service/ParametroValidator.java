package com.empresa.grupoconsistencias.service;

import com.empresa.grupoconsistencias.model.dto.ErroValidacaoDTO;
import com.empresa.grupoconsistencias.model.dto.GrupoDefinicaoDTO;
import com.empresa.grupoconsistencias.model.dto.ParametroDefinicaoDTO;
import com.empresa.grupoconsistencias.model.estado.TipoParametro;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Component
public class ParametroValidator {

    public List<ErroValidacaoDTO> validar(GrupoDefinicaoDTO grupo, Map<String, Object> parametros) {
        var erros = new ArrayList<ErroValidacaoDTO>();

        if (grupo.parametrosEntrada() == null) {
            return erros;
        }

        for (var def : grupo.parametrosEntrada()) {
            var nome = def.nome();
            var valor = parametros.get(nome);

            if (Boolean.TRUE.equals(def.obrigatorio()) && (valor == null || (valor instanceof String s && s.isBlank()))) {
                erros.add(new ErroValidacaoDTO(nome,
                        "Parâmetro obrigatório não informado", valor));
                continue;
            }

            if (valor == null) continue;

            try {
                validarTipo(nome, valor, def.tipo());
                validarRegras(nome, valor, def, erros);
            } catch (Exception e) {
                erros.add(new ErroValidacaoDTO(nome,
                        "Erro de validação: " + e.getMessage(), valor));
            }
        }

        return erros;
    }

    private void validarTipo(String nome, Object valor, TipoParametro tipoEsperado) {
        if (tipoEsperado == null) return;

        switch (tipoEsperado) {
            case INTEGER -> {
                if (valor instanceof Number) return;
                if (valor instanceof String s) { Integer.parseInt(s); return; }
                throw new IllegalArgumentException("Valor não é um inteiro válido");
            }
            case LONG -> {
                if (valor instanceof Number) return;
                if (valor instanceof String s) { Long.parseLong(s); return; }
                throw new IllegalArgumentException("Valor não é um long válido");
            }
            case BIGDECIMAL -> {
                if (valor instanceof Number) return;
                if (valor instanceof String s) { new BigDecimal(s); return; }
                throw new IllegalArgumentException("Valor não é um decimal válido");
            }
            case LOCALDATE -> {
                if (valor instanceof String s) {
                    try {
                        LocalDate.parse(s, DateTimeFormatter.ISO_LOCAL_DATE);
                    } catch (DateTimeParseException e) {
                        throw new IllegalArgumentException("Valor não é uma data válida (yyyy-MM-dd)");
                    }
                }
            }
            case BOOLEAN -> {
                if (valor instanceof Boolean) return;
                if (valor instanceof String s) {
                    if (!"true".equalsIgnoreCase(s) && !"false".equalsIgnoreCase(s)) {
                        throw new IllegalArgumentException("Valor não é um booleano válido");
                    }
                    return;
                }
                throw new IllegalArgumentException("Valor não é um booleano válido");
            }
            case STRING -> {
                if (!(valor instanceof String)) {
                    throw new IllegalArgumentException("Valor não é uma string válida");
                }
            }
        }
    }

    private void validarRegras(String nome, Object valor, ParametroDefinicaoDTO def,
                                List<ErroValidacaoDTO> erros) {
        var validacao = def.validacao();
        if (validacao == null) return;

        if (validacao.min() != null || validacao.max() != null) {
            BigDecimal num;
            if (valor instanceof Number n) {
                num = BigDecimal.valueOf(n.doubleValue());
            } else {
                try {
                    num = new BigDecimal(valor.toString());
                } catch (Exception e) {
                    return;
                }
            }

            if (validacao.min() != null && num.compareTo(validacao.min()) < 0) {
                erros.add(new ErroValidacaoDTO(nome,
                        "Valor mínimo: " + validacao.min(), valor));
            }
            if (validacao.max() != null && num.compareTo(validacao.max()) > 0) {
                erros.add(new ErroValidacaoDTO(nome,
                        "Valor máximo: " + validacao.max(), valor));
            }
        }

        if (validacao.regex() != null && valor instanceof String s) {
            if (!Pattern.matches(validacao.regex(), s)) {
                erros.add(new ErroValidacaoDTO(nome,
                        "Valor não corresponde ao padrão esperado", valor));
            }
        }

        if (validacao.valoresPermitidos() != null && !validacao.valoresPermitidos().isEmpty()) {
            var strValor = valor.toString();
            if (validacao.valoresPermitidos().stream().noneMatch(v -> v.equals(strValor))) {
                erros.add(new ErroValidacaoDTO(nome,
                        "Valor não permitido. Valores aceitos: " + validacao.valoresPermitidos(), valor));
            }
        }
    }
}
