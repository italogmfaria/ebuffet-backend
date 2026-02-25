package com.ebuffet.controller.dto.reserva;

import com.ebuffet.controller.dto.endereco.EnderecoRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
public class ReservaRequest {
    @NotNull
    private Long buffetId;

    @NotNull
    private Integer qtdPessoas;

    @NotNull
    private LocalDate dataDesejada;

    @NotNull
    private LocalTime horarioDesejado;

    @Valid
    @NotNull
    private EnderecoRequest endereco;

    private List<Long> servicoIds;
    private List<Long> comidaIds;
    private String titulo;
    private String descricao;
}
