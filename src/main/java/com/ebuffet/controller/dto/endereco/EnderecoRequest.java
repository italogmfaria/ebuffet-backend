package com.ebuffet.controller.dto.endereco;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EnderecoRequest {

    @NotBlank
    @Size(max = 120)
    private String rua;

    @NotBlank
    @Size(max = 20)
    private String numero;

    @Size(max = 80)
    private String bairro;

    @NotBlank
    @Size(max = 80)
    private String cidade;

    @NotBlank
    @Size(min = 2, max = 2)
    private String estado;

    @NotBlank
    @Size(max = 9)
    private String cep;

    @Size(max = 120)
    private String complemento;
}
