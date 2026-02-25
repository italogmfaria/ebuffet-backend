package com.ebuffet.controller;

import com.ebuffet.controller.dto.servico.ServicoRequest;
import com.ebuffet.controller.dto.servico.ServicoResponse;
import com.ebuffet.models.enums.EnumCategoria;
import com.ebuffet.models.enums.EnumStatus;
import com.ebuffet.service.ServicoService;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static com.ebuffet.utils.Constants.BUFFET_ID_HEADER;

@Tag(name = "Serviços", description = "API para gerenciamento de serviços oferecidos pelos buffets")
@RestController
@RequestMapping("/api/servicos")
public class ServicoController {

    private final ServicoService service;

    public ServicoController(ServicoService service) {
        this.service = service;
    }

    @Operation(
        summary = "Cadastrar novo serviço",
        description = "Cria um novo serviço no portfólio do buffet. Permite o upload de uma imagem opcional do serviço."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Serviço criado com sucesso",
            content = @Content(schema = @Schema(implementation = ServicoResponse.class))),
        @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos", content = @Content),
        @ApiResponse(responseCode = "404", description = "Buffet não encontrado", content = @Content)
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ServicoResponse> create(
            @Parameter(description = "ID do buffet", required = true)
            @RequestHeader(value = BUFFET_ID_HEADER) Long buffetId,
            @Parameter(description = "Dados do serviço", required = true)
            @Valid @RequestPart("servico") ServicoRequest req,
            @Parameter(description = "Imagem do serviço (opcional, máximo 5MB)")
            @RequestPart(value = "imagem", required = false) MultipartFile imagem,
            @Parameter(description = "ID do proprietário do buffet", required = true)
            @RequestParam Long ownerId) {
        ServicoResponse created = service.create(buffetId, req, imagem, ownerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(
        summary = "Buscar serviço por ID",
        description = "Retorna os detalhes de um serviço específico pelo seu identificador."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Serviço encontrado com sucesso",
            content = @Content(schema = @Schema(implementation = ServicoResponse.class))),
        @ApiResponse(responseCode = "404", description = "Serviço não encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    public ServicoResponse get(
            @Parameter(description = "ID do serviço", required = true)
            @PathVariable Long id) {
        return service.get(id);
    }

    @Operation(
        summary = "Listar serviços do buffet",
        description = "Retorna uma lista paginada de serviços de um buffet. Permite filtrar por categoria, status e busca por texto."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de serviços retornada com sucesso",
            content = @Content(schema = @Schema(implementation = Page.class)))
    })
    @GetMapping
    public Page<ServicoResponse> list(
            @Parameter(description = "ID do buffet", required = true)
            @RequestHeader(value = BUFFET_ID_HEADER) Long buffetId,
            @Parameter(description = "Filtrar por categoria (FOTOGRAFIA, DECORACAO, MUSICA, etc.)")
            @RequestParam(required = false) EnumCategoria categoria,
            @Parameter(description = "Filtrar por status (ATIVO, INATIVO)")
            @RequestParam(required = false) EnumStatus status,
            @Parameter(description = "Buscar por texto no nome ou descrição")
            @RequestParam(required = false, name = "q") String q,
            @Parameter(description = "Configuração de paginação e ordenação", hidden = true)
            @PageableDefault(size = 20, sort = "dataCriacao", direction = Sort.Direction.DESC)
            Pageable pageable) {
        return service.listByBuffet(buffetId, categoria, status, q, pageable);
    }

    @Operation(
        summary = "Atualizar serviço",
        description = "Atualiza os dados de um serviço existente. Permite atualizar a imagem do serviço."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Serviço atualizado com sucesso",
            content = @Content(schema = @Schema(implementation = ServicoResponse.class))),
        @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos", content = @Content),
        @ApiResponse(responseCode = "404", description = "Serviço não encontrado", content = @Content),
        @ApiResponse(responseCode = "409", description = "Serviço não pertence ao buffet informado", content = @Content)
    })
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ServicoResponse update(
            @Parameter(description = "ID do buffet", required = true)
            @RequestHeader(value = BUFFET_ID_HEADER) Long buffetId,
            @Parameter(description = "ID do serviço", required = true)
            @PathVariable Long id,
            @Parameter(description = "Novos dados do serviço", required = true)
            @Valid @RequestPart("servico") ServicoRequest req,
            @Parameter(description = "Nova imagem do serviço (opcional, máximo 5MB)")
            @RequestPart(value = "imagem", required = false) MultipartFile imagem,
            @Parameter(description = "ID do proprietário do buffet", required = true)
            @RequestParam Long ownerId) {
        return service.update(buffetId, id, req, imagem, ownerId);
    }

    @Operation(
        summary = "Excluir serviço",
        description = "Remove um serviço do portfólio. Por padrão, realiza exclusão lógica (soft delete), mas pode realizar exclusão física se especificado."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Serviço excluído com sucesso", content = @Content),
        @ApiResponse(responseCode = "404", description = "Serviço não encontrado", content = @Content),
        @ApiResponse(responseCode = "409", description = "Serviço não pertence ao buffet informado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID do buffet", required = true)
            @RequestHeader(value = BUFFET_ID_HEADER) Long buffetId,
            @Parameter(description = "ID do serviço", required = true)
            @PathVariable Long id,
            @Parameter(description = "ID do proprietário do buffet", required = true)
            @RequestParam Long ownerId,
            @Parameter(description = "Realizar exclusão lógica (true) ou física (false). Padrão: true")
            @RequestParam(defaultValue = "true") boolean soft) {
        service.delete(buffetId, id, ownerId, soft);
        return ResponseEntity.noContent().build();
    }
}

