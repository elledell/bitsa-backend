package com.website.bitsa.service;

import com.website.bitsa.dto.request.LoginRequest;
import com.website.bitsa.dto.request.RegisterRequest;
import com.website.bitsa.dto.response.AuthResponse;
import com.website.bitsa.exception.BadRequestException;
import com.website.bitsa.exception.UnauthorizedException;
import com.website.bitsa.model.Role;
import com.website.bitsa.model.User;
import com.website.bitsa.repository.RoleRepository;
import com.website.bitsa.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest; // <-- 1. New Import
import jakarta.servlet.http.HttpSession;      // <-- 2. New Import
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext; // <-- 3. New Import
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository; // <-- 4. New Import
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    // Inject the request so we can access the session directly
    private final HttpServletRequest httpRequest;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered");
        }
        if (userRepository.existsByStudentId(request.getStudentId())) {
            throw new BadRequestException("Student ID already registered");
        }
        Role studentRole = roleRepository.findByName("STUDENT")
                .orElseThrow(() -> new BadRequestException("Student role not found"));
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .studentId(request.getStudentId())
                .course(request.getCourse())
                .year(request.getYear())
                .role(studentRole)
                .isActive(true)
                .isEmailVerified(false)
                .build();
        userRepository.save(user);
        return AuthResponse.builder()
                .message("Registration successful! Please login.")
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        // 1. Authenticate
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // 2. Set the Context
        SecurityContext sc = SecurityContextHolder.getContext();
        sc.setAuthentication(authentication);

        // 3. FORCE SAVE TO SESSION (The Fix)
        // We manually grab the session and save the security context into it.
        HttpSession session = httpRequest.getSession(true);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, sc);

        // 4. Get User Details
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Error finding user after login"));

        if (!user.getIsActive()) {
            throw new UnauthorizedException("Account is deactivated");
        }

        user.setLastLogin(java.time.LocalDateTime.now());
        userRepository.save(user);

        return AuthResponse.builder()
                .token(null)
                .type("Session")
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().getName())
                .message("Login successful")
                .build();
    }
}