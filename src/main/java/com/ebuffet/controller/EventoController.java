package com.ebuffet.controller;

import com.ebuffet.controller.dto.calendario.DatasIndisponiveisResponse;
import com.ebuffet.controller.dto.evento.ClienteEventoUpdateRequest;
import com.ebuffet.controller.dto.evento.EventoResponse;
import com.ebuffet.models.enums.EnumStatus;
import com.ebuffet.models.enums.EnumStatusEvento;
import com.ebuffet.service.EventoService;
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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Tag(name = "Eventos", description = "API para gerenciamento de eventos do buffet")
@RestController
@RequestMapping("/api/eventos")
public class EventoController {

    private final EventoService service;

    public EventoController(EventoService service) {
        this.service = service;
    }

    @Operation(summary = "Buscar evento por ID")
    @GetMapping("/{id}")
    public EventoResponse get(@PathVariable Long id) {
        return service.getById(id);
    }

    @Operation(summary = "Listar eventos do buffet")
    @GetMapping
    public Page<EventoResponse> list(
            @RequestParam(required = false) EnumStatusEvento statusEvento,
            @RequestParam(required = false) EnumStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataEventoFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataEventoTo,
            @PageableDefault(size = 20, sort = "dataCriacao", direction = Sort.Direction.DESC)
            Pageable pageable) {
        return service.listByBuffet(statusEvento, status, dataEventoFrom, dataEventoTo, pageable);
    }

    @Operation(summary = "Excluir evento")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @RequestParam Long ownerId,
            @RequestParam(defaultValue = "true") boolean soft) {
        service.delete(id, ownerId, soft);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Obter datas indisponíveis")
    @GetMapping("/datas-indisponiveis")
    public ResponseEntity<DatasIndisponiveisResponse> getDatasIndisponiveis(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        DatasIndisponiveisResponse datas = service.getDatasIndisponiveis(dataInicio, dataFim);
        return ResponseEntity.ok(datas);
    }

    @Operation(summary = "Listar meus eventos")
    @GetMapping("/me")
    public Page<EventoResponse> listarMeusEventos(
            @RequestParam Long clienteId,
            @PageableDefault(size = 20, sort = "dataCriacao", direction = Sort.Direction.DESC)
            Pageable pageable) {
        return service.listarMeusEventos(clienteId, pageable);
    }

    @Operation(summary = "Atualizar valor do evento (Buffet)")
    @PutMapping("/{id}/valor")
    public EventoResponse updateValor(
            @PathVariable Long id,
            @Valid @RequestBody com.ebuffet.controller.dto.evento.UpdateValorRequest req,
            @RequestParam Long ownerId) {
        return service.updateValor(id, req.getValor(), ownerId);
    }

    @Operation(summary = "Concluir evento")
    @PutMapping("/{id}/concluir")
    public EventoResponse concluir(
            @PathVariable Long id,
            @RequestParam Long ownerId) {
        return service.concluirEvento(id, ownerId);
    }

    @Operation(summary = "Cancelar evento (Buffet)")
    @PutMapping("/{id}/cancelar")
    public EventoResponse cancelar(
            @PathVariable Long id,
            @RequestParam Long ownerId) {
        return service.cancelarEvento(id, ownerId);
    }

    @Operation(summary = "Reverter cancelamento do evento (Buffet)")
    @PutMapping("/{id}/reverter-cancelamento")
    public EventoResponse reverterCancelamento(
            @PathVariable Long id,
            @RequestParam Long ownerId) {
        return service.reverterCancelamentoEvento(id, ownerId);
    }

    @Operation(summary = "Cancelar evento (Cliente)")
    @PutMapping("/{id}/cliente/cancelar")
    public EventoResponse cancelarPeloCliente(
            @PathVariable Long id,
            @RequestParam Long clienteId) {
        return service.cancelarEventoPeloCliente(id, clienteId);
    }

    @Operation(summary = "Atualizar evento (Cliente)")
    @PutMapping("/{id}/cliente")
    public EventoResponse atualizarPeloCliente(
            @PathVariable Long id,
            @RequestParam Long clienteId,
            @Valid @RequestBody ClienteEventoUpdateRequest req) {
        return service.atualizarEventoPeloCliente(id, clienteId, req);
    }
}
