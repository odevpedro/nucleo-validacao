package com.empresa.nucleovalidacao.controller;

import com.empresa.nucleovalidacao.model.dto.AlertaSegurancaDTO;
import com.empresa.nucleovalidacao.service.ComplianceAlertService;
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
