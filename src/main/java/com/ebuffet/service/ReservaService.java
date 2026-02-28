package com.ebuffet.service;

import com.ebuffet.controller.dto.reserva.AprovarReservaRequest;
import com.ebuffet.controller.dto.reserva.ReservaRequest;
import com.ebuffet.controller.dto.reserva.ReservaResponse;
import com.ebuffet.controller.dto.reserva.ReservaUpdateRequest;
import jakarta.annotation.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReservaService {

    ReservaResponse criarReserva(Long clienteId, ReservaRequest req);

    ReservaResponse atualizarReserva(Long reservaId, Long clienteId, ReservaUpdateRequest req);

    ReservaResponse aprovarReserva(Long reservaId, Long ownerId, AprovarReservaRequest req);

    ReservaResponse recusarReserva(Long reservaId, Long ownerId, @Nullable String motivo);

    ReservaResponse cancelarReservaPeloBuffet(Long reservaId, Long ownerId, @Nullable String motivo);

    ReservaResponse reverterCancelamentoReserva(Long reservaId, Long ownerId);

    ReservaResponse cancelarReservaPeloCliente(Long reservaId, Long clienteId, @Nullable String motivo);

    ReservaResponse atualizarCardapioEServicos(Long reservaId, Long solicitanteId,
                                               List<Long> comidaIds, List<Long> servicoIds,
                                               boolean solicitanteEhBuffetOwner);

    ReservaResponse getById(Long reservaId, Long clienteId);

    Page<ReservaResponse> listarPorCliente(Long clienteId, Pageable pageable);

    Page<ReservaResponse> listarPorBuffet(Long ownerId, Pageable pageable);

}
