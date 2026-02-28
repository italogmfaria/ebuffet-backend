package com.ebuffet.service.impl;

import com.ebuffet.config.SingleBuffetProperties;
import com.ebuffet.controller.dto.buffet.BuffetResponse;
import com.ebuffet.controller.exceptions.NotFoundException;
import com.ebuffet.models.PasswordResetCode;
import com.ebuffet.models.User;
import com.ebuffet.repository.PasswordResetCodeRepository;
import com.ebuffet.repository.UserRepository;
import com.ebuffet.service.BuffetService;
import com.ebuffet.service.EmailService;
import com.ebuffet.service.PasswordRecoveryService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PasswordRecoveryServiceImpl implements PasswordRecoveryService {

    private static final int CODIGO_TAMANHO = 4;
    private static final int EXPIRACAO_MINUTOS = 15;
    private static final String CARACTERES = "0123456789";

    private final UserRepository userRepository;
    private final PasswordResetCodeRepository resetCodeRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom random = new SecureRandom();
    private final BuffetService buffetService;
    private final SingleBuffetProperties singleBuffetProperties;

    public PasswordRecoveryServiceImpl(UserRepository userRepository,
                                       PasswordResetCodeRepository resetCodeRepository,
                                       EmailService emailService,
                                       PasswordEncoder passwordEncoder,
                                       BuffetService buffetService,
                                       SingleBuffetProperties singleBuffetProperties) {
        this.userRepository = userRepository;
        this.resetCodeRepository = resetCodeRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
        this.buffetService = buffetService;
        this.singleBuffetProperties = singleBuffetProperties;
    }

    @Transactional
    @Override
    public void enviarCodigo(String email) {
        User user = userRepository.findByEmailIgnoreCase(email.trim().toLowerCase())
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado com este e-mail."));

        BuffetResponse buffet = buffetService.get(singleBuffetProperties.getBuffetId());

        List<PasswordResetCode> codigosAnteriores = resetCodeRepository.findByUsuarioAndUtilizadoFalse(user);
        codigosAnteriores.forEach(c -> c.setUtilizado(true));
        resetCodeRepository.saveAll(codigosAnteriores);

        String codigo = gerarCodigo();

        PasswordResetCode resetCode = new PasswordResetCode();
        resetCode.setUsuario(user);
        resetCode.setCodigo(codigo);
        resetCode.setExpiracao(LocalDateTime.now().plusMinutes(EXPIRACAO_MINUTOS));
        resetCode.setUtilizado(false);
        resetCodeRepository.save(resetCode);

        emailService.enviarCodigoRecuperacao(user.getEmail(), codigo, buffet);
    }

    @Override
    public boolean verificarCodigo(String email, String codigo) {
        User user = userRepository.findByEmailIgnoreCase(email.trim().toLowerCase())
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado com este e-mail."));

        return resetCodeRepository
                .findByUsuarioAndCodigoAndUtilizadoFalseAndExpiracaoAfter(user, codigo, LocalDateTime.now())
                .isPresent();
    }

    @Transactional
    @Override
    public void redefinirSenha(String email, String codigo, String novaSenha) {
        User user = userRepository.findByEmailIgnoreCase(email.trim().toLowerCase())
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado com este e-mail."));

        PasswordResetCode resetCode = resetCodeRepository
                .findByUsuarioAndCodigoAndUtilizadoFalseAndExpiracaoAfter(user, codigo, LocalDateTime.now())
                .orElseThrow(() -> new IllegalArgumentException("Código inválido ou expirado."));

        resetCode.setUtilizado(true);
        resetCodeRepository.save(resetCode);

        user.setSenha(passwordEncoder.encode(novaSenha));
        userRepository.save(user);
    }

    private String gerarCodigo() {
        StringBuilder sb = new StringBuilder(CODIGO_TAMANHO);
        for (int i = 0; i < CODIGO_TAMANHO; i++) {
            sb.append(CARACTERES.charAt(random.nextInt(CARACTERES.length())));
        }
        return sb.toString();
    }
}
