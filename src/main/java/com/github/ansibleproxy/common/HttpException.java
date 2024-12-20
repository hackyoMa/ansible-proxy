package com.github.ansibleproxy.common;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

/**
 * HttpException
 *
 * @author hackyo
 * @since 2022/4/1
 */
@Data
public class HttpException extends RuntimeException implements Serializable {

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
