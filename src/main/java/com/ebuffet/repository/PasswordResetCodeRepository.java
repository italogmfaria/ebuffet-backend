package com.ebuffet.repository;

import com.ebuffet.models.PasswordResetCode;
import com.ebuffet.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PasswordResetCodeRepository extends JpaRepository<PasswordResetCode, Long> {

    Optional<PasswordResetCode> findByUsuarioAndCodigoAndUtilizadoFalseAndExpiracaoAfter(
            User usuario, String codigo, LocalDateTime now);

    List<PasswordResetCode> findByUsuarioAndUtilizadoFalse(User usuario);

    void deleteByExpiracaoBefore(LocalDateTime dateTime);
}
