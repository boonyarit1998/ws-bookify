package com.ws.bookify.config;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security config — self-issued JWT (HS256) + stateless resource server.
 *
 * - register/login เปิด public; ที่เหลือต้องแนบ Bearer token ที่แอปออกเอง
 * - JwtEncoder ใช้ตอนออก token (login/register), JwtDecoder ใช้ตอน validate ทุก request
 * - error 401/403 จาก filter ถูกแปลงเป็น ApiResponse envelope ผ่าน RestAuthErrorHandler
 */
@Configuration
public class SecurityConfig {

    private final SecretKey jwtKey;

    public SecurityConfig(@Value("${app.jwt.secret}") String secret) {
        // HS256 ต้องการ key อย่างน้อย 256 bit (32 bytes) — ดู .env.example
        this.jwtKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   RestAuthErrorHandler authErrorHandler) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/register", "/api/auth/login").permitAll()
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> {}))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authErrorHandler)
                        .accessDeniedHandler(authErrorHandler));
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withSecretKey(jwtKey).macAlgorithm(MacAlgorithm.HS256).build();
    }

    @Bean
    public JwtEncoder jwtEncoder() {
        return new NimbusJwtEncoder(new ImmutableSecret<>(jwtKey));
    }
}
