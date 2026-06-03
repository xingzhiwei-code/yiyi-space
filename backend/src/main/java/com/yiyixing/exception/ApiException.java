package com.yiyixing.exception;

import org.springframework.http.HttpStatus;

/**
 * 自定义业务异常 — 携带 HTTP 状态码和错误消息。
 */
public class ApiException extends RuntimeException {

    private final HttpStatus status;

    public ApiException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
