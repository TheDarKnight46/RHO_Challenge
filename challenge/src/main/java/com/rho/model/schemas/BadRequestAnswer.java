package com.rho.model.schemas;

public class BadRequestAnswer extends CustomSchema {
    private String errorMessage = "";

    public BadRequestAnswer(boolean success, boolean callExecuted, String error) {
        super(success, callExecuted);
        this.errorMessage = error;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean isCallExecuted() {
        return super.isCallExecuted();
    }

    public boolean isSuccess() {
        return super.isCallExecuted();
    }
}
