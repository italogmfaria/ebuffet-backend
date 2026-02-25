package com.ebuffet.repository;

import com.ebuffet.models.Evento;
import com.ebuffet.models.enums.EnumStatus;
import com.ebuffet.models.enums.EnumStatusEvento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface EventoRepository extends JpaRepository<Evento, Long>, EventoRepositoryCustom {

    @Query("""
    SELECT e FROM Evento e
    WHERE e.statusEvento IN (:statuses)
      AND e.status = :status
      AND e.dataEvento < :hoje
    """)
    List<Evento> findEventosExpirados(
            @Param("statuses") List<EnumStatusEvento> statuses,
            @Param("status") EnumStatus status,
            @Param("hoje") LocalDate hoje
    );

    @Query("""
    SELECT DISTINCT e.dataEvento
    FROM Evento e
    WHERE e.buffet.id = :buffetId
      AND e.statusEvento IN :statuses
      AND e.status = :status
      AND e.dataEvento >= :dataInicio
      AND e.dataEvento <= :dataFim
    """)
    List<LocalDate> findDatasOcupadasByBuffetId(
            @Param("buffetId") Long buffetId,
            @Param("statuses") List<EnumStatusEvento> statuses,
            @Param("status") EnumStatus status,
            @Param("dataInicio") LocalDate dataInicio,
            @Param("dataFim") LocalDate dataFim
    );

    @Query("""
    SELECT DISTINCT e.dataEvento
    FROM Evento e
    WHERE e.buffet.id = :buffetId
      AND e.bloquearCalendario = true
      AND e.statusEvento = 'AGENDADO'
      AND e.status = :status
      AND e.dataEvento >= :dataInicio
      AND e.dataEvento <= :dataFim
    """)
    List<LocalDate> findDatasBloquedadasByBuffetId(
            @Param("buffetId") Long buffetId,
            @Param("status") EnumStatus status,
            @Param("dataInicio") LocalDate dataInicio,
            @Param("dataFim") LocalDate dataFim
    );

    @Query("""
    SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END
    FROM Evento e
    WHERE e.buffet.id = :buffetId
      AND e.dataEvento = :dataEvento
      AND e.bloquearCalendario = true
      AND e.statusEvento = 'AGENDADO'
      AND e.status = :status
    """)
    boolean existsDataBloqueada(
            @Param("buffetId") Long buffetId,
            @Param("dataEvento") LocalDate dataEvento,
            @Param("status") EnumStatus status
    );

    @Query("""
      SELECT e
      FROM Evento e
      WHERE e.buffet.id = :buffetId
        AND e.reserva.cliente.id = :clienteId
        AND e.status = :status
      ORDER BY e.dataCriacao DESC
    """)
    Page<Evento> findMeusEventos(
            @Param("buffetId") Long buffetId,
            @Param("clienteId") Long clienteId,
            @Param("status") EnumStatus status,
            Pageable pageable
    );
}

