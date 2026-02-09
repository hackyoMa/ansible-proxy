package com.github.ansibleproxy.config;

import com.github.ansibleproxy.common.SecurityProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtIssuerValidator;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

import java.security.interfaces.RSAPublicKey;
import java.util.HashSet;

/**
 * SecurityConfiguration
 *
 * @author hackyo
 * @since 1.0.0
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final SecurityProperties securityProperties;
    private final WebServerFactory webServerFactory;

    @Value("${spring.application.name}")
    private String applicationName;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        webServerFactory.setSslRedirect(http);
        http
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .headers(headers -> headers.frameOptions((HeadersConfigurer.FrameOptionsConfig::sameOrigin)))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer((oauth2) -> oauth2.jwt(Customizer.withDefaults())
                        .authenticationEntryPoint((_, _, authException) -> {
                            throw authException;
                        })
                        .accessDeniedHandler((_, _, accessDeniedException) -> {
                            throw accessDeniedException;
                        })
                );
        configureWhitelistAccess(http);
        return http.build();
    }

    private void configureWhitelistAccess(HttpSecurity http) {
        PathPatternRequestMatcher.Builder requestMatcher = PathPatternRequestMatcher.withDefaults().basePath(WebConfig.CONTEXT_PATH);
        http.authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests.requestMatchers(requestMatcher.matcher("/**")).permitAll());
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withPublicKey((RSAPublicKey) securityProperties.getSecret().getPublicKey())
                .signatureAlgorithm(SignatureAlgorithm.from(SecurityProperties.Secret.JWS_ALGORITHM))
                .validateType(true).build();
        jwtDecoder.setJwtValidator(JwtValidators.createDefaultWithValidators(new JwtIssuerValidator(applicationName)));
        return jwtDecoder;
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(_ -> new HashSet<>());
        return jwtAuthenticationConverter;
    }

}
