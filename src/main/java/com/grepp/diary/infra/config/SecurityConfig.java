package com.grepp.diary.infra.config;

import static org.springframework.http.HttpMethod.GET;

import com.grepp.diary.app.model.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.http.HttpMethod.GET;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Value("${remember-me.key}")
    private String rememberMeKey;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(AuthService authService) {
        return authService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, UserDetailsService userDetailsService) throws Exception {
        http
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/api/**", "/admin/**", "/member/**", "/diary/**", "/app/**", "/ai/**", "/auth/**")
            )
            .formLogin(login -> login
                .loginPage("/")
                .loginProcessingUrl("/none")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/auth/logout")
                .logoutSuccessUrl("/")
                .deleteCookies("JSESSIONID", "remember-me")
                .invalidateHttpSession(true)
                .permitAll()
            )
            .rememberMe(rememberMe -> rememberMe
                .key(rememberMeKey)
                .userDetailsService(userDetailsService)
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(GET, "/", "/css/**", "/js/**", "/images/**", "/assets/**").permitAll()
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/member/login", "/member/logout", "/member/find_id", "/member/find_pw", "/member/regist/**", "/member/regist-mail","/member/auth-id","/member/auth-pw").permitAll()
                .requestMatchers("/member/auth-id", "/member/auth-pw", "/member/change-pw", "/member/find-idpw").permitAll()
                .requestMatchers("/custom/**").permitAll()
//                .anyRequest().permitAll() // 개발 중 전체 열기
                .anyRequest().authenticated()
            );

        return http.build();
    }
}
