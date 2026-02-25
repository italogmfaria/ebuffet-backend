package com.ebuffet.controller.dto.evento;

import com.ebuffet.models.enums.EnumStatus;
import com.ebuffet.models.enums.EnumStatusEvento;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
public class EventoUpdateRequest {
    @NotBlank
    private String nome;

    @NotNull
    private EnumStatusEvento statusEvento;

    @NotNull
    private EnumStatus status;

    @NotNull
    private LocalDate dataEvento;

    @NotNull
    private LocalTime horaEvento;

    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal valor;

    private String descricao;

    private List<Long> comidaIds;

    private List<Long> servicoIds;
}
