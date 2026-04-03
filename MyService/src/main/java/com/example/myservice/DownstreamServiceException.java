package com.example.myservice;

import org.springframework.http.HttpStatusCode;

public class DownstreamServiceException extends RuntimeException {

    private final HttpStatusCode statusCode;

    public DownstreamServiceException(String message, HttpStatusCode statusCode, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }
}
