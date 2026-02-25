package com.ebuffet.repository;

import com.ebuffet.models.Buffet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BuffetRepository extends JpaRepository<Buffet, Long> {

    Optional<Buffet> findByEmail(String email);

    Page<Buffet> findByOwnerId(Long ownerId, Pageable pageable);

    boolean existsByIdAndOwnerId(Long id, Long ownerId);

    boolean existsByOwnerId(Long ownerId);
}
