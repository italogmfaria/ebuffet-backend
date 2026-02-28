package com.ebuffet.controller;

import com.ebuffet.config.SingleBuffetProperties;
import com.ebuffet.controller.dto.reserva.AtualizarItensReservaRequest;
import com.ebuffet.controller.dto.reserva.MotivoRequest;
import com.ebuffet.controller.dto.reserva.ReservaRequest;
import com.ebuffet.controller.dto.reserva.ReservaResponse;
import com.ebuffet.controller.dto.reserva.ReservaUpdateRequest;
import com.ebuffet.controller.dto.servico.ServicoResponse;
import com.ebuffet.controller.exceptions.ConflictException;
import com.ebuffet.controller.exceptions.ForbiddenException;
import com.ebuffet.controller.exceptions.NotFoundException;
import com.ebuffet.models.Reserva;
import com.ebuffet.models.Servico;
import com.ebuffet.repository.ReservaRepository;
import com.ebuffet.repository.ServicoRepository;
import com.ebuffet.service.ReservaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Reservas (Cliente)", description = "API para gerenciamento de reservas pelos clientes")
@RestController
@RequestMapping("/api/clientes/reservas")
public class ReservaClienteController {

    private final ReservaService service;
    private final ReservaRepository reservaRepo;
    private final ServicoRepository servicoRepo;
    private final SingleBuffetProperties singleBuffetProperties;

    public ReservaClienteController(ReservaService service, ReservaRepository reservaRepo,
                                    ServicoRepository servicoRepo, SingleBuffetProperties singleBuffetProperties) {
        this.service = service;
        this.reservaRepo = reservaRepo;
        this.servicoRepo = servicoRepo;
        this.singleBuffetProperties = singleBuffetProperties;
    }

    @PostMapping
    public ResponseEntity<ReservaResponse> criar(
            @RequestParam Long clienteId,
            @Valid @RequestBody ReservaRequest req) {
        ReservaResponse created = service.criarReserva(clienteId, req);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ReservaResponse get(@PathVariable Long id,
                               @RequestParam Long clienteId) {
        return service.getById(id, clienteId);
    }

    @GetMapping("/me")
    public Page<ReservaResponse> listarMinhas(
            @RequestParam Long clienteId,
            @PageableDefault(size = 20, sort = "dataCriacao", direction = Sort.Direction.DESC)
            Pageable pageable) {
        return service.listarPorCliente(clienteId, pageable);
    }

    @PutMapping("/{id}")
    public ReservaResponse atualizar(
            @PathVariable Long id,
            @RequestParam Long clienteId,
            @Valid @RequestBody ReservaUpdateRequest req) {
        return service.atualizarReserva(id, clienteId, req);
    }

    @PutMapping("/{id}/itens")
    public ReservaResponse atualizarItens(
            @PathVariable Long id,
            @RequestParam Long clienteId,
            @Valid @RequestBody AtualizarItensReservaRequest req) {
        return service.atualizarCardapioEServicos(
                id, clienteId,
                req.getComidaIds(), req.getServicoIds(),
                false
        );
    }

    @PutMapping("/{id}/cancelar")
    public ReservaResponse cancelar(@PathVariable Long id,
                                    @RequestParam Long clienteId,
                                    @RequestBody(required = false) MotivoRequest req) {
        String motivo = (req == null ? null : req.getMotivo());
        return service.cancelarReservaPeloCliente(id, clienteId, motivo);
    }

    @GetMapping("/{id}/servicos")
    public List<ServicoResponse> listarServicos(@PathVariable Long id,
                                                 @RequestParam Long clienteId) {
        Reserva r = findReservaForCliente(id, clienteId);
        return r.getServicos().stream().map(ServicoResponse::new).toList();
    }

    @PostMapping("/{id}/servicos/{servicoId}")
    @Transactional
    public ResponseEntity<Void> adicionarServico(@PathVariable Long id,
                                                 @PathVariable Long servicoId,
                                                 @RequestParam Long clienteId) {
        Long buffetId = singleBuffetProperties.getBuffetId();
        Reserva r = findReservaForCliente(id, clienteId);

        Servico s = servicoRepo.findById(servicoId)
                .orElseThrow(() -> new NotFoundException("Serviço não encontrado"));
        if (!s.getBuffet().getId().equals(buffetId))
            throw new ConflictException("Serviço não pertence a este buffet");

        if (r.getServicos().stream().noneMatch(x -> x.getId().equals(servicoId))) {
            r.getServicos().add(s);
        }
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/servicos/{servicoId}")
    @Transactional
    public ResponseEntity<Void> removerServico(@PathVariable Long id,
                                               @PathVariable Long servicoId,
                                               @RequestParam Long clienteId) {
        Reserva r = findReservaForCliente(id, clienteId);
        r.getServicos().removeIf(s -> s.getId().equals(servicoId));
        return ResponseEntity.noContent().build();
    }

    private Reserva findReservaForCliente(Long reservaId, Long clienteId) {
        Long buffetId = singleBuffetProperties.getBuffetId();
        Reserva r = reservaRepo.findById(reservaId)
                .orElseThrow(() -> new NotFoundException("Reserva não encontrada"));
        if (!r.getBuffet().getId().equals(buffetId))
            throw new ConflictException("Reserva não pertence ao buffet informado");
        if (!r.getCliente().getId().equals(clienteId))
            throw new ForbiddenException("Reserva não pertence a este cliente");
        return r;
    }
}
