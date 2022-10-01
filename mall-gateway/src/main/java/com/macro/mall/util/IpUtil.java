package com.macro.mall.util;


import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.StringUtils;

/**
 * 获取ip地址
 * @author 郭浩伟
 */
public class IpUtil {
    private static final String X_FORWARDED_FOR = "x-forwarded-for";
    private static final String PROXY_CLIENT_IP = "Proxy-Client-IP";
    private static final String WL_PROXY_CLIENT_IP = "WL-Proxy-Client-IP";
    private static final String HTTP_CLIENT_IP = "HTTP_CLIENT_IP";
    private static final String HTTP_X_FORWARDED_FOR = "HTTP_X_FORWARDED_FOR";
    private static final String X_REAL_IP = "X-Real-IP";
    private static final String UNKNOWN = "unknown";
    private static final String LOCALHOST = "127.0.0.1";
    private static final String COMMA = ",";

    /**
     * 网关中获取ip地址
     *
     * @param request
     * @return
     */
    public static String getGatewayIpAddress(ServerHttpRequest request) {
        HttpHeaders headers = request.getHeaders();
        String ip = headers.getFirst(X_FORWARDED_FOR);
        if (!StringUtils.hasLength(ip) && !UNKNOWN.equalsIgnoreCase(ip)) {
            // 多次反向代理后会有多个ip值，第一个ip才是真实ip
            if (ip.contains(COMMA)) {
                ip = ip.split(COMMA)[0];
            }
        }
        if (StringUtils.hasLength(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = headers.getFirst(PROXY_CLIENT_IP);
        }
        if (StringUtils.hasLength(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = headers.getFirst(WL_PROXY_CLIENT_IP);
        }
        if (StringUtils.hasLength(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = headers.getFirst(HTTP_CLIENT_IP);
        }
        if (StringUtils.hasLength(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = headers.getFirst(HTTP_X_FORWARDED_FOR);
        }
        if (StringUtils.hasLength(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = headers.getFirst(X_REAL_IP);
        }
        if (StringUtils.hasLength(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddress().getAddress().getHostAddress();
        }
        return ip;
    }
}