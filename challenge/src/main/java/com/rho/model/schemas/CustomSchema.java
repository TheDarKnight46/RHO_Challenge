package com.rho.model.schemas;

public class CustomSchema {
    private boolean success = false;
    private boolean callExecuted = false;

    public CustomSchema(boolean success, boolean executed) {
        this.success = success;
        this.callExecuted = executed;
    }

    public boolean isCallExecuted() {
        return callExecuted;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setCallExecuted(boolean callExecuted) {
        this.callExecuted = callExecuted;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
