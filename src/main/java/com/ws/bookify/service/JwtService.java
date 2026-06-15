package com.ws.bookify.service;

import com.ws.bookify.entity.User;
import java.time.Instant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

/**
 * ออก JWT (access token) ที่แอปลงนามเอง ด้วย HS256.
 * subject = user id, พร้อม claim username/email เพื่อความสะดวกฝั่ง client.
 */
@Service
public class JwtService {

    private final JwtEncoder encoder;
    private final long expirationSeconds;

    public JwtService(JwtEncoder encoder,
                      @Value("${app.jwt.expiration}") long expirationSeconds) {
        this.encoder = encoder;
        this.expirationSeconds = expirationSeconds;
    }

    /** สร้าง token สำหรับ user หลัง register/login สำเร็จ */
    public String generateToken(User user) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("bookify")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expirationSeconds))
                .subject(String.valueOf(user.getId()))
                .claim("username", user.getUsername())
                .claim("email", user.getEmail())
                .build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }

    public long getExpirationSeconds() {
        return expirationSeconds;
    }
}
