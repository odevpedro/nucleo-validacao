package com.empresa.nucleovalidacao.exception;

import com.empresa.nucleovalidacao.model.dto.ErroValidacaoDTO;
import java.util.List;

public class ValidacaoException extends RuntimeException {
    private final List<ErroValidacaoDTO> erros;

    public ValidacaoException(List<ErroValidacaoDTO> erros) {
        super("Erros de validação");
        this.erros = erros;
    }

    public List<ErroValidacaoDTO> getErros() {
        return erros;
    }
}
