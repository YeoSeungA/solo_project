package com.springboot.security.config;

import com.springboot.security.filter.JwtAuthenticationFilter;
import com.springboot.security.filter.JwtVerificationFilter;
import com.springboot.security.handler.MemberAccessDeniedHandler;
import com.springboot.security.handler.MemberAuthenticationEntryPoint;
import com.springboot.security.handler.MemberAuthenticationFailureHandler;
import com.springboot.security.handler.MemberAuthenticationSuccessHandler;
import com.springboot.security.jwt.JwtTokenizer;
import com.springboot.security.utils.AuthorityUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
public class SecurityConfiguration {
//    DI로 JwtTokenizer 주입 받는다.
    private final JwtTokenizer jwtTokenizer;
    private final AuthorityUtils authorityUtils;

    public SecurityConfiguration(JwtTokenizer jwtTokenizer, AuthorityUtils authorityUtils) {
        this.jwtTokenizer = jwtTokenizer;
        this.authorityUtils = authorityUtils;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
//                inmemory 사용일때만 필요합니다.frameOptions()
                .headers().frameOptions().sameOrigin()
                .and()
                .csrf().disable()
//                기본설정을 따라갈게요. 기본설정을 위한 정보는 우리가 만들어야 합니다. default 메서드를 따라간다.
                .cors(Customizer.withDefaults())
//                naver session있으면 쓰는데 없으면 안만들게 StateLess는 아예 session 안쓸게 session과 작별 안뇽~~
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
//                formLogin 페이지 안쓸게요 세션방식 X
                .formLogin().disable()
//                id,패스워드를 헤더에 담는 방식 안쓸게요 (옛날방식이다.)
                .httpBasic().disable()
                .exceptionHandling()
                .authenticationEntryPoint(new MemberAuthenticationEntryPoint())
//                예외 처리 filter를 등록. security에 예외를 처리하기 위한 point들이 있다. 이걸 사용하자!
                .accessDeniedHandler(new MemberAccessDeniedHandler())
                .and()
                .apply(new CustomFilterConfigurer())
                .and()
                .authorizeHttpRequests(authorize -> authorize
//                        버전 아무거나 대응할 수 있다.
                        .antMatchers(HttpMethod.POST,"/*/members").permitAll()
                        .antMatchers(HttpMethod.PATCH, "/*/members/**").hasRole("USER")
                        .antMatchers(HttpMethod.GET,"/*/members").hasRole("ADMIN")
                        .antMatchers(HttpMethod.GET,"/*/members/**").hasAnyRole("USER","ADMIN")
                        .antMatchers(HttpMethod.DELETE,"/*/members/**").hasRole("USER")

                        .antMatchers(HttpMethod.POST,"/*/questions").hasRole("USER")
                        .antMatchers(HttpMethod.PATCH, "/*/questions/**").hasRole("USER")
                        .antMatchers(HttpMethod.GET,"/*/questions").hasAnyRole("USER","ADMIN")
                        .antMatchers(HttpMethod.GET,"/*/questions/**").hasAnyRole("USER","ADMIN")
                        .antMatchers(HttpMethod.DELETE,"/*/questions/**").hasRole("USER")

                        .antMatchers(HttpMethod.POST,"/*/answers").hasRole("USER")
                        .antMatchers(HttpMethod.PATCH, "/*/answers/**").hasAnyRole("USER","ADMIN")
                        .antMatchers(HttpMethod.GET,"/*/answers").hasAnyRole("USER","ADMIN")
                        .antMatchers(HttpMethod.GET,"/*/answers/**").hasAnyRole("USER","ADMIN")
                        .antMatchers(HttpMethod.DELETE,"/*/answers/**").hasRole("USER")
                        .anyRequest().permitAll());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
    //     .cors(Customizer.withDefaults()) 얘를 따라간다.
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
//        어떤 origin이든 cors 발생 안시킬게요
        configuration.setAllowedOrigins(Arrays.asList("*"));
//        origin이 다를 떄는 GET","POST","PATCH","DELETE 이 네개의 요청만 받을게요
        configuration.setAllowedMethods(List.of("GET","POST","PATCH","DELETE"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        소스에 등록
        source.registerCorsConfiguration("/**",configuration);
        return source;
    }
    //    customFiler는  AbstractHttpConfigurer 얘를 상속 받는다. //    우리가 만든 filter를 등록
    public class CustomFilterConfigurer extends AbstractHttpConfigurer<CustomFilterConfigurer, HttpSecurity> {
        @Override
        public void configure(HttpSecurity builder) {
            AuthenticationManager authenticationManager = builder.getSharedObject(AuthenticationManager.class);
//            fiter를 매니저에 등록하자.
            JwtAuthenticationFilter jwtAuthenticationFilter= new JwtAuthenticationFilter(authenticationManager, jwtTokenizer);
//            특정 url로의 접근에만 활성화되게 지정한다. 변경안하면 default는 /login 이다.
            jwtAuthenticationFilter.setFilterProcessesUrl("/v12/auth/login");
//            new MemberAuthenticationSuccessHandler() 얘는 여기서 쓰기 때문에 굳이 DI하지 않아요
            jwtAuthenticationFilter.setAuthenticationSuccessHandler(new MemberAuthenticationSuccessHandler());
            jwtAuthenticationFilter.setAuthenticationFailureHandler(new MemberAuthenticationFailureHandler());

            JwtVerificationFilter jwtVerificationFilter = new JwtVerificationFilter(jwtTokenizer, authorityUtils);

//            builder로 filter 등록 filterChain에 추가된다.
//            인증 뒤 검증되는 filter를 추가하겠다.
            builder.addFilter(jwtAuthenticationFilter)
//                    filter 추가도 가능한데 꼭 addFilterAfter로 추가한다. --> 검증은 header에 토큰이 들어올때만 적용되면 된다.
                    .addFilterAfter(jwtVerificationFilter, JwtAuthenticationFilter.class);
        }
    }
}
