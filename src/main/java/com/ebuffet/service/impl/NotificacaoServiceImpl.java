package com.ebuffet.service.impl;

import com.ebuffet.controller.dto.notificacao.NotificacaoResponse;
import com.ebuffet.controller.exceptions.ForbiddenException;
import com.ebuffet.controller.exceptions.NotFoundException;
import com.ebuffet.models.Evento;
import com.ebuffet.models.Notificacao;
import com.ebuffet.models.Reserva;
import com.ebuffet.models.User;
import com.ebuffet.models.enums.EnumStatus;
import com.ebuffet.repository.NotificacaoRepository;
import com.ebuffet.service.NotificacaoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class NotificacaoServiceImpl implements NotificacaoService {

    private final NotificacaoRepository repository;

    public NotificacaoServiceImpl(NotificacaoRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    @Override
    public Page<NotificacaoResponse> listByUsuario(Long usuarioId, Pageable pageable) {
        return repository.findByUsuarioIdAndStatus(usuarioId, EnumStatus.ATIVO, pageable)
                .map(NotificacaoResponse::new);
    }

    @Transactional(readOnly = true)
    @Override
    public Long countUnread(Long usuarioId) {
        return repository.countUnreadByUsuarioId(usuarioId, EnumStatus.ATIVO);
    }

    @Transactional
    @Override
    public NotificacaoResponse markAsRead(Long notificacaoId, Long usuarioId) {
        Notificacao notificacao = repository.findById(notificacaoId)
                .orElseThrow(() -> new NotFoundException("Notificação não encontrada"));

        if (!notificacao.getUsuario().getId().equals(usuarioId)) {
            throw new ForbiddenException("Notificação não pertence a este usuário");
        }

        notificacao.setLida(true);
        notificacao.setDataLeitura(LocalDateTime.now());
        repository.save(notificacao);

        return new NotificacaoResponse(notificacao);
    }

    @Transactional
    @Override
    public void delete(Long notificacaoId, Long usuarioId) {
        Notificacao notificacao = repository.findById(notificacaoId)
                .orElseThrow(() -> new NotFoundException("Notificação não encontrada"));

        if (!notificacao.getUsuario().getId().equals(usuarioId)) {
            throw new ForbiddenException("Notificação não pertence a este usuário");
        }

        notificacao.setStatus(EnumStatus.INATIVO);
        repository.save(notificacao);
    }

    @Transactional
    @Override
    public void criarNotificacaoReservaAprovada(Reserva reserva, User cliente) {
        Notificacao notificacao = new Notificacao();
        notificacao.setUsuario(cliente);
        notificacao.setTitulo("Reserva Aprovada!");
        notificacao.setMensagem(
                String.format("Sua reserva foi aprovada pelo buffet %s. " +
                        "Verifique os detalhes e prepare-se para o seu evento!",
                        reserva.getBuffet().getNome())
        );
        notificacao.setReserva(reserva);
        notificacao.setLida(false);
        notificacao.setStatus(EnumStatus.ATIVO);

        repository.save(notificacao);
    }

    @Transactional
    @Override
    public void criarNotificacaoNovaReserva(Reserva reserva, User buffetOwner) {
        Notificacao notificacao = new Notificacao();
        notificacao.setUsuario(buffetOwner);
        notificacao.setTitulo("Nova Reserva Recebida!");
        notificacao.setMensagem(
                String.format("Você recebeu uma nova reserva de %s para %d pessoas. " +
                        "Verifique os detalhes e aprove ou recuse a reserva.",
                        reserva.getCliente().getNome(),
                        reserva.getQtdPessoas())
        );
        notificacao.setReserva(reserva);
        notificacao.setLida(false);
        notificacao.setStatus(EnumStatus.ATIVO);

        repository.save(notificacao);
    }

    @Transactional
    @Override
    public void criarNotificacaoReservaRecusada(Reserva reserva, User cliente, String motivo) {
        Notificacao notificacao = new Notificacao();
        notificacao.setUsuario(cliente);
        notificacao.setTitulo("Reserva Recusada");
        String detalhe = (motivo == null || motivo.isBlank()) ? "" : " Motivo: " + motivo;
        notificacao.setMensagem(
                String.format("Sua reserva no buffet %s foi recusada.%s",
                        reserva.getBuffet().getNome(),
                        detalhe)
        );
        notificacao.setReserva(reserva);
        notificacao.setLida(false);
        notificacao.setStatus(EnumStatus.ATIVO);

        repository.save(notificacao);
    }

    @Transactional
    @Override
    public void criarNotificacaoReservaCanceladaPeloBuffet(Reserva reserva, User cliente, String motivo) {
        Notificacao notificacao = new Notificacao();
        notificacao.setUsuario(cliente);
        notificacao.setTitulo("Reserva Cancelada pelo Buffet");
        String detalhe = (motivo == null || motivo.isBlank()) ? "" : " Motivo: " + motivo;
        notificacao.setMensagem(
                String.format("Sua reserva no buffet %s foi cancelada pelo buffet.%s",
                        reserva.getBuffet().getNome(),
                        detalhe)
        );
        notificacao.setReserva(reserva);
        notificacao.setLida(false);
        notificacao.setStatus(EnumStatus.ATIVO);

        repository.save(notificacao);
    }

    @Transactional
    @Override
    public void criarNotificacaoReservaReativada(Reserva reserva, User cliente) {
        Notificacao notificacao = new Notificacao();
        notificacao.setUsuario(cliente);
        notificacao.setTitulo("Reserva Reativada");
        notificacao.setMensagem(
                String.format("Sua reserva no buffet %s foi reativada.",
                        reserva.getBuffet().getNome())
        );
        notificacao.setReserva(reserva);
        notificacao.setLida(false);
        notificacao.setStatus(EnumStatus.ATIVO);

        repository.save(notificacao);
    }

    @Transactional
    @Override
    public void criarNotificacaoEventoCanceladoPeloBuffet(Evento evento, User cliente) {
        Notificacao notificacao = new Notificacao();
        notificacao.setUsuario(cliente);
        notificacao.setTitulo("Evento Cancelado pelo Buffet");
        notificacao.setMensagem(
                String.format("Seu evento no buffet %s foi cancelado pelo buffet.",
                        evento.getBuffet().getNome())
        );
        if (evento.getReserva() != null) {
            notificacao.setReserva(evento.getReserva());
        }
        notificacao.setLida(false);
        notificacao.setStatus(EnumStatus.ATIVO);

        repository.save(notificacao);
    }

    @Transactional
    @Override
    public void criarNotificacaoEventoValorAtualizado(Evento evento, User cliente, BigDecimal valorAntigo, BigDecimal valorNovo) {
        Notificacao notificacao = new Notificacao();
        notificacao.setUsuario(cliente);
        notificacao.setTitulo("Valor do Evento Atualizado");
        notificacao.setMensagem(
                String.format("O valor do seu evento no buffet %s foi atualizado de R$ %s para R$ %s.",
                        evento.getBuffet().getNome(),
                        valorAntigo,
                        valorNovo)
        );
        if (evento.getReserva() != null) {
            notificacao.setReserva(evento.getReserva());
        }
        notificacao.setLida(false);
        notificacao.setStatus(EnumStatus.ATIVO);

        repository.save(notificacao);
    }

    @Transactional
    @Override
    public void criarNotificacaoEventoReativado(Evento evento, User cliente) {
        Notificacao notificacao = new Notificacao();
        notificacao.setUsuario(cliente);
        notificacao.setTitulo("Evento Reativado");
        notificacao.setMensagem(
                String.format("Seu evento no buffet %s foi reativado.",
                        evento.getBuffet().getNome())
        );
        if (evento.getReserva() != null) {
            notificacao.setReserva(evento.getReserva());
        }
        notificacao.setLida(false);
        notificacao.setStatus(EnumStatus.ATIVO);

        repository.save(notificacao);
    }

    @Transactional
    @Override
    public void criarNotificacaoEventoCanceladoPeloCliente(Evento evento, User buffetOwner) {
        Notificacao notificacao = new Notificacao();
        notificacao.setUsuario(buffetOwner);
        notificacao.setTitulo("Evento Cancelado pelo Cliente");
        String clienteNome = evento.getReserva() != null ? evento.getReserva().getCliente().getNome() : "cliente";
        notificacao.setMensagem(
                String.format("O evento de %s no buffet %s foi cancelado pelo cliente.",
                        clienteNome,
                        evento.getBuffet().getNome())
        );
        if (evento.getReserva() != null) {
            notificacao.setReserva(evento.getReserva());
        }
        notificacao.setLida(false);
        notificacao.setStatus(EnumStatus.ATIVO);

        repository.save(notificacao);
    }
}
