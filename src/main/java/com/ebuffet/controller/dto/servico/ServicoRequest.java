package com.ebuffet.controller.dto.servico;

import com.ebuffet.models.enums.EnumCategoria;
import com.ebuffet.models.enums.EnumStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServicoRequest {

    @NotBlank
    private String nome;

    @NotBlank
    private String descricao;

    @NotNull
    private EnumCategoria categoria;

    @NotNull
    private EnumStatus status;
}
