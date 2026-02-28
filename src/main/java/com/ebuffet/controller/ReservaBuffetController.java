package com.ebuffet.controller;

import com.ebuffet.config.SingleBuffetProperties;
import com.ebuffet.controller.dto.reserva.AprovarReservaRequest;
import com.ebuffet.controller.dto.reserva.AtualizarItensReservaRequest;
import com.ebuffet.controller.dto.reserva.MotivoRequest;
import com.ebuffet.controller.dto.reserva.ReservaResponse;
import com.ebuffet.controller.dto.servico.ServicoResponse;
import com.ebuffet.controller.exceptions.ConflictException;
import com.ebuffet.controller.exceptions.NotFoundException;
import com.ebuffet.models.Reserva;
import com.ebuffet.models.Servico;
import com.ebuffet.repository.ReservaRepository;
import com.ebuffet.repository.ServicoRepository;
import com.ebuffet.service.ReservaService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/buffets/reservas")
public class ReservaBuffetController {

    private final ReservaService service;
    private final ReservaRepository reservaRepo;
    private final ServicoRepository servicoRepo;
    private final SingleBuffetProperties singleBuffetProperties;

    public ReservaBuffetController(ReservaService service, ReservaRepository reservaRepo,
                                   ServicoRepository servicoRepo, SingleBuffetProperties singleBuffetProperties) {
        this.service = service;
        this.reservaRepo = reservaRepo;
        this.servicoRepo = servicoRepo;
        this.singleBuffetProperties = singleBuffetProperties;
    }

    @GetMapping
    public Page<ReservaResponse> listarDoBuffet(@RequestParam Long ownerId,
                                                @PageableDefault(size = 20, sort = "dataCriacao", direction = Sort.Direction.DESC)
                                                Pageable pageable) {
        return service.listarPorBuffet(ownerId, pageable);
    }

    @PutMapping("/aprovar/{id}")
    public ReservaResponse aprovar(@PathVariable Long id,
                                   @RequestParam Long ownerId,
                                   @Valid @RequestBody(required = false) AprovarReservaRequest req) {
        return service.aprovarReserva(id, ownerId, req == null ? new AprovarReservaRequest() : req);
    }

    @PutMapping("/recusar/{id}")
    public ReservaResponse recusar(@PathVariable Long id,
                                   @RequestParam Long ownerId,
                                   @RequestBody(required = false) MotivoRequest req) {
        String motivo = (req == null ? null : req.getMotivo());
        return service.recusarReserva(id, ownerId, motivo);
    }

    @PutMapping("/cancelar/{id}")
    public ReservaResponse cancelar(@PathVariable Long id,
                                    @RequestParam Long ownerId,
                                    @RequestBody(required = false) MotivoRequest req) {
        String motivo = (req == null ? null : req.getMotivo());
        return service.cancelarReservaPeloBuffet(id, ownerId, motivo);
    }

    @PutMapping("/reverter-cancelamento/{id}")
    public ReservaResponse reverterCancelamento(@PathVariable Long id,
                                                @RequestParam Long ownerId) {
        return service.reverterCancelamentoReserva(id, ownerId);
    }

    @PutMapping("/{id}/itens")
    public ReservaResponse atualizarItensBuffet(@PathVariable Long id,
                                                @RequestParam Long ownerId,
                                                @Valid @RequestBody AtualizarItensReservaRequest req) {
        return service.atualizarCardapioEServicos(
                id, ownerId,
                req.getComidaIds(), req.getServicoIds(),
                true
        );
    }

    @GetMapping("/{id}/servicos")
    public List<ServicoResponse> listarServicos(@PathVariable Long id) {
        Reserva r = findReserva(id);
        return r.getServicos().stream().map(ServicoResponse::new).toList();
    }

    @PostMapping("/{id}/servicos/{servicoId}")
    @Transactional
    public ResponseEntity<Void> adicionarServico(@PathVariable Long id,
                                                 @PathVariable Long servicoId,
                                                 @RequestParam Long ownerId) {
        Long buffetId = singleBuffetProperties.getBuffetId();
        Reserva r = findReserva(id);

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
                                               @RequestParam Long ownerId) {
        Reserva r = findReserva(id);
        r.getServicos().removeIf(s -> s.getId().equals(servicoId));
        return ResponseEntity.noContent().build();
    }

    private Reserva findReserva(Long reservaId) {
        Long buffetId = singleBuffetProperties.getBuffetId();
        Reserva r = reservaRepo.findById(reservaId)
                .orElseThrow(() -> new NotFoundException("Reserva não encontrada"));
        if (!r.getBuffet().getId().equals(buffetId))
            throw new ConflictException("Reserva não pertence ao buffet informado");
        return r;
    }
}
