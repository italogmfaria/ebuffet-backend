package com.ebuffet.repository;

import com.ebuffet.models.Buffet;
import com.ebuffet.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmailIgnoreCase(String email);

    Optional<User> findByTelefone(String telefone);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByTelefone(String telefone);
}
