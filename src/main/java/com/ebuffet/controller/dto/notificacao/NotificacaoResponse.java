package com.ebuffet.controller.dto.notificacao;

import com.ebuffet.models.Notificacao;
import com.ebuffet.models.enums.EnumStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NotificacaoResponse {

    private Long id;
    private String titulo;
    private String mensagem;
    private Boolean lida;
    private Long reservaId;
    private EnumStatus status;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataLeitura;

    public NotificacaoResponse(Notificacao n) {
        this.id = n.getId();
        this.titulo = n.getTitulo();
        this.mensagem = n.getMensagem();
        this.lida = n.getLida();
        this.reservaId = n.getReserva() != null ? n.getReserva().getId() : null;
        this.status = n.getStatus();
        this.dataCriacao = n.getDataCriacao();
        this.dataLeitura = n.getDataLeitura();
    }
}
