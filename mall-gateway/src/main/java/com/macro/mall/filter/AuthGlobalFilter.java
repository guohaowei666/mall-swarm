package com.macro.mall.filter;

import cn.hutool.core.util.StrUtil;
import com.macro.mall.common.constant.AuthConstant;
import com.nimbusds.jose.JWSObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.text.ParseException;
import java.util.List;

/**
 * 将登录用户的JWT转化成用户信息的全局过滤器
 * Created by macro on 2020/6/17.
 */
@Component
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    private static Logger LOGGER = LoggerFactory.getLogger(AuthGlobalFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String token = exchange.getRequest().getHeaders().getFirst(AuthConstant.JWT_TOKEN_HEADER);
        if (StrUtil.isEmpty(token)) {
            return chain.filter(exchange);
        }
        try {
            //从token中解析用户信息并设置到Header中去
            String realToken = token.replace(AuthConstant.JWT_TOKEN_PREFIX, "");
            JWSObject jwsObject = JWSObject.parse(realToken);
            String userStr = jwsObject.getPayload().toString();
            LOGGER.info("AuthGlobalFilter.filter() user:{}",userStr);
            ServerHttpRequest request = exchange.getRequest().mutate().header(AuthConstant.USER_TOKEN_HEADER, userStr).build();
            exchange = exchange.mutate().request(request).build();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }

    /**
     * TODO 未登录的用户也可以添加商品到购物车，购物车独立出一个module，gateway进行放行，分发一个临时用户给它
     * 获取用户临时id
     * @param request
     * @return
     */
    private String getUserTempId(ServerHttpRequest request) {
        String userTempId="";
        //从头信息
        List<String> list = request.getHeaders().get("userTempId");
        //判断
        if(!CollectionUtils.isEmpty(list)){
            userTempId=list.get(0);
        }
        //判断，从cookie取
        if(StringUtils.isEmpty(userTempId)){
            HttpCookie cookie = request.getCookies().getFirst("userTempId");
            if(cookie!=null){
                userTempId=cookie.getValue();
            }
        }
        return userTempId;
    }
}
