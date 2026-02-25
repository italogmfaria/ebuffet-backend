package com.ebuffet.repository;

import com.ebuffet.models.Arquivo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArquivoRepository extends JpaRepository<Arquivo, Long> {
}
