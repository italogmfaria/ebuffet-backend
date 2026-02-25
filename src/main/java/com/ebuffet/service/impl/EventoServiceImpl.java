package com.ebuffet.service.impl;

import com.ebuffet.controller.dto.calendario.DatasIndisponiveisResponse;
import com.ebuffet.controller.dto.evento.ClienteEventoUpdateRequest;
import com.ebuffet.controller.dto.evento.EventoResponse;
import com.ebuffet.controller.exceptions.ForbiddenException;
import com.ebuffet.controller.exceptions.NotFoundException;
import com.ebuffet.models.*;
import com.ebuffet.models.enums.EnumStatus;
import com.ebuffet.models.enums.EnumStatusEvento;
import com.ebuffet.repository.ComidaRepository;
import com.ebuffet.repository.EventoRepository;
import com.ebuffet.repository.ServicoRepository;
import com.ebuffet.service.EventoService;
import com.ebuffet.service.NotificacaoService;
import com.ebuffet.controller.exceptions.ConflictException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class EventoServiceImpl implements EventoService {

    private final EventoRepository repository;
    private final ComidaRepository comidaRepository;
    private final ServicoRepository servicoRepository;
    private final NotificacaoService notificacaoService;

    public EventoServiceImpl(EventoRepository repository, ComidaRepository comidaRepository, ServicoRepository servicoRepository, NotificacaoService notificacaoService) {
        this.repository = repository;
        this.comidaRepository = comidaRepository;
        this.servicoRepository = servicoRepository;
        this.notificacaoService = notificacaoService;
    }

    @Transactional(readOnly = true)
    @Override
    public EventoResponse getById(Long buffetId, Long id) {
        Evento e = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Evento não encontrado"));

        if (!e.getBuffet().getId().equals(buffetId)) {
            throw new ForbiddenException("Evento não pertence a este buffet");
        }

        return new EventoResponse(e);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<EventoResponse> listByBuffet(Long buffetId,
                                             EnumStatusEvento statusEvento,
                                             EnumStatus status,
                                             LocalDate dataEventoFrom,
                                             LocalDate dataEventoTo,
                                             Pageable pageable) {
        return repository.findByFilters(buffetId, statusEvento, status, dataEventoFrom, dataEventoTo, pageable)
                .map(EventoResponse::new);
    }


    @Transactional
    @Override
    public void delete(Long buffetId, Long id, Long ownerId, boolean soft) {
        Evento e = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Evento não encontrado"));

        if (!e.getBuffet().getId().equals(buffetId)) {
            throw new ConflictException("Evento não pertence ao buffet informado");
        }

        if (soft) {
            e.setStatus(EnumStatus.INATIVO);
            repository.save(e);
        } else {
            repository.delete(e);
        }
    }

    @Override
    public DatasIndisponiveisResponse getDatasIndisponiveis(Long buffetId,
                                                               LocalDate dataInicio,
                                                               LocalDate dataFim) {

        List<LocalDate> datas = repository.findDatasBloquedadasByBuffetId(
                        buffetId, EnumStatus.ATIVO, dataInicio, dataFim
                ).stream()
                .distinct()
                .toList();

        return new DatasIndisponiveisResponse(datas);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<EventoResponse> listarMeusEventos(
            Long buffetId,
            Long clienteId,
            Pageable pageable
    ) {
        return repository
                .findMeusEventos(buffetId, clienteId, EnumStatus.ATIVO, pageable)
                .map(EventoResponse::new);
    }

    @Transactional
    @Override
    public EventoResponse updateValor(Long buffetId, Long id, BigDecimal valor, Long ownerId) {
        Evento e = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Evento não encontrado"));

        if (!e.getBuffet().getId().equals(buffetId)) {
            throw new ConflictException("Evento não pertence ao buffet informado");
        }

        if (!e.getBuffet().getOwner().getId().equals(ownerId)) {
            throw new ForbiddenException("Você não é o dono deste buffet");
        }

        if (e.getStatusEvento() != EnumStatusEvento.AGENDADO) {
            throw new ConflictException("Só é possível alterar o valor de eventos agendados");
        }

        LocalDate hoje = LocalDate.now();
        LocalDate limiteEdicao = e.getDataEvento().minusDays(3);
        if (hoje.isAfter(limiteEdicao)) {
            throw new ConflictException("Só é possível alterar o valor até 3 dias antes do evento. Prazo limite: " + limiteEdicao);
        }

        BigDecimal valorAntigo = e.getValor();
        e.setValor(valor);

        Evento eventoAtualizado = repository.save(e);
        if (eventoAtualizado.getReserva() != null) {
            notificacaoService.criarNotificacaoEventoValorAtualizado(
                    eventoAtualizado,
                    eventoAtualizado.getReserva().getCliente(),
                    valorAntigo,
                    valor
            );
        }
        return new EventoResponse(eventoAtualizado);
    }

    @Transactional
    @Override
    public EventoResponse concluirEvento(Long buffetId, Long id, Long ownerId) {
        Evento e = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Evento não encontrado"));

        if (!e.getBuffet().getId().equals(buffetId)) {
            throw new ConflictException("Evento não pertence ao buffet informado");
        }

        if (!e.getBuffet().getOwner().getId().equals(ownerId)) {
            throw new ForbiddenException("Você não é o dono deste buffet");
        }

        if (e.getStatusEvento() == EnumStatusEvento.CANCELADO) {
            throw new ConflictException("Não é possível concluir um evento cancelado");
        }

        if (e.getStatusEvento() == EnumStatusEvento.CONCLUIDO) {
            throw new ConflictException("Evento já está concluído");
        }

        e.setStatusEvento(EnumStatusEvento.CONCLUIDO);

        return new EventoResponse(repository.save(e));
    }

    @Transactional
    @Override
    public EventoResponse cancelarEvento(Long buffetId, Long id, Long ownerId) {
        Evento e = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Evento não encontrado"));

        if (!e.getBuffet().getId().equals(buffetId)) {
            throw new ConflictException("Evento não pertence ao buffet informado");
        }

        if (!e.getBuffet().getOwner().getId().equals(ownerId)) {
            throw new ForbiddenException("Você não é o dono deste buffet");
        }

        if (e.getStatusEvento() == EnumStatusEvento.CANCELADO) {
            throw new ConflictException("Evento já está cancelado");
        }

        if (e.getStatusEvento() == EnumStatusEvento.CONCLUIDO) {
            throw new ConflictException("Não é possível cancelar um evento concluído");
        }

        e.setStatusEvento(EnumStatusEvento.CANCELADO);

        Evento eventoCancelado = repository.save(e);
        if (eventoCancelado.getReserva() != null) {
            notificacaoService.criarNotificacaoEventoCanceladoPeloBuffet(
                    eventoCancelado,
                    eventoCancelado.getReserva().getCliente()
            );
        }
        return new EventoResponse(eventoCancelado);
    }

    @Transactional
    @Override
    public EventoResponse reverterCancelamentoEvento(Long buffetId, Long id, Long ownerId) {
        Evento e = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Evento não encontrado"));

        if (!e.getBuffet().getId().equals(buffetId)) {
            throw new ConflictException("Evento não pertence ao buffet informado");
        }

        if (!e.getBuffet().getOwner().getId().equals(ownerId)) {
            throw new ForbiddenException("Você não é o dono deste buffet");
        }

        if (e.getStatusEvento() != EnumStatusEvento.CANCELADO) {
            throw new ConflictException("Só é possível reverter eventos cancelados");
        }

        LocalDate hoje = LocalDate.now();
        if (e.getDataEvento().isBefore(hoje)) {
            throw new ConflictException("Não é possível reverter um evento cuja data já passou");
        }

        e.setStatusEvento(EnumStatusEvento.AGENDADO);

        Evento eventoReativado = repository.save(e);
        if (eventoReativado.getReserva() != null) {
            notificacaoService.criarNotificacaoEventoReativado(
                    eventoReativado,
                    eventoReativado.getReserva().getCliente()
            );
        }
        return new EventoResponse(eventoReativado);
    }

    @Transactional
    @Override
    public EventoResponse cancelarEventoPeloCliente(Long buffetId, Long id, Long clienteId) {
        Evento e = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Evento não encontrado"));

        if (!e.getBuffet().getId().equals(buffetId)) {
            throw new ConflictException("Evento não pertence ao buffet informado");
        }

        Reserva reserva = e.getReserva();
        if (reserva == null) {
            throw new ConflictException("Evento não possui reserva associada");
        }

        if (!reserva.getCliente().getId().equals(clienteId)) {
            throw new ForbiddenException("Este evento não pertence a você");
        }

        if (e.getStatusEvento() != EnumStatusEvento.AGENDADO) {
            throw new ConflictException("Só é possível cancelar eventos agendados");
        }

        e.setStatusEvento(EnumStatusEvento.CANCELADO);

        Evento eventoCancelado = repository.save(e);
        notificacaoService.criarNotificacaoEventoCanceladoPeloCliente(
                eventoCancelado,
                eventoCancelado.getBuffet().getOwner()
        );
        return new EventoResponse(eventoCancelado);
    }

    private void validarComidasDoMesmoBuffet(List<Comida> comidas, Long buffetId) {
        if (comidas.stream().anyMatch(c -> !c.getBuffet().getId().equals(buffetId))) {
            throw new ConflictException("Todas as comidas do cardápio devem pertencer ao buffet do evento");
        }
    }

    private void validarServicosDoMesmoBuffet(List<Servico> servicos, Long buffetId) {
        if (servicos.stream().anyMatch(s -> !s.getBuffet().getId().equals(buffetId))) {
            throw new ConflictException("Todos os serviços devem pertencer ao buffet do evento");
        }
    }

    @Transactional
    @Override
    public EventoResponse atualizarEventoPeloCliente(Long buffetId, Long id, Long clienteId, ClienteEventoUpdateRequest req) {
        Evento e = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Evento não encontrado"));

        if (!e.getBuffet().getId().equals(buffetId)) {
            throw new ConflictException("Evento não pertence ao buffet informado");
        }

        Reserva reserva = e.getReserva();
        if (reserva == null) {
            throw new ConflictException("Evento não possui reserva associada");
        }

        if (!reserva.getCliente().getId().equals(clienteId)) {
            throw new ForbiddenException("Este evento não pertence a você");
        }

        if (e.getStatusEvento() != EnumStatusEvento.AGENDADO) {
            throw new ConflictException("Só é possível editar eventos agendados");
        }

        LocalDate hoje = LocalDate.now();

        LocalDate limiteEdicao = e.getDataEvento().minusDays(5);
        if (hoje.isAfter(limiteEdicao)) {
            throw new ConflictException("Só é possível editar o evento até 5 dias antes. Prazo limite: " + limiteEdicao);
        }

        if (req.getQtdPessoas() != null) {
            reserva.setQtdPessoas(req.getQtdPessoas());
        }

        if (req.getDataEvento() != null) {
            if (req.getDataEvento().isBefore(hoje)) {
                throw new ConflictException("A nova data do evento não pode estar no passado");
            }
            e.setDataEvento(req.getDataEvento());
            reserva.setDataDesejada(req.getDataEvento());
        }

        if (req.getHoraEvento() != null) {
            e.setHoraEvento(req.getHoraEvento());
            reserva.setHorarioDesejado(req.getHoraEvento());
        }

        if (req.getEndereco() != null) {
            Endereco endereco = reserva.getEndereco();
            if (endereco == null) {
                endereco = new Endereco();
                endereco.setStatus(EnumStatus.ATIVO);
                reserva.setEndereco(endereco);
            }
            endereco.setRua(req.getEndereco().getRua());
            endereco.setNumero(req.getEndereco().getNumero());
            endereco.setBairro(req.getEndereco().getBairro());
            endereco.setCidade(req.getEndereco().getCidade());
            endereco.setEstado(req.getEndereco().getEstado());
            endereco.setCep(req.getEndereco().getCep());
            endereco.setComplemento(req.getEndereco().getComplemento());
        }

        if (req.getComidaIds() != null) {
            List<Comida> comidas = comidaRepository.findByIdIn(req.getComidaIds());
            validarComidasDoMesmoBuffet(comidas, buffetId);
            reserva.setComidas(comidas);
        }

        if (req.getServicoIds() != null) {
            List<Servico> servicos = servicoRepository.findByIdIn(req.getServicoIds());
            validarServicosDoMesmoBuffet(servicos, buffetId);
            reserva.setServicos(servicos);
        }

        return new EventoResponse(repository.save(e));
    }

}
