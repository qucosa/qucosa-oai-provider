package de.qucosa.oai.provider;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

public class ErrorDetails {
    private String classname;

    private HttpStatus statuscode;

    private String errorMsg;

    private String requestPath;

    private String requestMethod;

    private Exception exception;

    private StackTraceElement[] stacktrace;

    private LocalDateTime date;

    private String method;

    public ErrorDetails create(String classname, String method, String requestMethodAndPath, HttpStatus statuscode, String errorMsg, Exception exception) {
        this.date = LocalDateTime.now();
        this.classname = classname;
        this.method = method;
        this.statuscode = statuscode;
        this.errorMsg = (exception != null) ? exception.getMessage() : errorMsg;
        setException(exception);
        setRequestPath(requestMethodAndPath);
        return this;
    }

    public ResponseEntity response() {
        return new ResponseEntity(this, this.statuscode);
    }

    public String getClassname() {
        return classname;
    }

    public String getStatuscode() {
        return statuscode.toString();
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public String getRequestPath() {
        return requestPath;
    }

    public Exception getException() {
        return exception;
    }

    public StackTraceElement[] getStacktrace() {
        return stacktrace;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public String getMethod() {
        return method;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    private ErrorDetails setMethod(String method) {
        this.method = method;
        return this;
    }

    private ErrorDetails setRequestPath(String requestMethodAndPath) {
        String[] req = requestMethodAndPath.split(":");
        this.requestPath = req[1];
        this.requestMethod = req[0];
        return this;
    }

    private ErrorDetails setException(Exception exception) {

        if (exception != null) {
            this.exception = exception;
            setStacktrace(this.exception.getStackTrace());
        }

        return this;
    }
    private ErrorDetails setStacktrace(StackTraceElement[] stacktrace) {
        this.stacktrace = stacktrace;
        return this;
    }

    private ErrorDetails setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
        return this;
    }
}
