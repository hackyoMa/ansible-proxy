package com.github.ansibleproxy.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * I18n
 *
 * @author hackyo
 * @since 1.0.0
 */
@Component
public class I18n {

    private static MessageSource MESSAGE_SOURCE;

    @Autowired
    public I18n(MessageSource messageSource) {
        MESSAGE_SOURCE = messageSource;
    }

    public static String get(String message, String... params) {
        return MESSAGE_SOURCE.getMessage(message,
                Arrays.copyOf(params, params.length, Object[].class),
                LocaleContextHolder.getLocale());
    }

}
