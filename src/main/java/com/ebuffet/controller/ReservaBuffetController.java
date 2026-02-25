package com.ebuffet.controller;

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

import static com.ebuffet.utils.Constants.BUFFET_ID_HEADER;

@RestController
@RequestMapping("/api/buffets/reservas")
public class ReservaBuffetController {

    private final ReservaService service;
    private final ReservaRepository reservaRepo;
    private final ServicoRepository servicoRepo;

    public ReservaBuffetController(ReservaService service, ReservaRepository reservaRepo, ServicoRepository servicoRepo) {
        this.service = service;
        this.reservaRepo = reservaRepo;
        this.servicoRepo = servicoRepo;
    }

    @GetMapping
    public Page<ReservaResponse> listarDoBuffet(@RequestHeader(value = BUFFET_ID_HEADER) Long buffetId,
                                                @RequestParam Long ownerId,
                                                @PageableDefault(size = 20, sort = "dataCriacao", direction = Sort.Direction.DESC)
                                                Pageable pageable) {
        return service.listarPorBuffet(buffetId, ownerId, pageable);
    }

    @PutMapping("/aprovar/{id}")
    public ReservaResponse aprovar(@RequestHeader(value = BUFFET_ID_HEADER) Long buffetId,
                                   @PathVariable Long id,
                                   @RequestParam Long ownerId,
                                   @Valid @RequestBody(required = false) AprovarReservaRequest req) {
        return service.aprovarReserva(buffetId, id, ownerId, req == null ? new AprovarReservaRequest() : req);
    }

    @PutMapping("/recusar/{id}")
    public ReservaResponse recusar(@RequestHeader(value = BUFFET_ID_HEADER) Long buffetId,
                                   @PathVariable Long id,
                                   @RequestParam Long ownerId,
                                   @RequestBody(required = false) MotivoRequest req) {
        String motivo = (req == null ? null : req.getMotivo());
        return service.recusarReserva(buffetId, id, ownerId, motivo);
    }

    @PutMapping("/cancelar/{id}")
    public ReservaResponse cancelar(@RequestHeader(value = BUFFET_ID_HEADER) Long buffetId,
                                    @PathVariable Long id,
                                    @RequestParam Long ownerId,
                                    @RequestBody(required = false) MotivoRequest req) {
        String motivo = (req == null ? null : req.getMotivo());
        return service.cancelarReservaPeloBuffet(buffetId, id, ownerId, motivo);
    }

    @PutMapping("/reverter-cancelamento/{id}")
    public ReservaResponse reverterCancelamento(@RequestHeader(value = BUFFET_ID_HEADER) Long buffetId,
                                                @PathVariable Long id,
                                                @RequestParam Long ownerId) {
        return service.reverterCancelamentoReserva(buffetId, id, ownerId);
    }

    @PutMapping("/{id}/itens")
    public ReservaResponse atualizarItensBuffet(@RequestHeader(value = BUFFET_ID_HEADER) Long buffetId,
                                                @PathVariable Long id,
                                                @RequestParam Long ownerId,
                                                @Valid @RequestBody AtualizarItensReservaRequest req) {
        return service.atualizarCardapioEServicos(
                buffetId, id, ownerId,
                req.getComidaIds(), req.getServicoIds(),
                true
        );
    }

    @GetMapping("/{id}/servicos")
    public List<ServicoResponse> listarServicos(@RequestHeader(value = BUFFET_ID_HEADER) Long buffetId,
                                                 @PathVariable Long id) {
        Reserva r = findReserva(buffetId, id);
        return r.getServicos().stream().map(ServicoResponse::new).toList();
    }

    @PostMapping("/{id}/servicos/{servicoId}")
    @Transactional
    public ResponseEntity<Void> adicionarServico(@RequestHeader(value = BUFFET_ID_HEADER) Long buffetId,
                                                 @PathVariable Long id,
                                                 @PathVariable Long servicoId,
                                                 @RequestParam Long ownerId) {
        Reserva r = findReserva(buffetId, id);

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
                                               @RequestParam Long ownerId) {
        Reserva r = findReserva(buffetId, id);
        r.getServicos().removeIf(s -> s.getId().equals(servicoId));
        return ResponseEntity.noContent().build();
    }

    private Reserva findReserva(Long buffetId, Long reservaId) {
        Reserva r = reservaRepo.findById(reservaId)
                .orElseThrow(() -> new NotFoundException("Reserva não encontrada"));
        if (!r.getBuffet().getId().equals(buffetId))
            throw new ConflictException("Reserva não pertence ao buffet informado");
        return r;
    }
}
