package com.ebuffet.controller;

import com.ebuffet.controller.dto.calendario.DatasIndisponiveisResponse;
import com.ebuffet.controller.dto.evento.ClienteEventoUpdateRequest;
import com.ebuffet.controller.dto.evento.EventoResponse;
import com.ebuffet.models.enums.EnumStatus;
import com.ebuffet.models.enums.EnumStatusEvento;
import com.ebuffet.service.EventoService;
import com.ebuffet.utils.Constants;
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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.ebuffet.utils.Constants.BUFFET_ID_HEADER;

@Tag(name = "Eventos", description = "API para gerenciamento de eventos dos buffets")
@RestController
@RequestMapping("/api/eventos")
public class EventoController {

    private final EventoService service;

    public EventoController(EventoService service) {
        this.service = service;
    }

    @Operation(
        summary = "Buscar evento por ID",
        description = "Retorna os detalhes completos de um evento específico."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Evento encontrado com sucesso",
            content = @Content(schema = @Schema(implementation = EventoResponse.class))),
        @ApiResponse(responseCode = "404", description = "Evento não encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    public EventoResponse get(
            @Parameter(description = "ID do buffet", required = true)
            @RequestHeader(value = BUFFET_ID_HEADER) Long buffetId,
            @Parameter(description = "ID do evento", required = true)
            @PathVariable Long id) {
        return service.getById(buffetId, id);
    }

