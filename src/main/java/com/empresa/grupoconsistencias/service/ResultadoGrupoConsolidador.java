package com.empresa.grupoconsistencias.service;

import com.empresa.grupoconsistencias.model.dto.ConsistenciaResultadoDTO;
import com.empresa.grupoconsistencias.model.dto.GrupoDefinicaoDTO;
import com.empresa.grupoconsistencias.model.estado.EstadoConsistencia;
import com.empresa.grupoconsistencias.model.estado.EstadoGrupo;
import com.empresa.grupoconsistencias.model.estado.ResultadoNegocio;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ResultadoGrupoConsolidador {

    public record ResultadoConsolidado(
            EstadoGrupo estadoGrupo,
            ResultadoNegocio resultadoNegocioGrupo,
            String mensagem
    ) {
    }

    public ResultadoConsolidado consolidar(GrupoDefinicaoDTO grupo, List<ConsistenciaResultadoDTO> resultados) {
        if (resultados.isEmpty()) {
            return new ResultadoConsolidado(
                    EstadoGrupo.FALHA_CRITICA, ResultadoNegocio.INCONCLUSIVO,
                    "Nenhuma consistência foi executada"
            );
        }

        boolean temFalhaAutorizacao = resultados.stream()
                .anyMatch(r -> EstadoConsistencia.FALHA_AUTORIZACAO.name().equals(r.estadoTecnico()));

        if (temFalhaAutorizacao) {
            return new ResultadoConsolidado(
                    EstadoGrupo.FALHA_AUTORIZACAO, ResultadoNegocio.INCONCLUSIVO,
                    "Falha de autorização detectada em uma ou mais consistências"
            );
        }

        boolean temFalhaExecucaoOuTimeout = resultados.stream()
                .anyMatch(r -> EstadoConsistencia.FALHA_EXECUCAO.name().equals(r.estadoTecnico())
                        || EstadoConsistencia.TIMEOUT.name().equals(r.estadoTecnico()));

        var idsObrigatorias = grupo.consistencias().stream()
                .filter(c -> Boolean.TRUE.equals(c.obrigatoriaParaAprovacao()))
                .map(c -> c.id())
                .toList();

        boolean obrigatoriaReprovada = resultados.stream()
                .filter(r -> idsObrigatorias.contains(r.idConsistencia()))
                .anyMatch(r -> ResultadoNegocio.REPROVADO.name().equals(r.resultadoNegocio()));

        if (obrigatoriaReprovada) {
            return new ResultadoConsolidado(
                    EstadoGrupo.FINALIZADO_SUCESSO, ResultadoNegocio.REPROVADO,
                    "Consistência obrigatória reprovou"
            );
        }

        boolean obrigatoriaAlerta = resultados.stream()
                .filter(r -> idsObrigatorias.contains(r.idConsistencia()))
                .anyMatch(r -> ResultadoNegocio.ALERTA.name().equals(r.resultadoNegocio()));

        if (obrigatoriaAlerta) {
            return new ResultadoConsolidado(
                    EstadoGrupo.FINALIZADO_SUCESSO, ResultadoNegocio.ALERTA,
                    "Consistência obrigatória gerou alerta"
            );
        }

        if (temFalhaExecucaoOuTimeout) {
            return new ResultadoConsolidado(
                    EstadoGrupo.FINALIZADO_PARCIAL, ResultadoNegocio.INCONCLUSIVO,
                    "Grupo executado parcialmente. Uma ou mais consistências falharam."
            );
        }

        return new ResultadoConsolidado(
                EstadoGrupo.FINALIZADO_SUCESSO, ResultadoNegocio.APROVADO,
                "Grupo de consistências executado com sucesso"
        );
    }
}
