package com.github.ansibleproxy.config;

import com.github.ansibleproxy.common.HttpException;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * GlobalErrorController
 *
 * @author hackyo
 * @since 2022/4/1
 */
@Controller
public class GlobalErrorController implements ErrorController {

    @RequestMapping("${server.error.path:${error.path:/error}}")
    public void errorHtml(HttpServletRequest request) {
        HttpStatus status = getStatus(request);
        String errorMessage = (String) request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        errorMessage = StringUtils.hasLength(errorMessage) ? errorMessage : status.getReasonPhrase();
        if (HttpStatus.UNAUTHORIZED.equals(status)) {
            throw new BadCredentialsException(errorMessage);
        } else if (HttpStatus.FORBIDDEN.equals(status)) {
            throw new AccessDeniedException(errorMessage);
        } else {
            throw new HttpException(status, errorMessage);
        }
    }

    private HttpStatus getStatus(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (statusCode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        try {
            return HttpStatus.valueOf(statusCode);
        } catch (Exception e) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

}
