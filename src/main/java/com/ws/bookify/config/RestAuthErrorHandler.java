package com.ws.bookify.config;

import com.ws.bookify.dto.ApiResponse;
import com.ws.bookify.util.GsonUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

/**
 * แปลง error จาก security filter (เกิดก่อนเข้า controller) ให้เป็น ApiResponse envelope
 * เหมือน error อื่นๆ — เพื่อให้ client เจอรูปแบบเดียวกันทั้งหมด.
 *
 * - AuthenticationEntryPoint: ไม่มี/ token ไม่ถูกต้อง -> 401
 * - AccessDeniedHandler:      login แล้วแต่ไม่มีสิทธิ์ -> 403
 */
@Component
public class RestAuthErrorHandler implements AuthenticationEntryPoint, AccessDeniedHandler {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        write(request, response, HttpStatus.UNAUTHORIZED, "authentication required");
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        write(request, response, HttpStatus.FORBIDDEN, "access denied");
    }

    private void write(HttpServletRequest request, HttpServletResponse response,
                       HttpStatus status, String message) throws IOException {
        ApiResponse<Void> body = ApiResponse.error(message, null).withPath(request.getRequestURI());
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(GsonUtil.toJson(body));
    }
}
