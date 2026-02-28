package com.ebuffet.controller;

import com.ebuffet.controller.dto.login.AuthRequest;
import com.ebuffet.controller.dto.login.AuthResponse;
import com.ebuffet.controller.dto.register.RegisterRequest;
import com.ebuffet.controller.dto.register.UserResponse;
import com.ebuffet.models.User;
import com.ebuffet.service.AuthService;
import com.ebuffet.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest request) {
        return authService.login(request);
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest req) {
        User created = userService.register(req);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new UserResponse(created));
    }

    @PostMapping("/register/buffet")
    public ResponseEntity<UserResponse> registerBuffet(@Valid @RequestBody RegisterRequest req) {
        User created = userService.registerBuffet(req);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new UserResponse(created));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(new UserResponse(user));
    }
}
