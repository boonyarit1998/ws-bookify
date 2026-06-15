package com.ws.bookify.controller;

import com.ws.bookify.dto.LoginRequest;
import com.ws.bookify.dto.RegisterRequest;
import com.ws.bookify.service.AuthService;
import com.ws.bookify.util.ResponseEntityUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller layer — authentication.
 * register/login เปิด public (ดู SecurityConfig); me ต้อง login ก่อน.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /** POST /api/auth/register — สมัครสมาชิก แล้วได้ token กลับเลย */
    @PostMapping("/register")
    public ResponseEntity<Object> register(@Valid @RequestBody RegisterRequest request,
                                           HttpServletRequest httpRequest) {
        return ResponseEntityUtil.returnDataObject(httpRequest, authService.register(request));
    }

    /** POST /api/auth/login — เข้าสู่ระบบด้วย email + password */
    @PostMapping("/login")
    public ResponseEntity<Object> login(@Valid @RequestBody LoginRequest request,
                                        HttpServletRequest httpRequest) {
        return ResponseEntityUtil.returnDataObject(httpRequest, authService.login(request));
    }

    /** GET /api/auth/me — ข้อมูลของ user ที่ login อยู่ */
    @GetMapping("/me")
    public ResponseEntity<Object> me(HttpServletRequest httpRequest) {
        return ResponseEntityUtil.returnDataObject(httpRequest, authService.me());
    }
}
