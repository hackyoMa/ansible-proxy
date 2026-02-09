package com.github.ansibleproxy.config;

import com.github.ansibleproxy.common.HttpException;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.webmvc.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * GlobalErrorController
 *
 * @author hackyo
 * @since 1.0.0
 */
@Controller
public class GlobalErrorController implements ErrorController {

    @RequestMapping("${server.error.path:${error.path:/error}}")
    public void errorHtml(HttpServletRequest request) {
        HttpStatus status = getStatus(request);
        Exception exception = getException(request);
        String errorMessage = getErrorMessage(request, status, exception);
        if (HttpStatus.UNAUTHORIZED.equals(status) || exception instanceof AuthenticationException) {
            throw new BadCredentialsException(errorMessage);
        } else if (HttpStatus.FORBIDDEN.equals(status) || exception instanceof AccessDeniedException) {
            throw new AccessDeniedException(errorMessage);
        } else {
            throw new HttpException(status, errorMessage);
        }
    }

    private HttpStatus getStatus(HttpServletRequest request) {
        Object statusCode = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (statusCode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        try {
            return HttpStatus.valueOf((Integer) statusCode);
        } catch (Exception e) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

    private Exception getException(HttpServletRequest request) {
        Object exception = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        if (exception == null) {
            return new Exception();
        }
        try {
            return (Exception) exception;
        } catch (Exception e) {
            return new Exception();
        }
    }

    private String getErrorMessage(HttpServletRequest request, HttpStatus status, Exception exception) {
        String errorMessage = null;
        if (exception != null) {
            errorMessage = exception.getMessage();
        }
        if (StringUtils.hasLength(errorMessage)) {
            return errorMessage;
        }
        errorMessage = (String) request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        if (StringUtils.hasLength(errorMessage)) {
            return errorMessage;
        }
        return status.getReasonPhrase();
    }

}
