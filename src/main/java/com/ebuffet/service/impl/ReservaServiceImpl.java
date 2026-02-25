package com.ebuffet.service.impl;

import com.ebuffet.controller.dto.endereco.EnderecoRequest;
import com.ebuffet.controller.dto.reserva.AprovarReservaRequest;
import com.ebuffet.controller.dto.reserva.ReservaRequest;
import com.ebuffet.controller.dto.reserva.ReservaResponse;
import com.ebuffet.controller.dto.reserva.ReservaUpdateRequest;
import com.ebuffet.controller.exceptions.ConflictException;
import com.ebuffet.controller.exceptions.ForbiddenException;
import com.ebuffet.controller.exceptions.NotFoundException;
import com.ebuffet.models.*;
import com.ebuffet.models.enums.EnumStatus;
import com.ebuffet.models.enums.EnumStatusEvento;
import com.ebuffet.models.enums.EnumStatusReserva;
import com.ebuffet.repository.*;
import com.ebuffet.service.NotificacaoService;
import com.ebuffet.service.ReservaService;
import jakarta.annotation.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReservaServiceImpl implements ReservaService {

    private final ReservaRepository reservaRepo;
    private final BuffetRepository buffetRepo;
    private final UserRepository userRepo;
    private final ServicoRepository servicoRepo;
    private final ComidaRepository comidaRepo;
    private final EventoRepository eventoRepo;
    private final NotificacaoService notificacaoService;

    public ReservaServiceImpl(ReservaRepository reservaRepo, BuffetRepository buffetRepo, UserRepository userRepo, ServicoRepository servicoRepo, ComidaRepository comidaRepo, EventoRepository eventoRepo, NotificacaoService notificacaoService) {
        this.reservaRepo = reservaRepo;
        this.buffetRepo = buffetRepo;
        this.userRepo = userRepo;
        this.servicoRepo = servicoRepo;
        this.comidaRepo = comidaRepo;
        this.eventoRepo = eventoRepo;
        this.notificacaoService = notificacaoService;
    }

    @Transactional
    @Override
    public ReservaResponse criarReserva(Long buffetId,Long clienteId, ReservaRequest req) {
        User cliente = userRepo.findById(clienteId)
                .orElseThrow(() -> new NotFoundException("Cliente não encontrado"));

        Buffet buffet = buffetRepo.findById(req.getBuffetId())
                .orElseThrow(() -> new NotFoundException("Buffet não encontrado"));

        boolean dataBloqueada = eventoRepo.existsDataBloqueada(
                buffet.getId(), req.getDataDesejada(), EnumStatus.ATIVO);
        if (dataBloqueada) {
            throw new ConflictException("Não é possível criar uma reserva para este dia. A data " + req.getDataDesejada() + " está bloqueada no calendário.");
        }

        Reserva r = new Reserva();
        r.setCliente(cliente);
        r.setBuffet(buffet);
        r.setQtdPessoas(req.getQtdPessoas());
        r.setDataDesejada(req.getDataDesejada());
        r.setHorarioDesejado(req.getHorarioDesejado());
        r.setStatusReserva(EnumStatusReserva.PENDENTE);
        r.setStatus(EnumStatus.ATIVO);
        r.setTitulo(req.getTitulo());
        r.setDescricao(req.getDescricao());

        Endereco e = new Endereco();
        EnderecoRequest er = req.getEndereco();
        e.setRua(er.getRua());
        e.setNumero(er.getNumero());
        e.setBairro(er.getBairro());
        e.setCidade(er.getCidade());
        e.setEstado(er.getEstado());
        e.setCep(er.getCep());
        e.setComplemento(er.getComplemento());
        e.setStatus(EnumStatus.ATIVO);
        r.setEndereco(e);

        if (req.getServicoIds() != null && !req.getServicoIds().isEmpty()) {
            List<Servico> servicos = servicoRepo.findByIdIn(req.getServicoIds());
            validarServicosDoMesmoBuffet(servicos, buffet.getId());
            r.setServicos(servicos);
        }

        if (req.getComidaIds() != null && !req.getComidaIds().isEmpty()) {
            List<Comida> comidas = comidaRepo.findByIdIn(req.getComidaIds());
            validarComidasDoMesmoBuffet(comidas, buffet.getId());
            r.setComidas(comidas);
        }

        Reserva reservaSalva = reservaRepo.save(r);

        notificacaoService.criarNotificacaoNovaReserva(reservaSalva, buffet.getOwner());

        return ReservaResponse.of(reservaSalva);
    }

    @Transactional
    @Override
    public ReservaResponse atualizarReserva(Long buffetId, Long reservaId, Long clienteId, ReservaUpdateRequest req) {
        Reserva r = reservaRepo.findById(reservaId)
                .orElseThrow(() -> new NotFoundException("Reserva não encontrada"));

        if (!r.getCliente().getId().equals(clienteId)) {
            throw new ForbiddenException("Reserva não pertence a este cliente");
        }

        if (!r.getBuffet().getId().equals(buffetId)) {
            throw new ConflictException("Reserva não pertence ao buffet informado");
        }

        if (r.getStatusReserva() != EnumStatusReserva.PENDENTE) {
            throw new ConflictException("Somente reservas PENDENTES podem ser editadas. Status atual: " + r.getStatusReserva());
        }

        LocalDate hoje = LocalDate.now();
        if (req.getDataDesejada().isBefore(hoje)) {
            throw new ConflictException("A data desejada não pode estar no passado");
        }

        r.setQtdPessoas(req.getQtdPessoas());
        r.setDataDesejada(req.getDataDesejada());
        r.setHorarioDesejado(req.getHorarioDesejado());
        r.setTitulo(req.getTitulo());
        r.setDescricao(req.getDescricao());

        Endereco endereco = r.getEndereco();
        if (endereco == null) {
            endereco = new Endereco();
            endereco.setStatus(EnumStatus.ATIVO);
            r.setEndereco(endereco);
        }
        EnderecoRequest er = req.getEndereco();
        endereco.setRua(er.getRua());
        endereco.setNumero(er.getNumero());
        endereco.setBairro(er.getBairro());
        endereco.setCidade(er.getCidade());
        endereco.setEstado(er.getEstado());
        endereco.setCep(er.getCep());
        endereco.setComplemento(er.getComplemento());

        Reserva reservaAtualizada = reservaRepo.save(r);

        return ReservaResponse.of(reservaAtualizada);
    }

    @Transactional
    @Override
    public ReservaResponse aprovarReserva(Long buffetId, Long reservaId, Long ownerId, AprovarReservaRequest req) {
        Reserva r = reservaRepo.findById(reservaId)
                .orElseThrow(() -> new NotFoundException("Reserva não encontrada"));
        if (!r.getBuffet().getOwner().getId().equals(ownerId))
            throw new ForbiddenException("Você não é o dono deste buffet");

        if (r.getStatusReserva() != EnumStatusReserva.PENDENTE)
            throw new ConflictException("Somente reservas PENDENTES podem ser aprovadas");

        Evento ev = new Evento();
        ev.setReserva(r);
        ev.setBuffet(r.getBuffet());

        ev.setStatusEvento(EnumStatusEvento.AGENDADO);

        boolean shouldBlockDay = req.getBlockDay() != null && req.getBlockDay();
        ev.setBloquearCalendario(shouldBlockDay);

        ev.setValor(req.getValor());
        ev.setStatus(EnumStatus.ATIVO);

        ev.setNome(r.getTitulo());
        ev.setDescricao(r.getDescricao());

        ev.setDataEvento(r.getDataDesejada());
        ev.setHoraEvento(r.getHorarioDesejado());

        r.setEvento(ev);
        r.setStatusReserva(EnumStatusReserva.APROVADA);

        notificacaoService.criarNotificacaoReservaAprovada(r, r.getCliente());

        return ReservaResponse.of(r);
    }

    @Transactional
    @Override
    public ReservaResponse recusarReserva(Long buffetId, Long reservaId, Long ownerId, @Nullable String motivo) {
        Reserva r = reservaRepo.findById(reservaId)
                .orElseThrow(() -> new NotFoundException("Reserva não encontrada"));
        if (!r.getBuffet().getOwner().getId().equals(ownerId))
            throw new ForbiddenException("Você não é o dono deste buffet");
        if (r.getStatusReserva() != EnumStatusReserva.PENDENTE)
            throw new ConflictException("Somente reservas PENDENTES podem ser recusadas");

        r.setStatusReserva(EnumStatusReserva.CANCELADA);
        r.setStatus(EnumStatus.INATIVO);
        if (motivo != null && !motivo.isBlank()) {
            String desc = (r.getDescricao() == null ? "" : r.getDescricao() + " | ");
            r.setDescricao(desc + "Recusada pelo buffet: " + motivo);
        }
        notificacaoService.criarNotificacaoReservaRecusada(r, r.getCliente(), motivo);
        return ReservaResponse.of(r);
    }

    @Transactional
    @Override
    public ReservaResponse cancelarReservaPeloBuffet(Long buffetId, Long reservaId, Long ownerId, @Nullable String motivo) {
        Reserva r = reservaRepo.findById(reservaId)
                .orElseThrow(() -> new NotFoundException("Reserva não encontrada"));

        if (!r.getBuffet().getOwner().getId().equals(ownerId)) {
            throw new ForbiddenException("Você não é o dono deste buffet");
        }

        if (!r.getBuffet().getId().equals(buffetId)) {
            throw new ConflictException("Reserva não pertence ao buffet informado");
        }

        if (r.getStatusReserva() == EnumStatusReserva.CANCELADA) {
            return ReservaResponse.of(r);
        }

        if (r.getStatusReserva() != EnumStatusReserva.PENDENTE && r.getStatusReserva() != EnumStatusReserva.APROVADA) {
            throw new ConflictException("Só é possível cancelar reservas pendentes ou aprovadas");
        }

        if (r.getEvento() != null) {
            r.getEvento().setStatusEvento(EnumStatusEvento.CANCELADO);
        }

        r.setStatusReserva(EnumStatusReserva.CANCELADA);
        if (motivo != null && !motivo.isBlank()) {
            String desc = (r.getDescricao() == null ? "" : r.getDescricao() + " | ");
            r.setDescricao(desc + "Cancelada pelo buffet: " + motivo);
        }

        notificacaoService.criarNotificacaoReservaCanceladaPeloBuffet(r, r.getCliente(), motivo);
        return ReservaResponse.of(reservaRepo.save(r));
    }

    @Transactional
    @Override
    public ReservaResponse reverterCancelamentoReserva(Long buffetId, Long reservaId, Long ownerId) {
        Reserva r = reservaRepo.findById(reservaId)
                .orElseThrow(() -> new NotFoundException("Reserva não encontrada"));

        if (!r.getBuffet().getOwner().getId().equals(ownerId)) {
            throw new ForbiddenException("Você não é o dono deste buffet");
        }

        if (!r.getBuffet().getId().equals(buffetId)) {
            throw new ConflictException("Reserva não pertence ao buffet informado");
        }

        if (r.getStatusReserva() != EnumStatusReserva.CANCELADA) {
            throw new ConflictException("Só é possível reverter reservas canceladas");
        }

        LocalDate hoje = LocalDate.now();
        if (r.getDataDesejada().isBefore(hoje)) {
            throw new ConflictException("Não é possível reverter uma reserva cuja data já passou");
        }

        if (r.getEvento() != null) {
            r.getEvento().setStatusEvento(EnumStatusEvento.AGENDADO);
            r.setStatusReserva(EnumStatusReserva.APROVADA);
        } else {
            r.setStatusReserva(EnumStatusReserva.PENDENTE);
        }

        r.setStatus(EnumStatus.ATIVO);

        notificacaoService.criarNotificacaoReservaReativada(r, r.getCliente());
        return ReservaResponse.of(reservaRepo.save(r));
    }

    @Transactional
    @Override
    public ReservaResponse cancelarReservaPeloCliente(Long buffetId, Long reservaId, Long clienteId, @Nullable String motivo) {
        Reserva r = reservaRepo.findById(reservaId)
                .orElseThrow(() -> new NotFoundException("Reserva não encontrada"));
        if (!r.getCliente().getId().equals(clienteId))
            throw new ForbiddenException("Reserva não pertence a este cliente");

        if (r.getStatusReserva() == EnumStatusReserva.CANCELADA)
            return ReservaResponse.of(r);

        if (r.getStatusReserva() != EnumStatusReserva.PENDENTE && r.getStatusReserva() != EnumStatusReserva.APROVADA) {
            throw new ConflictException("Só é possível cancelar reservas pendentes ou aprovadas");
        }

        if (r.getEvento() != null) {
            r.getEvento().setStatusEvento(EnumStatusEvento.CANCELADO);
        }

        r.setStatusReserva(EnumStatusReserva.CANCELADA);
        r.setStatus(EnumStatus.INATIVO);
        if (motivo != null && !motivo.isBlank()) {
            String desc = (r.getDescricao() == null ? "" : r.getDescricao() + " | ");
            r.setDescricao(desc + "Cancelada pelo cliente: " + motivo);
        }
        return ReservaResponse.of(r);
    }

    @Transactional
    @Override
    public ReservaResponse atualizarCardapioEServicos(Long buffetId, Long reservaId, Long solicitanteId,
                                                      List<Long> comidaIds, List<Long> servicoIds,
                                                      boolean solicitanteEhBuffetOwner) {
        Reserva r = reservaRepo.findById(reservaId)
                .orElseThrow(() -> new NotFoundException("Reserva não encontrada"));

        if (solicitanteEhBuffetOwner) {
            if (!r.getBuffet().getOwner().getId().equals(solicitanteId))
                throw new ForbiddenException("Você não é o dono deste buffet");
        } else {
            if (!r.getCliente().getId().equals(solicitanteId))
                throw new ForbiddenException("Reserva não pertence a este cliente");
            if (r.getStatusReserva() != EnumStatusReserva.PENDENTE)
                throw new ConflictException("Cliente só pode alterar reserva PENDENTE");
        }

        if (servicoIds != null) {
            List<Servico> servicos = servicoRepo.findByIdIn(servicoIds);
            validarServicosDoMesmoBuffet(servicos, r.getBuffet().getId());
            r.setServicos(servicos);
        }

        if (comidaIds != null) {
            List<Comida> comidas = comidaRepo.findByIdIn(comidaIds);
            validarComidasDoMesmoBuffet(comidas, r.getBuffet().getId());
            r.setComidas(comidas);
        }

        return ReservaResponse.of(r);
    }

    @Transactional(readOnly = true)
    @Override
    public ReservaResponse getById(Long buffetId, Long reservaId, Long clienteId) {
        Reserva r = reservaRepo.findByIdAndClienteIdAndBuffetId(reservaId, clienteId, buffetId)
                .orElseThrow(() -> new NotFoundException("Reserva não encontrada"));
        return ReservaResponse.of(r);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<ReservaResponse> listarPorCliente(Long buffetId, Long clienteId, Pageable pageable) {
        return reservaRepo
                .findByClienteIdAndBuffetId(clienteId, buffetId, pageable)
                .map(ReservaResponse::of);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<ReservaResponse> listarPorBuffet(Long buffetId, Long ownerId, Pageable pageable) {
        Buffet b = buffetRepo.findById(buffetId).orElseThrow(() -> new NotFoundException("Buffet não encontrado"));
        if (!b.getOwner().getId().equals(ownerId)) throw new ForbiddenException("Você não é o dono deste buffet");
        return reservaRepo.findByBuffetId(buffetId, pageable).map(ReservaResponse::of);
    }


    private void validarServicosDoMesmoBuffet(List<Servico> servicos, Long buffetId) {
        if (servicos.stream().anyMatch(s -> !s.getBuffet().getId().equals(buffetId))) {
            throw new ConflictException("Todos os serviços devem pertencer ao buffet da reserva");
        }
    }

    private void validarComidasDoMesmoBuffet(List<Comida> comidas, Long buffetId) {
        if (comidas.stream().anyMatch(c -> !c.getBuffet().getId().equals(buffetId))) {
            throw new ConflictException("Todas as comidas do cardápio devem pertencer ao buffet da reserva");
        }
    }
}
