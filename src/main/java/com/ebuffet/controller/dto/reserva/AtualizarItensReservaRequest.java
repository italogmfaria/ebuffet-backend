package com.ebuffet.controller.dto.reserva;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AtualizarItensReservaRequest {
    private List<Long> servicoIds;
    private List<Long> comidaIds;
}
