package com.github.ansibleproxy.common;

import lombok.Data;
import org.springframework.http.HttpStatus;

/**
 * HttpException
 *
 * @author hackyo
 * @since 1.0.0
 */
@Data
public class HttpException extends RuntimeException {

    private HttpStatus httpStatus;

    public HttpException(String message) {
        super(message);
        this.httpStatus = HttpStatus.BAD_REQUEST;
    }

    public HttpException(HttpStatus httpStatus, String message) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public HttpException(HttpStatus httpStatus, Throwable cause) {
        super(cause);
        this.httpStatus = httpStatus;
    }

}
