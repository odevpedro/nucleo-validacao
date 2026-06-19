package com.empresa.nucleovalidacao.exception;

import com.empresa.nucleovalidacao.model.dto.ErroValidacaoDTO;
import com.empresa.nucleovalidacao.model.dto.ValidacaoErroResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ValidacaoException.class)
    public ResponseEntity<ValidacaoErroResponseDTO> handleValidacao(ValidacaoException ex) {
        var erros = ex.getErros();
        var response = new ValidacaoErroResponseDTO(
                null, null, "FALHA_VALIDACAO",
                "Parâmetros de entrada inválidos", null, erros
        );
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(ValidacaoNaoEncontradaException.class)
    public ResponseEntity<ValidacaoErroResponseDTO> handleGrupoNaoEncontrado(ValidacaoNaoEncontradaException ex) {
        var erro = new ErroValidacaoDTO(
                "idGrupoValidacao",
                "Grupo de validação não encontrado: " + ex.getIdGrupoValidacao(),
                ex.getIdGrupoValidacao()
        );
        var response = new ValidacaoErroResponseDTO(
                ex.getIdGrupoValidacao(), null, "FALHA_VALIDACAO",
                "Grupo de validação não encontrado", null, List.of(erro)
        );
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidacaoErroResponseDTO> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        var erros = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> new ErroValidacaoDTO(fe.getField(), fe.getDefaultMessage(), fe.getRejectedValue()))
                .toList();
        var response = new ValidacaoErroResponseDTO(
                null, null, "FALHA_VALIDACAO",
                "Parâmetros de entrada inválidos", null, erros
        );
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ValidacaoErroResponseDTO> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        var erro = new ErroValidacaoDTO(
                "body", "Corpo da requisição inválido: " + ex.getMostSpecificCause().getMessage(), null
        );
        var response = new ValidacaoErroResponseDTO(
                null, null, "FALHA_VALIDACAO",
                "Erro de formatação da requisição", null, List.of(erro)
        );
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ValidacaoErroResponseDTO> handleGeneric(Exception ex) {
        log.error("Erro interno inesperado", ex);
        var erro = new ErroValidacaoDTO(
                "internal", "Erro interno do servidor: " + ex.getMessage(), null
        );
        var response = new ValidacaoErroResponseDTO(
                null, null, "FALHA_CRITICA",
                "Erro interno inesperado", null, List.of(erro)
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
