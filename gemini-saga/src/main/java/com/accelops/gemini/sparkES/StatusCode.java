package com.accelops.gemini.sparkES;

/**
 * Created by kai.zhang on 1/22/2016.
 */
public enum StatusCode {

    SUCCESS("Success", 0),
    UNKNOWN_REQUEST("Unknown request command", 1),
    UNKNOWN_REQUEST_ID("Unknown request ID", 2),
    UNKNOWN_REQUEST_OPER("Unknown request operation", 3),
    REQUEST_XML_SYNTAX_ERROR("Request xml syntax error", 4),
    INVALID_REQUEST_XML("Invalid request xml", 5),
    INTERNAL_ERR("Server internal error", 7);

    private int code;
    String msg;

    private StatusCode(String msg, int code) {
        this.msg = msg;
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }

}
