package com.github.ansibleproxy.common;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * TaskProperties
 *
 * @author hackyo
 * @since 2022/4/1
 */
@ConfigurationProperties("task")
@Component
@Data
public class TaskProperties {

    private String folder;
    private Boolean cleanFile;
    private Boolean debug;

}
