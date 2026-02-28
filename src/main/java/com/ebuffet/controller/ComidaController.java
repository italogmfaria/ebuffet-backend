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

@Tag(name = "Comidas", description = "API para gerenciamento de comidas oferecidas pelo buffet")
@RestController
@RequestMapping("/api/comidas")
public class ComidaController {

    private final ComidaService service;

    public ComidaController(ComidaService service) {
        this.service = service;
    }

    @Operation(summary = "Cadastrar nova comida")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Comida criada com sucesso",
            content = @Content(schema = @Schema(implementation = ComidaResponse.class))),
        @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos", content = @Content),
        @ApiResponse(responseCode = "403", description = "Usuário não autorizado", content = @Content),
        @ApiResponse(responseCode = "404", description = "Buffet não encontrado", content = @Content)
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ComidaResponse> create(
            @Valid @RequestPart("comida") ComidaRequest req,
            @RequestPart(value = "imagem", required = false) MultipartFile imagem,
            @RequestParam Long ownerId) {
        ComidaResponse created = service.create(req, imagem, ownerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Buscar comida por ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Comida encontrada com sucesso",
            content = @Content(schema = @Schema(implementation = ComidaResponse.class))),
        @ApiResponse(responseCode = "404", description = "Comida não encontrada", content = @Content)
    })
    @GetMapping("/{id}")
    public ComidaResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @Operation(summary = "Listar comidas do buffet")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de comidas retornada com sucesso",
            content = @Content(schema = @Schema(implementation = Page.class)))
    })
    @GetMapping
    public Page<ComidaResponse> list(
            @RequestParam(required = false) EnumCategoria categoria,
            @RequestParam(required = false) EnumStatus status,
            @PageableDefault(size = 20, sort = "dataCriacao", direction = Sort.Direction.DESC)
            Pageable pageable) {
        return service.listByBuffet(categoria, status, pageable);
    }

    @Operation(summary = "Atualizar comida")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Comida atualizada com sucesso",
            content = @Content(schema = @Schema(implementation = ComidaResponse.class))),
        @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos", content = @Content),
        @ApiResponse(responseCode = "403", description = "Usuário não autorizado", content = @Content),
        @ApiResponse(responseCode = "404", description = "Comida ou buffet não encontrado", content = @Content)
    })
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ComidaResponse update(
            @PathVariable Long id,
            @Valid @RequestPart("comida") ComidaRequest req,
            @RequestPart(value = "imagem", required = false) MultipartFile imagem,
            @RequestParam Long ownerId) {
        return service.update(id, req, imagem, ownerId);
    }

    @Operation(summary = "Excluir comida")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Comida excluída com sucesso", content = @Content),
        @ApiResponse(responseCode = "403", description = "Usuário não autorizado", content = @Content),
        @ApiResponse(responseCode = "404", description = "Comida ou buffet não encontrado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @RequestParam Long ownerId,
            @RequestParam(defaultValue = "true") boolean soft) {
        service.delete(id, ownerId, soft);
        return ResponseEntity.noContent().build();
    }
}
