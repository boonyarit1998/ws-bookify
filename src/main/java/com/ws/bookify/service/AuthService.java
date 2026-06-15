package com.ws.bookify.service;

import com.ws.bookify.dto.AuthResponse;
import com.ws.bookify.dto.LoginRequest;
import com.ws.bookify.dto.RegisterRequest;
import com.ws.bookify.dto.UserResponse;
import com.ws.bookify.entity.User;
import com.ws.bookify.exception.DuplicateResourceException;
import com.ws.bookify.exception.InvalidCredentialsException;
import com.ws.bookify.exception.ResourceNotFoundException;
import com.ws.bookify.repository.UserRepository;
import com.ws.bookify.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service layer — authentication: สมัครสมาชิก, เข้าสู่ระบบ, ดูข้อมูลตัวเอง.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    /** สมัครสมาชิก — เช็ค email/username ซ้ำ, hash password, แล้วออก token ให้เลย */
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("email " + request.email() + " is already registered");
        }
        if (userRepository.existsByUsername(request.username())) {
            throw new DuplicateResourceException("username " + request.username() + " is already taken");
        }

        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));

        return buildAuthResponse(userRepository.save(user));
    }

    /** เข้าสู่ระบบด้วย email + password */
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new InvalidCredentialsException("invalid email or password"));
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("invalid email or password");
        }
        return buildAuthResponse(user);
    }

    /** ข้อมูลของ user ที่ login อยู่ */
    @Transactional(readOnly = true)
    public UserResponse me() {
        Long userId = SecurityUtils.currentUserId();
        return UserResponse.from(userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId)));
    }

    private AuthResponse buildAuthResponse(User user) {
        String token = jwtService.generateToken(user);
        return new AuthResponse(token, "Bearer", jwtService.getExpirationSeconds(), UserResponse.from(user));
    }
}
