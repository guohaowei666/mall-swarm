package com.macro.mall.auth.service.impl;

import com.macro.mall.auth.constant.MessageConstant;
import com.macro.mall.auth.domain.SecurityUser;
import com.macro.mall.auth.service.UmsAdminService;
import com.macro.mall.auth.service.UmsMemberService;
import com.macro.mall.common.constant.AuthConstant;
import com.macro.mall.common.domain.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import util.IpUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * 用户管理业务类
 * Created by macro on 2020/6/19.
 */
@Slf4j
@Service
public class UserServiceImpl implements UserDetailsService {

    @Autowired
    private UmsAdminService adminService;
    @Autowired
    private UmsMemberService memberService;
    @Autowired
    private HttpServletRequest request;
    /**
     * 不管哪一种授权方式，第三方应用申请令牌之前，都必须先到系统备案，
     * 说明自己的身份，然后会拿到两个身份识别码：客户端 ID（client ID）和客户端密钥（client secret）。
     * 这是为了防止令牌被滥用，没有备案过的第三方应用，是不会拿到令牌的。
     */
    private static final String CLIENT_ID = "client_id";

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (StringUtils.isEmpty(username)) {
            log.warn("用户登陆用户名为空:{}", username);
            throw new UsernameNotFoundException(MessageConstant.USERNAME_IS_NULL);
        }
        //将用户登录的ip存到jwt中，供gateway判断token是否被盗用
        String ip = IpUtil.getGatewayIpAddress(request);
        String clientId = request.getParameter(CLIENT_ID);
        UserDto userDto = null;
        if (AuthConstant.ADMIN_CLIENT_ID.equals(clientId)) {
            userDto = adminService.loadUserByUsername(username);
        } else if (AuthConstant.PORTAL_CLIENT_ID.equals(clientId)) {
            userDto = memberService.loadUserByUsername(username);
        }
        if (userDto == null) {
            throw new UsernameNotFoundException(MessageConstant.USERNAME_PASSWORD_ERROR);
        }
        userDto.setClientId(clientId);
        userDto.setIp(ip);
        SecurityUser securityUser = new SecurityUser(userDto);
        if (!securityUser.isEnabled()) {
            throw new DisabledException(MessageConstant.ACCOUNT_DISABLED);
        } else if (!securityUser.isAccountNonLocked()) {
            throw new LockedException(MessageConstant.ACCOUNT_LOCKED);
        } else if (!securityUser.isAccountNonExpired()) {
            throw new AccountExpiredException(MessageConstant.ACCOUNT_EXPIRED);
        } else if (!securityUser.isCredentialsNonExpired()) {
            throw new CredentialsExpiredException(MessageConstant.CREDENTIALS_EXPIRED);
        }
        return securityUser;
    }

    public static void main(String[] args) {
        Integer i = null;
        Integer integer = Optional.ofNullable(i).orElse(1);
        System.out.println(integer);
    }
}
