package com.ebuffet.controller.dto.buffet;

import com.ebuffet.controller.dto.endereco.EnderecoRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BuffetRequest {

    @NotNull
    private Long ownerId;

    @NotBlank
    private String nome;

    @Size(max = 20)
    private String telefone;

    @Email
    @Size(max = 120)
    private String email;

    @Valid
    @NotNull
    private EnderecoRequest endereco;
}
