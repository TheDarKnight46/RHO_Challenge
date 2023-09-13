package com.rho.model.schemas;

public class BadRequestAnswer {
    private boolean success = false;
    private boolean callExecuted = false;
    private String errorMessage = "";

    public BadRequestAnswer(boolean success, boolean callExecuted, String error) {
        this.success = success;
        this.callExecuted = callExecuted;
        this.errorMessage = error;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean isCallExecuted() {
        return callExecuted;
    }

    public boolean isSuccess() {
        return success;
    }
}
