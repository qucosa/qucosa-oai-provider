package de.qucosa.oai.provider;

import java.util.Date;

public class ErrorDetails {
    private String classname;

    private String line;

    private String statuscode;

    private String errorMsg;

    private String requestPath;

    private String exception;

    private String stacktrace;

    private Date date;

    public String getClassname() {
        return classname;
    }

    public ErrorDetails setClassname(String classname) {
        this.classname = classname;
        return this;
    }

    public String getLine() {
        return line;
    }

    public ErrorDetails setLine(String line) {
        this.line = line;
        return this;
    }

    public String getStatuscode() {
        return statuscode;
    }

    public ErrorDetails setStatuscode(String statuscode) {
        this.statuscode = statuscode;
        return this;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public ErrorDetails setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
        return this;
    }

    public String getRequestPath() {
        return requestPath;
    }

    public ErrorDetails setRequestPath(String requestPath) {
        this.requestPath = requestPath;
        return this;
    }

    public String getException() {
        return exception;
    }

    public ErrorDetails setException(String exception) {
        this.exception = exception;
        return this;
    }

    public String getStacktrace() {
        return stacktrace;
    }

    public ErrorDetails setStacktrace(String stacktrace) {
        this.stacktrace = stacktrace;
        return this;
    }

    public Date getDate() {
        return date;
    }

    public ErrorDetails setDate(Date date) {
        this.date = date;
        return this;
    }
}
