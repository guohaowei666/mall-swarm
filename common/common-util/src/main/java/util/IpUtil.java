package util;


import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;

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
    public static String getIpAddress(HttpServletRequest request) {
        String ipAddress = null;
        try {
            ipAddress = request.getHeader(X_FORWARDED_FOR);
            if (StringUtils.hasLength(ipAddress) || UNKNOWN.equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader(PROXY_CLIENT_IP);
            }
            if (StringUtils.hasLength(ipAddress) || UNKNOWN.equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader(WL_PROXY_CLIENT_IP);
            }
            if (StringUtils.hasLength(ipAddress) || UNKNOWN.equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getRemoteAddr();
                if (ipAddress.equals(LOCALHOST)) {
                    // 根据网卡取本机配置的IP
                    InetAddress inet = null;
                    try {
                        inet = InetAddress.getLocalHost();
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
                    ipAddress = inet.getHostAddress();
                }
            }
            // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
            // "***.***.***.***".length()
            if (ipAddress != null && ipAddress.length() > 15) {
                // = 15
                if (ipAddress.indexOf(SeparatorUtil.COMMA) > 0) {
                    ipAddress = ipAddress.substring(0, ipAddress.indexOf(SeparatorUtil.COMMA));
                }
            }
        } catch (Exception e) {
            ipAddress = "";
        }
        return ipAddress;
    }

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
            if (ip.contains(SeparatorUtil.COMMA)) {
                ip = ip.split(SeparatorUtil.COMMA)[0];
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
    /**
     * 认证中心中获取ip地址
     *
     * @param request
     * @return
     */
    public static String getGatewayIpAddress(HttpServletRequest request) {
        String ip = request.getHeader(X_FORWARDED_FOR);
        if (!StringUtils.hasLength(ip) && !UNKNOWN.equalsIgnoreCase(ip)) {
            // 多次反向代理后会有多个ip值，第一个ip才是真实ip
            if (ip.contains(SeparatorUtil.COMMA)) {
                ip = ip.split(SeparatorUtil.COMMA)[0];
            }
        }
        if (StringUtils.hasLength(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader(PROXY_CLIENT_IP);
        }
        if (StringUtils.hasLength(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader(WL_PROXY_CLIENT_IP);
        }
        if (StringUtils.hasLength(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader(HTTP_CLIENT_IP);
        }
        if (StringUtils.hasLength(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader(HTTP_X_FORWARDED_FOR);
        }
        if (StringUtils.hasLength(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader(X_REAL_IP);
        }
        if (StringUtils.hasLength(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}