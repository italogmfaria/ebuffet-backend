package com.ebuffet.service;

public interface PasswordRecoveryService {
    void enviarCodigo(String email, Long buffetId);
    boolean verificarCodigo(String email, String codigo, Long buffetId);
    void redefinirSenha(String email, String codigo, String novaSenha, Long buffetId);
}
