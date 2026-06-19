package com.empresa.nucleovalidacao.exception;

public class ProcedureExecutionException extends RuntimeException {
    private final String procedureRef;
    private final String sqlState;
    private final int errorCode;

    public ProcedureExecutionException(String procedureRef, String message, String sqlState, int errorCode) {
        super(message);
        this.procedureRef = procedureRef;
        this.sqlState = sqlState;
        this.errorCode = errorCode;
    }

    public String getProcedureRef() {
        return procedureRef;
    }

    public String getSqlState() {
        return sqlState;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
