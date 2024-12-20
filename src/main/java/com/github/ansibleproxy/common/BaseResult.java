package com.github.ansibleproxy.common;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

/**
 * BaseResult
 *
 * @author hackyo
 * @since 2022/4/1
 */
@Data
public class BaseResult implements Serializable {

    private boolean success = true;
    private int code = HttpStatus.OK.value();
    private String message = "success";
    private long timestamp = System.currentTimeMillis();

    public BaseResult() {
    }

    public BaseResult(HttpStatus code, String message) {
        this.success = code.is2xxSuccessful();
        this.code = code.value();
        this.message = message;
    }

    public static BaseResult ok() {
        return new BaseResult();
    }

    public static BaseResult fail(HttpStatus code, String message) {
        return new BaseResult(code, message);
    }

}
