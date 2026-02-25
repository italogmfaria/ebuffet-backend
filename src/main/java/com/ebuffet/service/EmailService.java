package com.ebuffet.service;

import com.ebuffet.controller.dto.buffet.BuffetResponse;

public interface EmailService {
    void enviarCodigoRecuperacao(String destinatario, String codigo, BuffetResponse buffet);
}
