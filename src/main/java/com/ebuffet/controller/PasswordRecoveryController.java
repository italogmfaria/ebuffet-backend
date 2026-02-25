package com.ebuffet.controller;

import com.ebuffet.controller.dto.password.ForgotPasswordRequest;
import com.ebuffet.controller.dto.password.ResetPasswordRequest;
import com.ebuffet.controller.dto.password.VerifyCodeRequest;
import com.ebuffet.service.PasswordRecoveryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.ebuffet.utils.Constants.BUFFET_ID_HEADER;

@RestController
@RequestMapping("/api/auth/password")
public class PasswordRecoveryController {

    private final PasswordRecoveryService passwordRecoveryService;

    public PasswordRecoveryController(PasswordRecoveryService passwordRecoveryService) {
        this.passwordRecoveryService = passwordRecoveryService;
    }

    @PostMapping("/forgot")
    public ResponseEntity<Map<String, String>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request,
            @RequestHeader(value = BUFFET_ID_HEADER) Long buffetId) {

        passwordRecoveryService.enviarCodigo(request.getEmail(), buffetId);
        return ResponseEntity.ok(Map.of("message", "Código de recuperação enviado para o e-mail informado."));
    }

    @PostMapping("/verify-code")
    public ResponseEntity<Map<String, Object>> verifyCode(
            @Valid @RequestBody VerifyCodeRequest request,
            @RequestHeader(value = BUFFET_ID_HEADER) Long buffetId) {

        boolean valido = passwordRecoveryService.verificarCodigo(
                request.getEmail(), request.getCodigo(), buffetId);

        return ResponseEntity.ok(Map.of("valido", valido));
    }

    @PostMapping("/reset")
    public ResponseEntity<Map<String, String>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request,
            @RequestHeader(value = BUFFET_ID_HEADER) Long buffetId) {

        passwordRecoveryService.redefinirSenha(
                request.getEmail(), request.getCodigo(), request.getNovaSenha(), buffetId);

        return ResponseEntity.ok(Map.of("message", "Senha redefinida com sucesso."));
    }
}
