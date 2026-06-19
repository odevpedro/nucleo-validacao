package com.empresa.nucleovalidacao.service;

import com.empresa.nucleovalidacao.model.dto.ValidacaoResultadoDTO;
import com.empresa.nucleovalidacao.model.dto.GrupoValidacaoDTO;
import com.empresa.nucleovalidacao.model.estado.EstadoExecucao;
import com.empresa.nucleovalidacao.model.estado.EstadoValidacao;
import com.empresa.nucleovalidacao.model.estado.ResultadoNegocio;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ResultadoValidacaoConsolidador {

    public record ResultadoConsolidado(
            EstadoValidacao estadoGrupo,
            ResultadoNegocio resultadoNegocioGrupo,
            String mensagem
    ) {
    }

    public ResultadoConsolidado consolidar(GrupoValidacaoDTO grupo, List<ValidacaoResultadoDTO> resultados) {
        if (resultados.isEmpty()) {
            return new ResultadoConsolidado(
                    EstadoValidacao.FALHA_CRITICA, ResultadoNegocio.INCONCLUSIVO,
                    "Nenhuma validação foi executada"
            );
        }

        boolean temFalhaAutorizacao = resultados.stream()
                .anyMatch(r -> EstadoExecucao.FALHA_AUTORIZACAO.name().equals(r.estadoTecnico()));

        if (temFalhaAutorizacao) {
            return new ResultadoConsolidado(
                    EstadoValidacao.FALHA_AUTORIZACAO, ResultadoNegocio.INCONCLUSIVO,
                    "Falha de autorização detectada em uma ou mais validações"
            );
        }

        boolean temFalhaExecucaoOuTimeout = resultados.stream()
                .anyMatch(r -> EstadoExecucao.FALHA_EXECUCAO.name().equals(r.estadoTecnico())
                        || EstadoExecucao.TIMEOUT.name().equals(r.estadoTecnico()));

        var idsObrigatorias = grupo.validacoes().stream()
                .filter(c -> Boolean.TRUE.equals(c.obrigatoriaParaAprovacao()))
                .map(c -> c.id())
                .toList();

        boolean obrigatoriaReprovada = resultados.stream()
                .filter(r -> idsObrigatorias.contains(r.idValidacao()))
                .anyMatch(r -> ResultadoNegocio.REPROVADO.name().equals(r.resultadoNegocio()));

        if (obrigatoriaReprovada) {
            return new ResultadoConsolidado(
                    EstadoValidacao.FINALIZADO_SUCESSO, ResultadoNegocio.REPROVADO,
                    "Validação obrigatória reprovou"
            );
        }

        boolean obrigatoriaAlerta = resultados.stream()
                .filter(r -> idsObrigatorias.contains(r.idValidacao()))
                .anyMatch(r -> ResultadoNegocio.ALERTA.name().equals(r.resultadoNegocio()));

        if (obrigatoriaAlerta) {
            return new ResultadoConsolidado(
                    EstadoValidacao.FINALIZADO_SUCESSO, ResultadoNegocio.ALERTA,
                    "Validação obrigatória gerou alerta"
            );
        }

        if (temFalhaExecucaoOuTimeout) {
            return new ResultadoConsolidado(
                    EstadoValidacao.FINALIZADO_PARCIAL, ResultadoNegocio.INCONCLUSIVO,
                    "Grupo executado parcialmente. Uma ou mais validações falharam."
            );
        }

        return new ResultadoConsolidado(
                EstadoValidacao.FINALIZADO_SUCESSO, ResultadoNegocio.APROVADO,
                "Grupo de validações executado com sucesso"
        );
    }
}
