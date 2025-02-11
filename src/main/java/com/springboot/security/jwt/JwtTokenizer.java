package com.springboot.security.jwt;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Map;

//로그인 시 발행되는 토큰을 만들어 보자!
@Component
public class JwtTokenizer {
    @Getter
    @Value("${jwt.key.secret}")
    private String secretKey;

    @Getter
    @Value("${jwt.access-token-expiration-minutes}")
    private int accessTokenExpirationMinutes;

    @Getter
    @Value("${jwt.refresh-token-expiration-minutes}")
    private int refreshTokenExpirationMinutes;
//    Plain Text 형태인 Secret Key의 byte[]를 Base64 형식의 문자열로 인코딩한다.(jjwt가) - 그냥 text로 표현하기 위해 사용한다.
    public String encodedBase64SecretKey(String secretKey) {
        return Encoders.BASE64.encode(secretKey.getBytes(StandardCharsets.UTF_8));
    }

//    AccessToken을 만들자. 인증된 사용자에게 JWT를 최초로 발급해 주기 위한 JWT 생성 메서드
    public String generateAccessToken (Map<String, Object> claims,
                                       String subject,
                                       Date expiration,
                                       String base64EncodedSecretKey) {
        Key key = getKeyFromBase64EncodedKey(base64EncodedSecretKey);
    }

//      Key 객체를 만들어 보자
    private Key getKeyFromBase64EncodedKey(String base64EncodedSecretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(base64EncodedSecretKey.getBytes(StandardCharsets.UTF_8))
    }
}
