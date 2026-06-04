package com.empresa.grupoconsistencias.controller;

import com.empresa.grupoconsistencias.exception.ValidacaoException;
import com.empresa.grupoconsistencias.model.dto.GrupoConsistenciaErroResponseDTO;
import com.empresa.grupoconsistencias.model.dto.GrupoConsistenciaRequest;
import com.empresa.grupoconsistencias.model.dto.GrupoConsistenciaResponseDTO;
import com.empresa.grupoconsistencias.service.GrupoConsistenciaGateway;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/grupos-consistencia")
public class GrupoConsistenciaController {

    private final GrupoConsistenciaGateway gateway;

    public GrupoConsistenciaController(GrupoConsistenciaGateway gateway) {
        this.gateway = gateway;
    }

    @PostMapping("/executar")
    public ResponseEntity<Object> executar(@RequestBody GrupoConsistenciaRequest request) {
        try {
            var response = gateway.executar(request);
            return ResponseEntity.ok(response);
        } catch (ValidacaoException e) {
            var erros = e.getErros();
            var response = new GrupoConsistenciaErroResponseDTO(
                    request.idGrupoConsistencia(),
                    null,
                    "FALHA_VALIDACAO",
                    "Parametros de entrada invalidos",
                    request.correlationId(),
                    erros
            );
            return ResponseEntity.badRequest().body(response);
        }
    }
}
