package com.ebuffet.repository;

import com.ebuffet.models.Reserva;
import com.ebuffet.models.enums.EnumStatus;
import com.ebuffet.models.enums.EnumStatusReserva;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    Page<Reserva> findByClienteId(Long clienteId, Pageable pageable);

    Page<Reserva> findByClienteIdAndBuffetId(Long clienteId, Long buffetId, Pageable pageable);

    Optional<Reserva> findByIdAndClienteId(Long reservaId, Long clienteId);

    Optional<Reserva> findByIdAndClienteIdAndBuffetId(Long reservaId, Long clienteId, Long buffetId);

    Page<Reserva> findByBuffetId(Long buffetId, Pageable pageable);

    @Query("""
    SELECT r FROM Reserva r
    WHERE r.statusReserva = :statusReserva
      AND r.status = :status
      AND r.dataDesejada < :hoje
    """)
    List<Reserva> findReservasExpiradas(
            @Param("statusReserva") EnumStatusReserva statusReserva,
            @Param("status") EnumStatus status,
            @Param("hoje") LocalDate hoje
    );
}
