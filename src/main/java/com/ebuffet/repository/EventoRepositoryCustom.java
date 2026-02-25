package com.ebuffet.repository;

import com.ebuffet.models.Evento;
import com.ebuffet.models.enums.EnumStatus;
import com.ebuffet.models.enums.EnumStatusEvento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface EventoRepositoryCustom {

    Page<Evento> findByFilters(Long buffetId,
                               EnumStatusEvento statusEvento,
                               EnumStatus status,
                               LocalDate dataEventoFrom,
                               LocalDate dataEventoTo,
                               Pageable pageable);
}
