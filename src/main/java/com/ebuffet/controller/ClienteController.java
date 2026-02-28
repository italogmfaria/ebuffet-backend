package com.ebuffet.controller;

import com.ebuffet.controller.dto.register.UserResponse;
import com.ebuffet.controller.dto.user.UpdateUserRequest;
import com.ebuffet.models.User;
import com.ebuffet.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static com.ebuffet.utils.Constants.USER_ID_HEADER;

@Tag(name = "Clientes", description = "API para gerenciamento de perfil de clientes")
@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final UserService userService;

    public ClienteController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Atualizar perfil do cliente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Perfil atualizado com sucesso",
            content = @Content(schema = @Schema(implementation = UserResponse.class))),
        @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos", content = @Content),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content),
        @ApiResponse(responseCode = "409", description = "E-mail ou telefone já cadastrado", content = @Content)
    })
    @PutMapping(value = "/me", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserResponse> updateProfile(
            @RequestHeader(value = USER_ID_HEADER) Long userId,
            @Valid @RequestPart("cliente") UpdateUserRequest req,
            @RequestPart(value = "foto", required = false) MultipartFile foto
    ) {
        User user = userService.updateUser(userId, req, foto);
        return ResponseEntity.ok(new UserResponse(user));
    }
}
