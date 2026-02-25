package com.ebuffet.service.impl;

import com.ebuffet.controller.dto.login.AuthRequest;
import com.ebuffet.controller.dto.login.AuthResponse;
import com.ebuffet.models.User;
import com.ebuffet.repository.UserRepository;
import com.ebuffet.service.AuthService;
import com.ebuffet.utils.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(PasswordEncoder passwordEncoder, JwtUtil jwtUtil, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Override
    public AuthResponse login(AuthRequest request, Long buffetId) {
        try {
            User user = userRepository.findByEmailIgnoreCaseAndBuffetId(
                            request.getUsername(),
                            buffetId
                    )
                    .or(() -> userRepository.findByTelefoneAndBuffetId(
                            request.getUsername(),
                            buffetId
                    ))
                    .orElseThrow(() -> new AuthenticationException("Usuário não encontrado neste buffet") {});

            if (!passwordEncoder.matches(request.getPassword(), user.getSenha())) {
                throw new AuthenticationException("Credenciais inválidas") {};
            }

            Authentication auth = new UsernamePasswordAuthenticationToken(
                    user,
                    null,
                    user.getAuthorities()
            );
            SecurityContextHolder.getContext().setAuthentication(auth);

            String token = jwtUtil.generateToken(user);

            return new AuthResponse(token, buffetId, user.getRole());

        } catch (AuthenticationException e) {
            throw new AuthenticationException("Credenciais inválidas ou buffet incorreto") {};
        }
    }
}
