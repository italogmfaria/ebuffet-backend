package com.ebuffet.repository;

import com.ebuffet.models.Comida;
import com.ebuffet.models.enums.EnumCategoria;
import com.ebuffet.models.enums.EnumStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface ComidaRepository extends JpaRepository<Comida, Long> {

    Page<Comida> findByBuffetId(Long buffetId, Pageable pageable);

    Page<Comida> findByBuffetIdAndCategoria(Long buffetId, EnumCategoria categoria, Pageable pageable);

    Page<Comida> findByBuffetIdAndStatus(Long buffetId, EnumStatus status, Pageable pageable);

    boolean existsByIdAndBuffetId(Long id, Long buffetId);

    List<Comida> findByIdIn(Collection<Long> ids);
}
