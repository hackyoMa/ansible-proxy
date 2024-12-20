package com.github.ansibleproxy.config;

import com.github.ansibleproxy.common.BaseResult;
import com.github.ansibleproxy.common.HttpException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * GlobalExceptionHandler
 *
 * @author hakcyo
 * @since 2022/4/1
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthenticationException.class)
    @ResponseBody
    public ResponseEntity<BaseResult> exceptionHandler(AuthenticationException e) {
        return new ResponseEntity<>(BaseResult.fail(HttpStatus.UNAUTHORIZED, e.getMessage()),
                HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseBody
    public ResponseEntity<BaseResult> exceptionHandler(AccessDeniedException e) {
        return new ResponseEntity<>(BaseResult.fail(HttpStatus.FORBIDDEN, e.getMessage()),
                HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(HttpException.class)
    @ResponseBody
    public ResponseEntity<BaseResult> exceptionHandler(HttpException e) {
        HttpStatus status = e.getHttpStatus() != null ? e.getHttpStatus() : HttpStatus.INTERNAL_SERVER_ERROR;
        return new ResponseEntity<>(BaseResult.fail(status, e.getMessage()), status);
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<BaseResult> exceptionHandler(Exception e) {
        return new ResponseEntity<>(BaseResult.fail(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
