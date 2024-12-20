package com.github.ansibleproxy.config;

import org.apache.catalina.connector.Connector;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

/**
 * WebServerFactory
 *
 * @author hackyo
 * @since 2022/4/1
 */
@Configuration
public class WebServerFactory implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {

    @Value("${server.ssl.enabled}")
    private Boolean serverSslEnabled;
    @Value("${server.http-port}")
    private Integer serverHttpPort;
    @Value("${server.ssl.port}")
    private Integer serverSslPort;
    @Value("${server.ssl.forced}")
    private Boolean serverSslForced;

    @Override
    public void customize(TomcatServletWebServerFactory server) {
        if (serverSslEnabled) {
            server.setPort(serverSslPort);
            Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
            connector.setScheme("http");
            connector.setPort(serverHttpPort);
            connector.setSecure(false);
            server.addAdditionalTomcatConnectors(connector);
        } else {
            server.setPort(serverHttpPort);
        }
    }

    public void setSslRedirect(HttpSecurity http) throws Exception {
        if (serverSslEnabled && serverSslForced) {
            http.portMapper(portMapper -> portMapper.http(serverHttpPort).mapsTo(serverSslPort))
                    .requiresChannel(requiresChannel -> requiresChannel.anyRequest().requiresSecure());
        }
    }

}
