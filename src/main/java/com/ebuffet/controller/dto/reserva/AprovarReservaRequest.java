package com.ebuffet.controller.dto.reserva;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class AprovarReservaRequest {
    private BigDecimal valor;
    private Boolean blockDay;
}