    @Operation(
        summary = "Listar eventos do buffet",
        description = "Retorna uma lista paginada de eventos. Permite filtrar por status do evento, status geral e período de início."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de eventos retornada com sucesso",
            content = @Content(schema = @Schema(implementation = Page.class)))
    })
    @GetMapping
    public Page<EventoResponse> list(
            @Parameter(description = "ID do buffet", required = true)
            @RequestHeader(value = BUFFET_ID_HEADER) Long buffetId,
            @Parameter(description = "Filtrar por status do evento (PENDENTE, CONFIRMADO, CONCLUIDO, CANCELADO)")
            @RequestParam(required = false) EnumStatusEvento statusEvento,
            @Parameter(description = "Filtrar por status geral (ATIVO, INATIVO)")
            @RequestParam(required = false) EnumStatus status,
            @Parameter(description = "Data do evento mínima (formato ISO: yyyy-MM-dd)")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataEventoFrom,
            @Parameter(description = "Data do evento máxima (formato ISO: yyyy-MM-dd)")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataEventoTo,
            @Parameter(description = "Configuração de paginação e ordenação", hidden = true)
            @PageableDefault(size = 20, sort = "dataCriacao", direction = Sort.Direction.DESC)
            Pageable pageable) {
        return service.listByBuffet(buffetId, statusEvento, status, dataEventoFrom, dataEventoTo, pageable);
    }

    @Operation(
        summary = "Excluir evento",
        description = "Remove um evento. Por padrão, realiza exclusão lógica (soft delete)."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Evento excluído com sucesso", content = @Content),
        @ApiResponse(responseCode = "403", description = "Usuário não autorizado", content = @Content),
        @ApiResponse(responseCode = "404", description = "Evento não encontrado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID do buffet", required = true)
            @RequestHeader(value = BUFFET_ID_HEADER) Long buffetId,
            @Parameter(description = "ID do evento", required = true)
            @PathVariable Long id,
            @Parameter(description = "ID do proprietário do buffet", required = true)
            @RequestParam Long ownerId,
            @Parameter(description = "Realizar exclusão lógica (true) ou física (false). Padrão: true")
            @RequestParam(defaultValue = "true") boolean soft) {
        service.delete(buffetId, id, ownerId, soft);
        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "Obter datas indisponíveis",
        description = "Retorna as datas em que o buffet já possui eventos confirmados no período especificado."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Datas indisponíveis retornadas com sucesso",
            content = @Content(schema = @Schema(implementation = DatasIndisponiveisResponse.class)))
    })
    @GetMapping("/datas-indisponiveis")
    public ResponseEntity<DatasIndisponiveisResponse> getDatasIndisponiveis(
            @Parameter(description = "ID do buffet", required = true)
            @RequestHeader(value = BUFFET_ID_HEADER) Long buffetId,
            @Parameter(description = "Data inicial do período (formato ISO: yyyy-MM-dd)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @Parameter(description = "Data final do período (formato ISO: yyyy-MM-dd)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {

        DatasIndisponiveisResponse datas = service.getDatasIndisponiveis(
                buffetId,
                dataInicio,
                dataFim
        );

        return ResponseEntity.ok(datas);
    }

    @Operation(
        summary = "Listar meus eventos",
        description = "Retorna uma lista paginada de eventos do cliente autenticado."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de eventos do cliente retornada com sucesso",
            content = @Content(schema = @Schema(implementation = Page.class)))
    })
    @GetMapping("/me")
    public Page<EventoResponse> listarMeusEventos(
            @Parameter(description = "ID do buffet", required = true)
            @RequestHeader(value = Constants.BUFFET_ID_HEADER) Long buffetId,
            @Parameter(description = "ID do cliente", required = true)
            @RequestParam Long clienteId,
            @Parameter(description = "Configuração de paginação e ordenação", hidden = true)
            @PageableDefault(size = 20, sort = "dataCriacao", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return service.listarMeusEventos(buffetId, clienteId, pageable);
    }

    @Operation(
        summary = "Atualizar valor do evento (Buffet)",
        description = "Permite ao proprietário do buffet atualizar o valor estimado do evento. Só pode ser alterado até 3 dias antes do evento e apenas eventos AGENDADOS."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Valor do evento atualizado com sucesso",
            content = @Content(schema = @Schema(implementation = EventoResponse.class))),
        @ApiResponse(responseCode = "400", description = "Valor inválido fornecido", content = @Content),
        @ApiResponse(responseCode = "403", description = "Usuário não autorizado", content = @Content),
        @ApiResponse(responseCode = "404", description = "Evento não encontrado", content = @Content),
        @ApiResponse(responseCode = "409", description = "Evento não está agendado ou prazo de 3 dias já passou", content = @Content)
    })
    @PutMapping("/{id}/valor")
    public EventoResponse updateValor(
            @Parameter(description = "ID do buffet", required = true)
            @RequestHeader(value = BUFFET_ID_HEADER) Long buffetId,
            @Parameter(description = "ID do evento", required = true)
            @PathVariable Long id,
            @Parameter(description = "Novo valor do evento", required = true)
            @Valid @RequestBody com.ebuffet.controller.dto.evento.UpdateValorRequest req,
            @Parameter(description = "ID do proprietário do buffet", required = true)
            @RequestParam Long ownerId
    ) {
        return service.updateValor(buffetId, id, req.getValor(), ownerId);
    }

    @Operation(
        summary = "Concluir evento",
        description = "Marca um evento como concluído. Apenas eventos confirmados podem ser concluídos."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Evento concluído com sucesso",
            content = @Content(schema = @Schema(implementation = EventoResponse.class))),
        @ApiResponse(responseCode = "400", description = "Evento não pode ser concluído no estado atual", content = @Content),
        @ApiResponse(responseCode = "403", description = "Usuário não autorizado", content = @Content),
        @ApiResponse(responseCode = "404", description = "Evento não encontrado", content = @Content)
    })
    @PutMapping("/{id}/concluir")
    public EventoResponse concluir(
            @Parameter(description = "ID do buffet", required = true)
            @RequestHeader(value = BUFFET_ID_HEADER) Long buffetId,
            @Parameter(description = "ID do evento", required = true)
            @PathVariable Long id,
            @Parameter(description = "ID do proprietário do buffet", required = true)
            @RequestParam Long ownerId
    ) {
        return service.concluirEvento(buffetId, id, ownerId);
    }

    @Operation(
        summary = "Cancelar evento (Buffet)",
        description = "Cancela um evento. Apenas eventos AGENDADOS ou PENDENTES podem ser cancelados. Apenas o dono do buffet pode usar este endpoint."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Evento cancelado com sucesso",
            content = @Content(schema = @Schema(implementation = EventoResponse.class))),
        @ApiResponse(responseCode = "403", description = "Usuário não autorizado", content = @Content),
        @ApiResponse(responseCode = "404", description = "Evento não encontrado", content = @Content),
        @ApiResponse(responseCode = "409", description = "Evento não pode ser cancelado no estado atual", content = @Content)
    })
    @PutMapping("/{id}/cancelar")
    public EventoResponse cancelar(
            @Parameter(description = "ID do buffet", required = true)
            @RequestHeader(value = BUFFET_ID_HEADER) Long buffetId,
            @Parameter(description = "ID do evento", required = true)
            @PathVariable Long id,
            @Parameter(description = "ID do proprietário do buffet", required = true)
            @RequestParam Long ownerId
    ) {
        return service.cancelarEvento(buffetId, id, ownerId);
    }

    @Operation(
        summary = "Reverter cancelamento do evento (Buffet)",
        description = "Reverte o cancelamento de um evento, retornando ao status AGENDADO. Apenas o dono do buffet pode reverter cancelamentos. A data do evento não pode ter passado."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cancelamento revertido com sucesso",
            content = @Content(schema = @Schema(implementation = EventoResponse.class))),
        @ApiResponse(responseCode = "403", description = "Usuário não autorizado", content = @Content),
        @ApiResponse(responseCode = "404", description = "Evento não encontrado", content = @Content),
        @ApiResponse(responseCode = "409", description = "Evento não está cancelado ou data já passou", content = @Content)
    })
    @PutMapping("/{id}/reverter-cancelamento")
    public EventoResponse reverterCancelamento(
            @Parameter(description = "ID do buffet", required = true)
            @RequestHeader(value = BUFFET_ID_HEADER) Long buffetId,
            @Parameter(description = "ID do evento", required = true)
            @PathVariable Long id,
            @Parameter(description = "ID do proprietário do buffet", required = true)
            @RequestParam Long ownerId
    ) {
        return service.reverterCancelamentoEvento(buffetId, id, ownerId);
    }

    @Operation(
        summary = "Cancelar evento (Cliente)",
        description = "Permite ao cliente cancelar seu próprio evento. Apenas eventos AGENDADOS ou PENDENTES podem ser cancelados. O cliente NÃO pode reverter o cancelamento."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Evento cancelado com sucesso",
            content = @Content(schema = @Schema(implementation = EventoResponse.class))),
        @ApiResponse(responseCode = "403", description = "Evento não pertence a este cliente", content = @Content),
        @ApiResponse(responseCode = "404", description = "Evento não encontrado", content = @Content),
        @ApiResponse(responseCode = "409", description = "Evento não pode ser cancelado no estado atual", content = @Content)
    })
    @PutMapping("/{id}/cliente/cancelar")
    public EventoResponse cancelarPeloCliente(
            @Parameter(description = "ID do buffet", required = true)
            @RequestHeader(value = BUFFET_ID_HEADER) Long buffetId,
            @Parameter(description = "ID do evento", required = true)
            @PathVariable Long id,
            @Parameter(description = "ID do cliente", required = true)
            @RequestParam Long clienteId
    ) {
        return service.cancelarEventoPeloCliente(buffetId, id, clienteId);
    }

    @Operation(
        summary = "Atualizar evento (Cliente)",
        description = """
            Permite ao cliente editar seu evento até 5 dias antes da data do evento.

            **Campos que podem ser alterados:**
            - Comidas do cardápio
            - Serviços contratados
            - Quantidade de pessoas
            - Data e horário do evento
            - Endereço do evento

            **Restrições:**
            - Somente eventos AGENDADOS ou PENDENTES podem ser editados
            - Prazo: até 5 dias antes do evento
            - Nova data do evento não pode estar no passado
            """
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Evento atualizado com sucesso",
            content = @Content(schema = @Schema(implementation = EventoResponse.class))),
        @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos (ex: quantidade <= 0)", content = @Content),
        @ApiResponse(responseCode = "403", description = "Evento não pertence a este cliente", content = @Content),
        @ApiResponse(responseCode = "404", description = "Evento não encontrado", content = @Content),
        @ApiResponse(responseCode = "409", description = "Evento não está agendado/pendente, prazo de 5 dias já passou, ou data inválida", content = @Content)
    })
    @PutMapping("/{id}/cliente")
    public EventoResponse atualizarPeloCliente(
            @Parameter(description = "ID do buffet", required = true)
            @RequestHeader(value = BUFFET_ID_HEADER) Long buffetId,
            @Parameter(description = "ID do evento", required = true)
            @PathVariable Long id,
            @Parameter(description = "ID do cliente", required = true)
            @RequestParam Long clienteId,
            @Parameter(description = """
                Dados do evento a serem atualizados. Todos os campos são opcionais:
                - comidaIds: Lista de IDs das comidas
                - servicoIds: Lista de IDs dos serviços
                - qtdPessoas: Quantidade de pessoas (mínimo 1)
                - dataEvento: Nova data do evento
                - horaEvento: Novo horário do evento
                - endereco: Novo endereço do evento
                """, required = true)
            @Valid @RequestBody ClienteEventoUpdateRequest req
    ) {
        return service.atualizarEventoPeloCliente(buffetId, id, clienteId, req);
    }
}
