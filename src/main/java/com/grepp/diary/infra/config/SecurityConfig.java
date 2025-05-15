package com.grepp.diary.infra.config;

import static org.springframework.http.HttpMethod.GET;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .formLogin(login -> login
                .loginPage("/")
                .loginProcessingUrl("/none")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .permitAll()
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(GET, "/", "/css/**", "/js/**", "/images/**", "/assets/**").permitAll()
                    .requestMatchers("/member/login", "/member/find_id", "/member/find_pw", "/member/regist/**", "/member/regist-mail","/member/auth-id","member/auth-pw").permitAll()
                    .requestMatchers("/member/auth-id", "/member/auth-pw", "/member/change-pw").permitAll()
                .anyRequest().permitAll() // 개발 중 전체 열기
//                .anyRequest().authenticated()
            );
        return http.build();
    }
}

