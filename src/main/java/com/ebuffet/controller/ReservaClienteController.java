package com.ebuffet.controller;

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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

import static com.ebuffet.utils.Constants.BUFFET_ID_HEADER;

@Tag(name = "Reservas (Cliente)", description = "API para gerenciamento de reservas pelos clientes")
@RestController
@RequestMapping("/api/clientes/reservas")
public class ReservaClienteController {

    private final ReservaService service;
    private final ReservaRepository reservaRepo;
    private final ServicoRepository servicoRepo;

    public ReservaClienteController(ReservaService service, ReservaRepository reservaRepo, ServicoRepository servicoRepo) {
        this.service = service;
        this.reservaRepo = reservaRepo;
        this.servicoRepo = servicoRepo;
    }

    @PostMapping
    public ResponseEntity<ReservaResponse> criar(
            @RequestHeader(value = BUFFET_ID_HEADER) Long buffetId,
            @RequestParam Long clienteId,
            @Valid @RequestBody ReservaRequest req) {
        ReservaResponse created = service.criarReserva(buffetId, clienteId, req);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ReservaResponse get(@RequestHeader(value = BUFFET_ID_HEADER) Long buffetId,
                               @PathVariable Long id,
                               @RequestParam Long clienteId) {
        return service.getById(buffetId, id, clienteId);
    }

    @GetMapping("/me")
    public Page<ReservaResponse> listarMinhas(
            @RequestHeader(value = BUFFET_ID_HEADER) Long buffetId,
            @RequestParam Long clienteId,
            @PageableDefault(size = 20, sort = "dataCriacao", direction = Sort.Direction.DESC)
            Pageable pageable) {
        return service.listarPorCliente(buffetId, clienteId, pageable);
    }

    @Operation(
        summary = "Atualizar reserva",
        description = "Permite ao cliente editar uma reserva PENDENTE. Não é possível editar reservas aprovadas ou canceladas. A data desejada não pode estar no passado."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reserva atualizada com sucesso",
            content = @Content(schema = @Schema(implementation = ReservaResponse.class))),
        @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos", content = @Content),
        @ApiResponse(responseCode = "403", description = "Reserva não pertence a este cliente", content = @Content),
        @ApiResponse(responseCode = "404", description = "Reserva não encontrada", content = @Content),
        @ApiResponse(responseCode = "409", description = "Reserva não está em status PENDENTE ou data está no passado", content = @Content)
    })
    @PutMapping("/{id}")
    public ReservaResponse atualizar(
            @Parameter(description = "ID do buffet", required = true)
            @RequestHeader(value = BUFFET_ID_HEADER) Long buffetId,
            @Parameter(description = "ID da reserva", required = true)
            @PathVariable Long id,
            @Parameter(description = "ID do cliente", required = true)
            @RequestParam Long clienteId,
            @Parameter(description = "Novos dados da reserva", required = true)
            @Valid @RequestBody ReservaUpdateRequest req) {
        return service.atualizarReserva(buffetId, id, clienteId, req);
    }

    @PutMapping("/{id}/itens")
    public ReservaResponse atualizarItens(
            @RequestHeader(value = BUFFET_ID_HEADER) Long buffetId,
            @PathVariable Long id,
            @RequestParam Long clienteId,
            @Valid @RequestBody AtualizarItensReservaRequest req) {
        return service.atualizarCardapioEServicos(
                buffetId, id, clienteId,
                req.getComidaIds(), req.getServicoIds(),
                false
        );
    }

    @PutMapping("/{id}/cancelar")
    public ReservaResponse cancelar(@RequestHeader(value = BUFFET_ID_HEADER) Long buffetId,
                                    @PathVariable Long id,
                                    @RequestParam Long clienteId,
                                    @RequestBody(required = false) MotivoRequest req) {
        String motivo = (req == null ? null : req.getMotivo());
        return service.cancelarReservaPeloCliente(buffetId, id, clienteId, motivo);
    }

    @GetMapping("/{id}/servicos")
    public List<ServicoResponse> listarServicos(@RequestHeader(value = BUFFET_ID_HEADER) Long buffetId,
                                                 @PathVariable Long id,
                                                 @RequestParam Long clienteId) {
        Reserva r = findReservaForCliente(buffetId, id, clienteId);
        return r.getServicos().stream().map(ServicoResponse::new).toList();
    }

    @PostMapping("/{id}/servicos/{servicoId}")
    @Transactional
    public ResponseEntity<Void> adicionarServico(@RequestHeader(value = BUFFET_ID_HEADER) Long buffetId,
                                                 @PathVariable Long id,
                                                 @PathVariable Long servicoId,
                                                 @RequestParam Long clienteId) {
        Reserva r = findReservaForCliente(buffetId, id, clienteId);

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
    public ResponseEntity<Void> removerServico(@RequestHeader(value = BUFFET_ID_HEADER) Long buffetId,
                                               @PathVariable Long id,
                                               @PathVariable Long servicoId,
                                               @RequestParam Long clienteId) {
        Reserva r = findReservaForCliente(buffetId, id, clienteId);
        r.getServicos().removeIf(s -> s.getId().equals(servicoId));
        return ResponseEntity.noContent().build();
    }

    private Reserva findReservaForCliente(Long buffetId, Long reservaId, Long clienteId) {
        Reserva r = reservaRepo.findById(reservaId)
                .orElseThrow(() -> new NotFoundException("Reserva não encontrada"));
        if (!r.getBuffet().getId().equals(buffetId))
            throw new ConflictException("Reserva não pertence ao buffet informado");
        if (!r.getCliente().getId().equals(clienteId))
            throw new ForbiddenException("Reserva não pertence a este cliente");
        return r;
    }
}
