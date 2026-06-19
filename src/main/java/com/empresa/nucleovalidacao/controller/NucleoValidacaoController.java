package com.empresa.nucleovalidacao.controller;

import com.empresa.nucleovalidacao.exception.ValidacaoException;
import com.empresa.nucleovalidacao.model.dto.ValidacaoErroResponseDTO;
import com.empresa.nucleovalidacao.model.dto.ValidacaoRequest;
import com.empresa.nucleovalidacao.model.dto.ValidacaoResponseDTO;
import com.empresa.nucleovalidacao.service.NucleoValidacaoGateway;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/nucleo-validacao")
public class NucleoValidacaoController {

    private final NucleoValidacaoGateway gateway;

    public NucleoValidacaoController(NucleoValidacaoGateway gateway) {
        this.gateway = gateway;
    }

    @PostMapping("/executar")
    public ResponseEntity<Object> executar(@RequestBody ValidacaoRequest request) {
        try {
            var response = gateway.executar(request);
            return ResponseEntity.ok(response);
        } catch (ValidacaoException e) {
            var erros = e.getErros();
            var response = new ValidacaoErroResponseDTO(
                    request.idGrupoValidacao(),
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
