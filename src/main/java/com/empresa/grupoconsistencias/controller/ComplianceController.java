package com.empresa.grupoconsistencias.controller;

import com.empresa.grupoconsistencias.model.dto.AlertaSegurancaDTO;
import com.empresa.grupoconsistencias.service.ComplianceAlertService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/compliance")
public class ComplianceController {

    private final ComplianceAlertService complianceAlertService;

    public ComplianceController(ComplianceAlertService complianceAlertService) {
        this.complianceAlertService = complianceAlertService;
    }

    @GetMapping("/alertas")
    public ResponseEntity<List<AlertaSegurancaDTO>> listarAlertas() {
        return ResponseEntity.ok(complianceAlertService.getUltimosAlertas());
    }
}
