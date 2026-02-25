package com.ebuffet.controller;

import com.ebuffet.controller.dto.comida.ComidaRequest;
import com.ebuffet.controller.dto.comida.ComidaResponse;
import com.ebuffet.models.enums.EnumCategoria;
import com.ebuffet.models.enums.EnumStatus;
import com.ebuffet.service.ComidaService;
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

@Tag(name = "Comidas", description = "API para gerenciamento de comidas oferecidas pelos buffets")
@RestController
@RequestMapping("/api/comidas")
public class ComidaController {

    private final ComidaService service;

    public ComidaController(ComidaService service) {
        this.service = service;
    }

    @Operation(
        summary = "Cadastrar nova comida",
        description = "Cria uma nova comida no cardápio do buffet. Permite o upload de uma imagem opcional da comida."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Comida criada com sucesso",
            content = @Content(schema = @Schema(implementation = ComidaResponse.class))),
        @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos", content = @Content),
        @ApiResponse(responseCode = "403", description = "Usuário não autorizado a criar comidas para este buffet", content = @Content),
        @ApiResponse(responseCode = "404", description = "Buffet não encontrado", content = @Content)
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ComidaResponse> create(
            @Parameter(description = "ID do buffet", required = true)
            @RequestHeader(value = BUFFET_ID_HEADER) Long buffetId,
            @Parameter(description = "Dados da comida", required = true)
            @Valid @RequestPart("comida") ComidaRequest req,
            @Parameter(description = "Imagem da comida (opcional, máximo 5MB)")
            @RequestPart(value = "imagem", required = false) MultipartFile imagem,
            @Parameter(description = "ID do proprietário do buffet", required = true)
            @RequestParam Long ownerId) {
        ComidaResponse created = service.create(buffetId, req, imagem, ownerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(
        summary = "Buscar comida por ID",
        description = "Retorna os detalhes de uma comida específica pelo seu identificador."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Comida encontrada com sucesso",
            content = @Content(schema = @Schema(implementation = ComidaResponse.class))),
        @ApiResponse(responseCode = "404", description = "Comida não encontrada", content = @Content)
    })
    @GetMapping("/{id}")
    public ComidaResponse get(
            @Parameter(description = "ID da comida", required = true)
            @PathVariable Long id) {
        return service.get(id);
    }

    @Operation(
        summary = "Listar comidas do buffet",
        description = "Retorna uma lista paginada de comidas de um buffet. Permite filtrar por categoria e status."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de comidas retornada com sucesso",
            content = @Content(schema = @Schema(implementation = Page.class)))
    })
    @GetMapping
    public Page<ComidaResponse> list(
            @Parameter(description = "ID do buffet", required = true)
            @RequestHeader(value = BUFFET_ID_HEADER) Long buffetId,
            @Parameter(description = "Filtrar por categoria (ENTRADA, PRINCIPAL, SOBREMESA, etc.)")
            @RequestParam(required = false) EnumCategoria categoria,
            @Parameter(description = "Filtrar por status (ATIVO, INATIVO)")
            @RequestParam(required = false) EnumStatus status,
            @Parameter(description = "Configuração de paginação e ordenação", hidden = true)
            @PageableDefault(size = 20, sort = "dataCriacao", direction = Sort.Direction.DESC)
            Pageable pageable) {
        return service.listByBuffet(buffetId, categoria, status, pageable);
    }

    @Operation(
        summary = "Atualizar comida",
        description = "Atualiza os dados de uma comida existente. Permite atualizar a imagem da comida."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Comida atualizada com sucesso",
            content = @Content(schema = @Schema(implementation = ComidaResponse.class))),
        @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos", content = @Content),
        @ApiResponse(responseCode = "403", description = "Usuário não autorizado a atualizar esta comida", content = @Content),
        @ApiResponse(responseCode = "404", description = "Comida ou buffet não encontrado", content = @Content)
    })
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ComidaResponse update(
            @Parameter(description = "ID do buffet", required = true)
            @RequestHeader(value = BUFFET_ID_HEADER) Long buffetId,
            @Parameter(description = "ID da comida", required = true)
            @PathVariable Long id,
            @Parameter(description = "Novos dados da comida", required = true)
            @Valid @RequestPart("comida") ComidaRequest req,
            @Parameter(description = "Nova imagem da comida (opcional, máximo 5MB)")
            @RequestPart(value = "imagem", required = false) MultipartFile imagem,
            @Parameter(description = "ID do proprietário do buffet", required = true)
            @RequestParam Long ownerId) {
        return service.update(buffetId, id, req, imagem, ownerId);
    }

    @Operation(
        summary = "Excluir comida",
        description = "Remove uma comida do cardápio. Por padrão, realiza exclusão lógica (soft delete), mas pode realizar exclusão física se especificado."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Comida excluída com sucesso", content = @Content),
        @ApiResponse(responseCode = "403", description = "Usuário não autorizado a excluir esta comida", content = @Content),
        @ApiResponse(responseCode = "404", description = "Comida ou buffet não encontrado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID do buffet", required = true)
            @RequestHeader(value = BUFFET_ID_HEADER) Long buffetId,
            @Parameter(description = "ID da comida", required = true)
            @PathVariable Long id,
            @Parameter(description = "ID do proprietário do buffet", required = true)
            @RequestParam Long ownerId,
            @Parameter(description = "Realizar exclusão lógica (true) ou física (false). Padrão: true")
            @RequestParam(defaultValue = "true") boolean soft) {
        service.delete(buffetId, id, ownerId, soft);
        return ResponseEntity.noContent().build();
    }
}
