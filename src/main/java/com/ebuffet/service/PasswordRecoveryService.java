package com.ebuffet.service;

public interface PasswordRecoveryService {
    void enviarCodigo(String email);
    boolean verificarCodigo(String email, String codigo);
    void redefinirSenha(String email, String codigo, String novaSenha);
}
