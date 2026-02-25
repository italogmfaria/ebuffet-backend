package com.ebuffet.controller.dto.evento;

import com.ebuffet.models.Evento;
import com.ebuffet.models.enums.EnumStatus;
import com.ebuffet.models.enums.EnumStatusEvento;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventoResponse {
    private Long id;
    private String nome;
    private EnumStatusEvento statusEvento;
    private EnumStatus status;

    private Long buffetId;
    private Long reservaId;
    private Long clienteId;

    private LocalDate dataEvento;
    private LocalTime horaEvento;
    private BigDecimal valor;
    private String descricao;

    public EventoResponse(Evento e) {
        this.id = e.getId();
        this.nome = e.getNome();
        this.statusEvento = e.getStatusEvento();
        this.status = e.getStatus();

        this.buffetId = e.getBuffet() != null ? e.getBuffet().getId() : null;

        this.reservaId = (e.getReserva() != null) ? e.getReserva().getId() : null;

        this.clienteId = (e.getReserva() != null && e.getReserva().getCliente() != null)
                ? e.getReserva().getCliente().getId()
                : null;

        this.dataEvento = e.getDataEvento();
        this.horaEvento = e.getHoraEvento();
        this.valor = e.getValor();
        this.descricao = e.getDescricao();
    }
}
