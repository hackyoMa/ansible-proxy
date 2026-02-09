package com.github.ansibleproxy.config;

import com.github.ansibleproxy.util.ExecUtil;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

/**
 * ServerContextListener
 *
 * @author hackyo
 * @since 1.0.0
 */
@Slf4j
@Configuration
public class ServerContextListener implements ServletContextListener {

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        log.info("ServerContextListener contextDestroyed");
        ExecUtil.close();
    }

}
