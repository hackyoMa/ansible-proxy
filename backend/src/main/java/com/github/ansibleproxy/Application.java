package com.github.ansibleproxy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Application
 *
 * @author hackyo
 * @since 1.0.0
 */
@SpringBootApplication
@EnableScheduling
public class Application {

    static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
