package com.ebuffet.controller.dto.comida;

import com.ebuffet.models.enums.EnumCategoria;
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
public class ComidaRequest {

    @NotBlank
    private String nome;

    @Size(max = 1000)
    private String descricao;

    @NotNull
    private EnumCategoria categoria;
}
