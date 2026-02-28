package com.ebuffet.controller;

import com.ebuffet.controller.dto.password.ForgotPasswordRequest;
import com.ebuffet.controller.dto.password.ResetPasswordRequest;
import com.ebuffet.controller.dto.password.VerifyCodeRequest;
import com.ebuffet.service.PasswordRecoveryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth/password")
public class PasswordRecoveryController {

    private final PasswordRecoveryService passwordRecoveryService;

    public PasswordRecoveryController(PasswordRecoveryService passwordRecoveryService) {
        this.passwordRecoveryService = passwordRecoveryService;
    }

    @PostMapping("/forgot")
    public ResponseEntity<Map<String, String>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {

        passwordRecoveryService.enviarCodigo(request.getEmail());
        return ResponseEntity.ok(Map.of("message", "Código de recuperação enviado para o e-mail informado."));
    }

    @PostMapping("/verify-code")
    public ResponseEntity<Map<String, Object>> verifyCode(
            @Valid @RequestBody VerifyCodeRequest request) {

        boolean valido = passwordRecoveryService.verificarCodigo(
                request.getEmail(), request.getCodigo());

        return ResponseEntity.ok(Map.of("valido", valido));
    }

    @PostMapping("/reset")
    public ResponseEntity<Map<String, String>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {

        passwordRecoveryService.redefinirSenha(
                request.getEmail(), request.getCodigo(), request.getNovaSenha());

        return ResponseEntity.ok(Map.of("message", "Senha redefinida com sucesso."));
    }
}
