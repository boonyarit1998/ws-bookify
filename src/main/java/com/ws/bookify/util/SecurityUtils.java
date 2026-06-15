package com.ws.bookify.util;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

/**
 * Helper สำหรับดึงข้อมูล user ปัจจุบันจาก SecurityContext.
 * หลัง validate JWT แล้ว Spring จะใส่ principal เป็น {@link Jwt} ซึ่ง subject = user id.
 */
public final class SecurityUtils {

    private SecurityUtils() {
    }

    /** id ของ user ที่ login อยู่ (อ่านจาก JWT subject) */
    public static Long currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof Jwt jwt)) {
            throw new AuthenticationCredentialsNotFoundException("no authenticated user in context");
        }
        return Long.valueOf(jwt.getSubject());
    }
}
