package com.springboot.security.filter;

import com.springboot.security.jwt.JwtTokenizer;
import com.springboot.security.utils.AuthorityUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class JwtVerificationFilter extends OncePerRequestFilter {
    private final JwtTokenizer jwtTokenizer;
    private final AuthorityUtils authorityUtils;

    public JwtVerificationFilter(JwtTokenizer jwtTokenizer, AuthorityUtils authorityUtils) {
        this.jwtTokenizer = jwtTokenizer;
        this.authorityUtils = authorityUtils;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        //        security는 throw 하지 X. request에 예외를 담음. filter를 다 넘어가다가 SecurityContext에 Exception은 저장 안한다.
//        exception 발생시 handler 역할을 하는 곳이 필요핟.
        try{
            // 검증  // 검증이 끝나면 SecurituContext에 보관해야 한다.
            Map<String, Object> claims = verifyJws(request);
            setAuthenticationToContext(claims);
        } catch (SignatureException se) { // 검증 실패
            request.setAttribute("exception", se);
        } catch (ExpiredJwtException ee) {
            request.setAttribute("exception", ee);
        } catch (Exception e) {
            request.setAttribute("exception", e);
        }
//        다음 필터로 넘기자
        filterChain.doFilter(request, response);
    }
    //검증은 header에 토큰이 들어올때만 적용되면 된다.
//    동작하지 말아야할 때를 잘 지정해야 한다.
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String authorization = request.getHeader("Authorization");

        return authorization == null || !authorization.startsWith("Bearer");
    }

    //    실패시 토큰 검증 실패 예외가 발생...?
    private Map<String, Object> verifyJws(HttpServletRequest request) {
//        Baarder을 대체해야 한다. 토큰이 아님. 빈문자열로 없애야 한다.
        String jws = request.getHeader("Authorization").replace("Bearer ","");
        String base64EncodedSecretKey = jwtTokenizer.encodedBase64SecretKey(jwtTokenizer.getSecretKey());
        Map<String, Object> claims = jwtTokenizer.getClaims(jws, base64EncodedSecretKey).getBody();
        return claims;
    }

    private void setAuthenticationToContext(Map<String, Object> claims) {
        String username = (String) claims.get("username");
//        권한을 갖고오자
        List<GrantedAuthority> authorities = authorityUtils.createAuthorities((List) claims.get("roles"));
//        비밀번호는 비운걸로 가져오자. token에 없음
        Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
