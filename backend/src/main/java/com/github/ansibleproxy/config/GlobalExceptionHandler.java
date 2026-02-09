package com.github.ansibleproxy.config;

import com.github.ansibleproxy.common.BaseResult;
import com.github.ansibleproxy.common.HttpException;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * GlobalExceptionHandler
 *
 * @author hackyo
 * @since 1.0.0
 */
@ControllerAdvice
public class GlobalExceptionHandler implements ResponseBodyAdvice<@NonNull Object> {

    /**
     * unauthorized exception
     *
     * @param e authentication exception
     * @return exception message
     */
    @ExceptionHandler(AuthenticationException.class)
    @ResponseBody
    public ResponseEntity<@NonNull BaseResult> exceptionHandler(AuthenticationException e) {
        return new ResponseEntity<>(BaseResult.fail(HttpStatus.UNAUTHORIZED, e.getMessage()),
                HttpStatus.UNAUTHORIZED);
    }

    /**
     * forbidden exception
     *
     * @param e access denied exception
     * @return exception message
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseBody
    public ResponseEntity<@NonNull BaseResult> exceptionHandler(AccessDeniedException e) {
        return new ResponseEntity<>(BaseResult.fail(HttpStatus.FORBIDDEN, e.getMessage()),
                HttpStatus.FORBIDDEN);
    }

    /**
     * no resource found exception
     *
     * @param response response
     * @return forward index
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public String httpNoFoundExceptionHandler(HttpServletResponse response) {
        response.setStatus(HttpStatus.OK.value());
        return "forward:/index.html";
    }

    /**
     * http exception
     *
     * @param e http exception
     * @return exception message
     */
    @ExceptionHandler(HttpException.class)
    @ResponseBody
    public ResponseEntity<@NonNull BaseResult> exceptionHandler(HttpException e) {
        HttpStatus status = e.getHttpStatus() != null ? e.getHttpStatus() : HttpStatus.INTERNAL_SERVER_ERROR;
        return new ResponseEntity<>(BaseResult.fail(status, e.getMessage()), status);
    }

    /**
     * exception
     *
     * @param e exception
     * @return exception message
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<@NonNull BaseResult> exceptionHandler(Exception e) {
        return new ResponseEntity<>(BaseResult.fail(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Supported types
     *
     * @param returnType    return type
     * @param converterType converter type
     * @return support
     */
    @Override
    public boolean supports(@Nullable MethodParameter returnType,
                            @Nullable Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    /**
     * before body write
     *
     * @param body                  body
     * @param returnType            return type
     * @param selectedContentType   selected content type
     * @param selectedConverterType selected converter type
     * @param request               request
     * @param response              response
     * @return return value
     */
    @Override
    public Object beforeBodyWrite(Object body,
                                  @Nullable MethodParameter returnType, @Nullable MediaType selectedContentType,
                                  @Nullable Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  @Nullable ServerHttpRequest request, @Nullable ServerHttpResponse response) {
        if (body != null && BaseResult.class.isAssignableFrom(body.getClass())) {
            return body;
        }
        return BaseResult.ok(body);
    }

}
