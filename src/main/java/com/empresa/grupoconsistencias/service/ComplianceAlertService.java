package com.empresa.grupoconsistencias.service;

import com.empresa.grupoconsistencias.model.dto.AlertaSegurancaDTO;
import com.empresa.grupoconsistencias.repository.ComplianceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class ComplianceAlertService {

    private static final Logger log = LoggerFactory.getLogger(ComplianceAlertService.class);
    private static final BigDecimal LIMIAR_ALERTA = BigDecimal.valueOf(20.0);

    private final ComplianceRepository complianceRepository;
    private final List<AlertaSegurancaDTO> ultimosAlertas = new CopyOnWriteArrayList<>();

    public ComplianceAlertService(ComplianceRepository complianceRepository) {
        this.complianceRepository = complianceRepository;
    }

    @Scheduled(fixedRate = 900000)
    public void verificarAnomalias() {
        try {
            var falhas = complianceRepository.findFalhasAutorizacaoUltimos7Dias();
            ultimosAlertas.clear();

            for (var f : falhas) {
                var percentual = f.percentualFalha();
                if (percentual.compareTo(LIMIAR_ALERTA) > 0) {
                    var alerta = new AlertaSegurancaDTO(
                            f.idGrupoDefinicao(),
                            f.nomeGrupo(),
                            percentual,
                            f.totalExecucoes(),
                            "Anomalia de autorizacao detectada no grupo " + f.nomeGrupo()
                    );
                    ultimosAlertas.add(alerta);
                    log.warn("ALERTA SEGURANCA: {} - {}% de falhas de autorizacao",
                            f.nomeGrupo(), percentual);
                }
            }

            if (ultimosAlertas.isEmpty()) {
                log.debug("Nenhuma anomalia de autorizacao detectada");
            }
        } catch (Exception e) {
            log.error("Erro ao verificar anomalias de seguranca: {}", e.getMessage());
        }
    }

    public List<AlertaSegurancaDTO> getUltimosAlertas() {
        return List.copyOf(ultimosAlertas);
    }
}
