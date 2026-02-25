package com.ebuffet.controller.dto.evento;

import com.ebuffet.controller.dto.endereco.EnderecoRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
public class ClienteEventoUpdateRequest {

    private List<Long> comidaIds;

    private List<Long> servicoIds;

    @Min(value = 1, message = "Quantidade de pessoas deve ser maior que zero")
    private Integer qtdPessoas;

    private LocalDate dataEvento;

    private LocalTime horaEvento;

    @Valid
    private EnderecoRequest endereco;
}
