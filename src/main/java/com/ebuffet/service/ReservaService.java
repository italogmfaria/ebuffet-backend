package com.ebuffet.service;

import com.ebuffet.controller.dto.register.RegisterRequest;
import com.ebuffet.controller.dto.reserva.AprovarReservaRequest;
import com.ebuffet.controller.dto.reserva.ReservaRequest;
import com.ebuffet.controller.dto.reserva.ReservaResponse;
import com.ebuffet.controller.dto.reserva.ReservaUpdateRequest;
import com.ebuffet.models.User;
import jakarta.annotation.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface ReservaService {

    ReservaResponse criarReserva(Long buffetId, Long clienteId, ReservaRequest req);

    ReservaResponse atualizarReserva(Long buffetId, Long reservaId, Long clienteId, ReservaUpdateRequest req);

    ReservaResponse aprovarReserva(Long buffetId,Long reservaId, Long ownerId, AprovarReservaRequest req);

    ReservaResponse recusarReserva(Long buffetId, Long reservaId, Long ownerId, @Nullable String motivo);

    ReservaResponse cancelarReservaPeloBuffet(Long buffetId, Long reservaId, Long ownerId, @Nullable String motivo);

    ReservaResponse reverterCancelamentoReserva(Long buffetId, Long reservaId, Long ownerId);

    ReservaResponse cancelarReservaPeloCliente(Long buffetId, Long reservaId, Long clienteId, @Nullable String motivo);

    ReservaResponse atualizarCardapioEServicos(Long buffetId, Long reservaId, Long solicitanteId,
                                               List<Long> comidaIds, List<Long> servicoIds,
                                               boolean solicitanteEhBuffetOwner);

    ReservaResponse getById(Long buffetId, Long reservaId, Long clienteId);

    Page<ReservaResponse> listarPorCliente(Long buffetId, Long clienteId, Pageable pageable);

    Page<ReservaResponse> listarPorBuffet(Long buffetId, Long ownerId, Pageable pageable);

}
