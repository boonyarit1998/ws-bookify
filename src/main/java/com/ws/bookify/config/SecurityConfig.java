package com.ws.bookify.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security config ชั่วคราวสำหรับ dev: เปิดทุก endpoint ให้เรียกได้
 * เพื่อโฟกัสที่การเรียนรู้ CRUD ก่อน.
 *
 * TODO: เมื่อทำ auth จริง ให้เปลี่ยนเป็น validate JWT จาก Supabase
 * (.oauth2ResourceServer(oauth2 -> oauth2.jwt(...))) และบังคับ authenticated().
 */
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }
}
