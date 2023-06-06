
package com.latteis.eumcoding.config;


import com.latteis.eumcoding.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.filter.CorsFilter;

@EnableWebSecurity
@Configuration
public class WebSecurityConfig {
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private static final String[] SWAGGER_URI = {
            "/swagger-ui.html", "/v2/api-docs", "/swagger-resources/**", "/webjars/**", "/swagger/**"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // http 시큐리티 빌더
        http.cors() //WebMvcConfig에서 이미 설정 했으므로 기본 cors 설정
                .and().csrf() // csrf 사용하지 않음
                .disable().httpBasic() // basic 사용하지 않음
                .disable().sessionManagement() // session 기반 아님
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().authorizeRequests() // 인증안해도 되는 경로 설정
                .antMatchers(SWAGGER_URI).permitAll()
                .antMatchers("/",
                        "/unauth/**",
                        "/partners/member/**",
                        "/nonmember/**",
                        "/board/unauth/**",
                        "/board/comment/unauth/**",
                        "/lecture/unauth/**",
                        "/lecture/heart/**",
                        "/lecture/review/unauth/**",
                        "/lecture/question/unauth/**",
                        "/lecture/section/unauth/**",
                        "/eumCodingImgs/**",
                        "/images/menu/**",
                        "/images/**",
                        "/gender/**",
                        "/error",
                        "/confirm/**",
                        "/google/**",
                        "/login/**").permitAll()
                .anyRequest().authenticated(); // 인증 안해도 되는 경로 설정                ;

        //filter 등록
        //매 요청마다
        //CorsFilter 실행한 후에
        //jwtAuthenticationFilter 실행
        http.addFilterAfter(jwtAuthenticationFilter, CorsFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() { // 회원가입 시 패스워드를 암호화하기 위한 객체
        return new BCryptPasswordEncoder();
    }


}


