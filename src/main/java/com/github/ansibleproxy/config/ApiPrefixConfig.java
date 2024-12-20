package com.github.ansibleproxy.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * ApiPrefixConfig
 *
 * @author hackyo
 * @since 2022/4/1
 */
@Configuration
public class ApiPrefixConfig implements WebMvcConfigurer {

    public final static String CONTEXT_PATH = "/api";

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.addPathPrefix(CONTEXT_PATH, c -> c.isAnnotationPresent(Controller.class))
                .addPathPrefix(CONTEXT_PATH, c -> c.isAnnotationPresent(RestController.class));
    }

}
