package com.springboot.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Calendar;
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
//        Base64 형식의 Secret Key 문자열을 이용해 Key 객체를 얻는다.
        Key key = getKeyFromBase64EncodedKey(base64EncodedSecretKey);
//        토큰을 만들자
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(Calendar.getInstance().getTime())
                .setExpiration(expiration)
                .compact();
    }

//     암호화된 key를 받아서 해석해보자. 디코딩 된 key는 복호화가 아니라 "서명 생성"에 사용된다.
//    JWT 서명에 사용할 Secret Key를 생성 실제 서명에 필요한 바이너리 키 값으로 변환하기 위해 사용한다.
public String generateRefreshToken (String subject,
                                   Date expiration,
                                   String base64EncodedSecretKey) {
    Key key = getKeyFromBase64EncodedKey(base64EncodedSecretKey);

    return Jwts.builder()
            .setSubject(subject)
            .setIssuedAt(Calendar.getInstance().getTime())
            .setExpiration(expiration)
            .compact();
}
// 검증 후, Claims을 반환하자. JWS => 서명이 완료된 토큰
    public Jws<Claims> getClaims(String jws, String base64EncodedSecretKey) {
        Key key = getKeyFromBase64EncodedKey(base64EncodedSecretKey);
//        parserBuilder()는 JWT를 파싱하기 위해 사용되는 메서드 토큰의 유효성 검사도 진행된다.
//        파싱 => 주어진 데이터를 분석하고 해석해 원하는 형태로 변환하는 작업
        Jws<Claims> claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
//        주어진 토큰을 파싱해 헤더, 페이로드, 서명 세 부분으로 분리 + 토큰의 서명을 검증해 데이터의 무결성을 확인한다.
//        팡싱의 결과로 Jws<Claims> 객체를 반환. 이 객체를 통해 헤더, 페이로드, 서명 등의 정보의 접근할 수 있다.
                .parseClaimsJws(jws);
        return claims;
    }
//   단순한 검증만 하는 용도로 쓰일 때
    public void verifySignature(String jws, String base64EncodedSecretKey) {
        Key key = getKeyFromBase64EncodedKey(base64EncodedSecretKey);
        Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(jws);
    }
//    expiration을 반환하는 메서드
    public Date getTokenExpiration(int expirationMinutes) {
//        Calendar란 인터페이스를, .getInstance() 메서드를 이용해 구현한다.
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, expirationMinutes);
        Date expiration = calendar.getTime();

        return expiration;
    }


//      Key 객체를 만들어 보자
    private Key getKeyFromBase64EncodedKey(String base64EncodedSecretKey) {
//     Base64 형식으로 인코딩 된 Secret Key를 디코딩하고, byte array를 반환한다.
        byte[] keyBytes = Decoders.BASE64.decode(base64EncodedSecretKey);
//        key byte array를 기반으로 적절한 HMAC 알고리즘을 적용한 Key 객체를 생성한다.
        Key key = Keys.hmacShaKeyFor(keyBytes);
        return key;
    }
}
