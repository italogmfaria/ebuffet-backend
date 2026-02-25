package com.ebuffet.repository;

import com.ebuffet.models.Buffet;
import com.ebuffet.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmailAndBuffetId(String email, Long buffetId);

    Optional<User> findByTelefoneAndBuffetId(String telefone, Long buffetId);

    Optional<User> findByEmailIgnoreCaseAndBuffetId(String email, Long buffetId);

    boolean existsByEmailIgnoreCaseAndBuffetId(String email, Long buffetId);

    boolean existsByTelefoneAndBuffetId(String telefone, Long buffetId);

    List<User> findByBuffetId(Long buffetId);

    Optional<User> findByEmailIgnoreCase(String email);

    Optional<User> findByTelefone(String telefone);
}
