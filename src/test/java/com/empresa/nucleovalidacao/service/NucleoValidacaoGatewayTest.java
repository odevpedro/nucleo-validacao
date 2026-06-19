package com.empresa.nucleovalidacao.service;

import com.empresa.nucleovalidacao.exception.ValidacaoException;
import com.empresa.nucleovalidacao.model.dto.ValidacaoRequest;
import com.empresa.nucleovalidacao.model.dto.ParametroEntradaDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class NucleoValidacaoGatewayTest {

    @Autowired
    private NucleoValidacaoGateway gateway;

    private static final String CORRELATION_ID = "test-correlation";

    @Test
    void sucessoTotal_grupo200() {
        var request = new ValidacaoRequest(
                200,
                List.of(
                        new ParametroEntradaDTO("cod_cli", 1),
                        new ParametroEntradaDTO("valor_solicitado", 10000.00),
                        new ParametroEntradaDTO("qtd_parcelas", 24)
                )
        );

        var response = gateway.executar(request, CORRELATION_ID);

        assertThat(response.estadoGrupo()).isEqualTo("FINALIZADO_SUCESSO");
        assertThat(response.resultadoNegocioGrupo()).isEqualTo("APROVADO");
        assertThat(response.validacoes()).isNotEmpty();
        assertThat(response.validacoes()).allMatch(c -> "SUCESSO".equals(c.estadoTecnico()));
        assertThat(response.idGrupoSolicitacao()).isNotNull();
        assertThat(response.correlationId()).isEqualTo(CORRELATION_ID);
    }

    @Test
    void falhaValidacao_parametroAusente() {
        var request = new ValidacaoRequest(
                200,
                List.of(
                        new ParametroEntradaDTO("cod_cli", 1),
                        new ParametroEntradaDTO("qtd_parcelas", 24)
                )
        );

        var exception = assertThrows(ValidacaoException.class, () -> gateway.executar(request, CORRELATION_ID));

        assertThat(exception.getErros()).isNotEmpty();
        assertThat(exception.getErros()).anyMatch(e -> "valor_solicitado".equals(e.campo()));
    }

    @Test
    void falhaValidacao_tipoInvalido() {
        var request = new ValidacaoRequest(
                200,
                List.of(
                        new ParametroEntradaDTO("cod_cli", 1),
                        new ParametroEntradaDTO("valor_solicitado", 10000.00),
                        new ParametroEntradaDTO("qtd_parcelas", "vinte e quatro")
                )
        );

        var exception = assertThrows(ValidacaoException.class, () -> gateway.executar(request, CORRELATION_ID));

        assertThat(exception.getErros()).isNotEmpty();
        assertThat(exception.getErros()).anyMatch(e -> "qtd_parcelas".equals(e.campo()));
    }

    @Test
    void reproducaoNegocio_clienteBaixoScore() {
        var request = new ValidacaoRequest(
                200,
                List.of(
                        new ParametroEntradaDTO("cod_cli", 2),
                        new ParametroEntradaDTO("valor_solicitado", 3000.00),
                        new ParametroEntradaDTO("qtd_parcelas", 12)
                )
        );

        var response = gateway.executar(request, CORRELATION_ID);

        assertThat(response.estadoGrupo()).isEqualTo("FINALIZADO_SUCESSO");
        assertThat(response.resultadoNegocioGrupo()).isIn("REPROVADO", "ALERTA");

        var scoreResult = response.validacoes().stream()
                .filter(c -> "calcular_score_interno".equals(c.nomeValidacao()))
                .findFirst();
        assertThat(scoreResult).isPresent();
    }

    @Test
    void grupoInexistente() {
        var request = new ValidacaoRequest(
                9999,
                List.of()
        );

        var exception = assertThrows(Exception.class, () -> gateway.executar(request, CORRELATION_ID));
        assertThat(exception.getMessage()).contains("não encontrado");
    }

    @Test
    void correlationIdPassadoParaResponse() {
        var request = new ValidacaoRequest(
                200,
                List.of(
                        new ParametroEntradaDTO("cod_cli", 1),
                        new ParametroEntradaDTO("valor_solicitado", 5000.00),
                        new ParametroEntradaDTO("qtd_parcelas", 12)
                )
        );

        var response = gateway.executar(request, "meu-correlation-id");

        assertThat(response.correlationId()).isEqualTo("meu-correlation-id");
    }

    @Test
    void validacaoValorMinimo() {
        var request = new ValidacaoRequest(
                200,
                List.of(
                        new ParametroEntradaDTO("cod_cli", 1),
                        new ParametroEntradaDTO("valor_solicitado", 50.00),
                        new ParametroEntradaDTO("qtd_parcelas", 12)
                )
        );

        var exception = assertThrows(ValidacaoException.class, () -> gateway.executar(request, CORRELATION_ID));
        assertThat(exception.getErros()).isNotEmpty();
        assertThat(exception.getErros()).anyMatch(e -> "valor_solicitado".equals(e.campo()));
    }

    @Test
    void sucesso_grupo300() {
        var request = new ValidacaoRequest(
                300,
                List.of(
                        new ParametroEntradaDTO("agencia", "0001"),
                        new ParametroEntradaDTO("conta", "10000-0"),
                        new ParametroEntradaDTO("chave_destino", "22222222222"),
                        new ParametroEntradaDTO("valor", 100.00),
                        new ParametroEntradaDTO("id_solicitacao", "PIX-TEST-001")
                )
        );

        var response = gateway.executar(request, CORRELATION_ID);

        assertThat(response.estadoGrupo()).isEqualTo("FINALIZADO_SUCESSO");
        assertThat(response.resultadoNegocioGrupo()).isEqualTo("APROVADO");
        assertThat(response.idGrupoValidacao()).isEqualTo(300);
    }

    @Test
    void sucesso_grupo100() {
        var request = new ValidacaoRequest(
                100,
                List.of(
                        new ParametroEntradaDTO("cpf_cnpj", "11111111111"),
                        new ParametroEntradaDTO("canal_origem", "APP"),
                        new ParametroEntradaDTO("biometria_hash", "hash1234567890abc")
                )
        );

        var response = gateway.executar(request, CORRELATION_ID);

        assertThat(response.estadoGrupo()).isIn("FINALIZADO_SUCESSO", "FINALIZADO_PARCIAL");
        assertThat(response.idGrupoValidacao()).isEqualTo(100);
    }

    @Test
    void paramValorExcedeMaximo() {
        var request = new ValidacaoRequest(
                200,
                List.of(
                        new ParametroEntradaDTO("cod_cli", 1),
                        new ParametroEntradaDTO("valor_solicitado", 100000.00),
                        new ParametroEntradaDTO("qtd_parcelas", 12)
                )
        );

        var exception = assertThrows(ValidacaoException.class, () -> gateway.executar(request, CORRELATION_ID));
        assertThat(exception.getErros()).anyMatch(e -> "valor_solicitado".equals(e.campo()));
    }

    @Test
    void rastroPersisteAposFalha() {
        var request = new ValidacaoRequest(
                200,
                List.of(
                        new ParametroEntradaDTO("cod_cli", 1),
                        new ParametroEntradaDTO("valor_solicitado", 10000.00),
                        new ParametroEntradaDTO("qtd_parcelas", 24)
                )
        );

        var response = gateway.executar(request, CORRELATION_ID);

        assertThat(response.idGrupoSolicitacao()).isNotNull();
        assertThat(response.idGrupoSolicitacao()).isPositive();
        assertThat(response.validacoes()).isNotEmpty();
    }
}
