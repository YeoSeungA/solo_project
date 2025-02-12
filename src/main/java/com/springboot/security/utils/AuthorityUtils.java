package com.springboot.security.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AuthorityUtils {
    @Value("${mail.address.admin}")
    private String adminMailAddress;
//    1. 데이터에 보관될 String 권한
    private final List<String> ADMIN_ROLES_STRING = List.of("ADMIN", "USER");
    private final List<String> USER_ROLES_STRING = List.of("USER");

    //    emil 입력시 데이터 베이스에 저장
    public List<String> createRoles(String email) {
        if(email.equals(adminMailAddress)) {
            return ADMIN_ROLES_STRING;
        }
        return USER_ROLES_STRING;
    }
    //    데이터베이스에서 가져온 role을 Security에서 권한부여시 사용되는 GrantedAuthority로 바꾸기
    public List<GrantedAuthority> createAuthorities(List<String> roles) {
        List<GrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());
        return authorities;
    }
}
