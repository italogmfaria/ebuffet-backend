package com.ebuffet.service;

import com.ebuffet.controller.dto.notificacao.NotificacaoResponse;
import com.ebuffet.models.Evento;
import com.ebuffet.models.Reserva;
import com.ebuffet.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface NotificacaoService {

    Page<NotificacaoResponse> listByUsuario(Long usuarioId, Pageable pageable);

    Long countUnread(Long usuarioId);

    NotificacaoResponse markAsRead(Long notificacaoId, Long usuarioId);

    void delete(Long notificacaoId, Long usuarioId);

    void criarNotificacaoReservaAprovada(Reserva reserva, User cliente);

    void criarNotificacaoNovaReserva(Reserva reserva, User buffetOwner);

    void criarNotificacaoReservaRecusada(Reserva reserva, User cliente, String motivo);

    void criarNotificacaoReservaCanceladaPeloBuffet(Reserva reserva, User cliente, String motivo);

    void criarNotificacaoReservaReativada(Reserva reserva, User cliente);

    void criarNotificacaoEventoCanceladoPeloBuffet(Evento evento, User cliente);

    void criarNotificacaoEventoValorAtualizado(Evento evento, User cliente, BigDecimal valorAntigo, BigDecimal valorNovo);

    void criarNotificacaoEventoReativado(Evento evento, User cliente);

    void criarNotificacaoEventoCanceladoPeloCliente(Evento evento, User buffetOwner);
}
