package com.empresa.grupoconsistencias.exception;

import com.empresa.grupoconsistencias.model.dto.ErroValidacaoDTO;
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
