package com.github.ansibleproxy.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * RequestUtil
 *
 * @author hackyo
 * @since 1.0.0
 */
public final class RequestUtil {

    private static final List<String> IP_HEADERS = Arrays.asList(
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_CLIENT_IP",
            "HTTP_X_FORWARDED_FOR",
            "X-Real-IP"
    );
    private static final String UNKNOWN = "unknown";

    public static String getClientIp(HttpServletRequest request) {
        String ip = IP_HEADERS.stream()
                .map(request::getHeader)
                .filter(header -> StringUtils.hasLength(header) && !UNKNOWN.equalsIgnoreCase(header))
                .findFirst()
                .orElse(null);
        if (ip != null && ip.contains(",")) {
            ip = Arrays.stream(ip.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty() && !UNKNOWN.equalsIgnoreCase(s))
                    .findFirst()
                    .orElse(null);
        }
        return ip != null ? ip : request.getRemoteAddr();
    }

    public static String getUserAgent(HttpServletRequest request) {
        String userAgent = request.getHeader(HttpHeaders.USER_AGENT);
        return userAgent != null ? userAgent : "";
    }

}
