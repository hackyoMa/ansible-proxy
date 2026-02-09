package com.github.ansibleproxy.common;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

/**
 * BaseResult
 *
 * @author hackyo
 * @since 1.0.0
 */
@Data
public class BaseResult {

    /**
     * status success
     */
    private boolean success = true;

    /**
     * status code
     */
    private int code = HttpStatus.OK.value();

    /**
     * message
     */
    private String message = HttpStatus.OK.getReasonPhrase();

    /**
     * timestamp
     */
    private LocalDateTime timestamp;

    /**
     * data
     */
    private Object data = null;

    private BaseResult() {
    }

    public static BaseResult ok() {
        return ok(null);
    }

    public static BaseResult ok(Object data) {
        return ok(data, HttpStatus.OK.getReasonPhrase());
    }

    public static BaseResult ok(Object data, String message) {
        BaseResult result = new BaseResult();
        result.setData(data);
        result.setMessage(message);
        return result;
    }

    public static BaseResult fail(HttpStatus code) {
        return fail(code, code.getReasonPhrase());
    }

    public static BaseResult fail(HttpStatus code, String message) {
        BaseResult result = new BaseResult();
        result.setSuccess(false);
        result.setCode(code.value());
        result.setMessage(message);
        return result;
    }

    public LocalDateTime getTimestamp() {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
        return timestamp;
    }

}
