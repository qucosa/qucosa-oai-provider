package de.qucosa.oai.provider;

import java.time.LocalDateTime;
import java.util.Date;

public class ErrorDetails {
    private String classname;

    private String line;

    private String statuscode;

    private String errorMsg;

    private String requestPath;

    private String exception;

    private String stacktrace;

    private LocalDateTime date;

    private String method;

    private String requestMethod;

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

    public LocalDateTime getDate() {
        return date;
    }

    public ErrorDetails setDate(LocalDateTime date) {
        this.date = date;
        return this;
    }

    public String getMethod() {
        return method;
    }

    public ErrorDetails setMethod(String method) {
        this.method = method;
        return this;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public ErrorDetails setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
        return this;
    }
}
