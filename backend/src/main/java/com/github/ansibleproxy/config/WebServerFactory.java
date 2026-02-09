package com.github.ansibleproxy.config;

import org.eclipse.jetty.server.ServerConnector;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jetty.servlet.JettyServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

/**
 * WebServerFactory
 *
 * @author hackyo
 * @since 1.0.0
 */
@Configuration
public class WebServerFactory implements WebServerFactoryCustomizer<@NonNull JettyServletWebServerFactory> {

    @Value("${server.ssl.enabled}")
    private Boolean serverSslEnabled;
    @Value("${server.http-port}")
    private Integer serverHttpPort;
    @Value("${server.ssl.port}")
    private Integer serverSslPort;
    @Value("${server.ssl.forced}")
    private Boolean serverSslForced;

    @Override
    public void customize(JettyServletWebServerFactory server) {
        if (serverSslEnabled) {
            server.setPort(serverSslPort);
            server.addServerCustomizers(customizer -> {
                ServerConnector httpsServerConnector = (ServerConnector) customizer.getConnectors()[0];
                ServerConnector httpServerConnector = new ServerConnector(httpsServerConnector.getServer());
                httpServerConnector.setHost(httpsServerConnector.getHost());
                httpServerConnector.setPort(serverHttpPort);
                customizer.addConnector(httpServerConnector);
            });
        } else {
            server.setPort(serverHttpPort);
        }
    }

    public void setSslRedirect(HttpSecurity http) {
        if (serverSslEnabled && serverSslForced) {
            http.portMapper(portMapper -> portMapper.http(serverHttpPort).mapsTo(serverSslPort))
                    .redirectToHttps(Customizer.withDefaults());
        }
    }

}
