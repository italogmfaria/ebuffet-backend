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

@Tag(name = "Serviços", description = "API para gerenciamento de serviços oferecidos pelo buffet")
@RestController
@RequestMapping("/api/servicos")
public class ServicoController {

    private final ServicoService service;

    public ServicoController(ServicoService service) {
        this.service = service;
    }

    @Operation(summary = "Cadastrar novo serviço")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Serviço criado com sucesso",
            content = @Content(schema = @Schema(implementation = ServicoResponse.class))),
        @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos", content = @Content),
        @ApiResponse(responseCode = "404", description = "Buffet não encontrado", content = @Content)
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ServicoResponse> create(
            @Valid @RequestPart("servico") ServicoRequest req,
            @RequestPart(value = "imagem", required = false) MultipartFile imagem,
            @RequestParam Long ownerId) {
        ServicoResponse created = service.create(req, imagem, ownerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Buscar serviço por ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Serviço encontrado com sucesso",
            content = @Content(schema = @Schema(implementation = ServicoResponse.class))),
        @ApiResponse(responseCode = "404", description = "Serviço não encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    public ServicoResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @Operation(summary = "Listar serviços do buffet")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de serviços retornada com sucesso",
            content = @Content(schema = @Schema(implementation = Page.class)))
    })
    @GetMapping
    public Page<ServicoResponse> list(
            @RequestParam(required = false) EnumCategoria categoria,
            @RequestParam(required = false) EnumStatus status,
            @RequestParam(required = false, name = "q") String q,
            @PageableDefault(size = 20, sort = "dataCriacao", direction = Sort.Direction.DESC)
            Pageable pageable) {
        return service.listByBuffet(categoria, status, q, pageable);
    }

    @Operation(summary = "Atualizar serviço")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Serviço atualizado com sucesso",
            content = @Content(schema = @Schema(implementation = ServicoResponse.class))),
        @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos", content = @Content),
        @ApiResponse(responseCode = "404", description = "Serviço não encontrado", content = @Content),
        @ApiResponse(responseCode = "409", description = "Serviço não pertence ao buffet", content = @Content)
    })
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ServicoResponse update(
            @PathVariable Long id,
            @Valid @RequestPart("servico") ServicoRequest req,
            @RequestPart(value = "imagem", required = false) MultipartFile imagem,
            @RequestParam Long ownerId) {
        return service.update(id, req, imagem, ownerId);
    }

    @Operation(summary = "Excluir serviço")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Serviço excluído com sucesso", content = @Content),
        @ApiResponse(responseCode = "404", description = "Serviço não encontrado", content = @Content),
        @ApiResponse(responseCode = "409", description = "Serviço não pertence ao buffet", content = @Content)
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
