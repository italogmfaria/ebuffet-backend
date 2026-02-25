package com.ebuffet.service.impl;

import com.ebuffet.controller.dto.buffet.BuffetResponse;
import com.ebuffet.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String remetente;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void enviarCodigoRecuperacao(String destinatario, String codigo, BuffetResponse buffet) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(remetente);
            message.setTo(destinatario);
            message.setSubject(buffet.getNome() + " - Código de recuperação de senha");
            message.setText(
                    "Olá!\n\n" +
                            "Você solicitou a recuperação de senha para o app " + buffet.getNome() + ".\n\n" +
                            "Seu código de verificação é: " + codigo + "\n\n" +
                            "Este código é válido por 15 minutos.\n\n" +
                            "Se você não solicitou esta recuperação, ignore este e-mail.\n\n" +
                            "Atenciosamente,\nEquipe " + buffet.getNome()
            );

            mailSender.send(message);
            log.info("E-mail de recuperação enviado para: {}", destinatario);
        } catch (Exception e) {
            log.error("Erro ao enviar e-mail de recuperação para {}: {}", destinatario, e.getMessage());
            throw new RuntimeException("Erro ao enviar e-mail de recuperação. Tente novamente mais tarde.");
        }
    }

}
