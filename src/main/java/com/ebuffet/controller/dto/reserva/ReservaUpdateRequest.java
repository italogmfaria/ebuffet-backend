package com.ebuffet.controller.dto.reserva;

import com.ebuffet.controller.dto.endereco.EnderecoRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class ReservaUpdateRequest {

    @NotNull(message = "Quantidade de pessoas é obrigatória")
    @Min(value = 1, message = "Quantidade de pessoas deve ser maior que zero")
    private Integer qtdPessoas;

    @NotNull(message = "Data desejada é obrigatória")
    private LocalDate dataDesejada;

    @NotNull(message = "Horário desejado é obrigatório")
    private LocalTime horarioDesejado;

    @Valid
    @NotNull(message = "Endereço é obrigatório")
    private EnderecoRequest endereco;

    private String titulo;
    private String descricao;
}
