package com.empresa.grupoconsistencias.service;

import com.empresa.grupoconsistencias.model.dto.*;
import com.empresa.grupoconsistencias.model.estado.TipoConsistencia;
import com.empresa.grupoconsistencias.model.estado.TipoParametro;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ParametroValidatorTest {

    private ParametroValidator validator;
    private GrupoDefinicaoDTO grupo;

    @BeforeEach
    void setUp() {
        validator = new ParametroValidator();

        var params = List.of(
                new ParametroDefinicaoDTO("cod_cli", TipoParametro.INTEGER, true, "ID do cliente", null),
                new ParametroDefinicaoDTO("valor_solicitado", TipoParametro.BIGDECIMAL, true, "Valor",
                        new ValidacaoParametroDTO(new BigDecimal("100"), new BigDecimal("50000"), null, null)),
                new ParametroDefinicaoDTO("qtd_parcelas", TipoParametro.INTEGER, true, "Parcelas",
                        new ValidacaoParametroDTO(BigDecimal.ONE, new BigDecimal("96"), null, null)),
                new ParametroDefinicaoDTO("opcional", TipoParametro.STRING, false, "Opcional", null),
                new ParametroDefinicaoDTO("email", TipoParametro.STRING, true, "Email",
                        new ValidacaoParametroDTO(null, null, "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", null))
        );

        grupo = new GrupoDefinicaoDTO(
                "ANALISE_CREDITO_PESSOAL", "Teste", true, params,
                List.of(new ConsistenciaDefinicaoDTO(
                        201, "teste", "desc", "PK_TEST.PROC",
                        TipoConsistencia.LEITURA, 5000, true, false, List.of()
                ))
        );
    }

    private Map<String, Object> mapOf(Object... args) {
        var map = new java.util.LinkedHashMap<String, Object>();
        for (int i = 0; i < args.length; i += 2) {
            map.put((String) args[i], args[i + 1]);
        }
        return map;
    }

    @Test
    void validarSucesso() {
        var params = mapOf(
                "cod_cli", 12345,
                "valor_solicitado", new BigDecimal("10000"),
                "qtd_parcelas", 24,
                "email", "teste@email.com"
        );

        var erros = validator.validar(grupo, params);
        assertThat(erros).isEmpty();
    }

    @Test
    void parametroObrigatorioAusente() {
        var params = mapOf(
                "cod_cli", 12345,
                "qtd_parcelas", 24
        );

        var erros = validator.validar(grupo, params);
        assertThat(erros).anyMatch(e -> "valor_solicitado".equals(e.campo()));
    }

    @Test
    void tipoInvalido() {
        var params = mapOf(
                "cod_cli", 12345,
                "valor_solicitado", new BigDecimal("10000"),
                "qtd_parcelas", "vinte e quatro"
        );

        var erros = validator.validar(grupo, params);
        assertThat(erros).anyMatch(e -> "qtd_parcelas".equals(e.campo()));
    }

    @Test
    void valorAbaixoMinimo() {
        var params = mapOf(
                "cod_cli", 12345,
                "valor_solicitado", new BigDecimal("50"),
                "qtd_parcelas", 24
        );

        var erros = validator.validar(grupo, params);
        assertThat(erros).anyMatch(e -> "valor_solicitado".equals(e.campo()));
    }

    @Test
    void regexInvalido() {
        var params = mapOf(
                "cod_cli", 12345,
                "valor_solicitado", new BigDecimal("10000"),
                "qtd_parcelas", 24,
                "email", "email-invalido"
        );

        var erros = validator.validar(grupo, params);
        assertThat(erros).anyMatch(e -> "email".equals(e.campo()));
    }

    @Test
    void valoresPermitidos() {
        var paramsComPermitidos = List.of(
                new ParametroDefinicaoDTO("tipo", TipoParametro.STRING, true, "Tipo",
                        new ValidacaoParametroDTO(null, null, null, List.of("A", "B", "C")))
        );
        var grupoComPermitidos = new GrupoDefinicaoDTO(
                "TESTE", "Teste", true, paramsComPermitidos,
                List.of(new ConsistenciaDefinicaoDTO(
                        99, "test", "desc", "PK_TEST.PROC",
                        TipoConsistencia.LEITURA, 5000, true, false, List.of()
                ))
        );

        var errosOk = validator.validar(grupoComPermitidos, mapOf("tipo", "A"));
        assertThat(errosOk).isEmpty();

        var errosFail = validator.validar(grupoComPermitidos, mapOf("tipo", "Z"));
        assertThat(errosFail).anyMatch(e -> "tipo".equals(e.campo()));
    }

    @Test
    void parametroOpcionalNaoExigeValidacao() {
        var params = mapOf(
                "cod_cli", 12345,
                "valor_solicitado", new BigDecimal("10000"),
                "qtd_parcelas", 24,
                "email", "teste@email.com"
        );

        var erros = validator.validar(grupo, params);
        assertThat(erros).noneMatch(e -> "opcional".equals(e.campo()));
    }
}
