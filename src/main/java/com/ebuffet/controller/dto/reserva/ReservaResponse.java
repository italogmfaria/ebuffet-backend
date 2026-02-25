package com.ebuffet.controller.dto.reserva;

import com.ebuffet.controller.dto.comida.ComidaResumoResponse;
import com.ebuffet.controller.dto.endereco.EnderecoResponse;
import com.ebuffet.controller.dto.servico.ServicoResumoResponse;
import com.ebuffet.models.Reserva;
import com.ebuffet.models.enums.EnumStatus;
import com.ebuffet.models.enums.EnumStatusReserva;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
public class ReservaResponse {

    private Long id;
    private EnumStatusReserva statusReserva;
    private EnumStatus status;

    private Long buffetId;
    private Long clienteId;
    private String nomeCliente;
    private String emailCliente;
    private String telefoneCliente;
    private Long eventoId;

    private LocalDate dataDesejada;
    private LocalTime horarioDesejado;
    private Integer qtdPessoas;

    private String titulo;
    private String descricao;
    private EnderecoResponse endereco;
    private List<ComidaResumoResponse> comidas;
    private List<ServicoResumoResponse> servicos;

    public static ReservaResponse of(Reserva r) {
        ReservaResponse dto = new ReservaResponse();

        dto.id = r.getId();
        dto.statusReserva = r.getStatusReserva();
        dto.status = r.getStatus();

        dto.buffetId = r.getBuffet().getId();
        dto.clienteId = r.getCliente().getId();
        dto.nomeCliente = r.getCliente().getNome();
        dto.emailCliente = r.getCliente().getEmail();
        dto.telefoneCliente = r.getCliente().getTelefone();
        dto.eventoId = (r.getEvento() != null ? r.getEvento().getId() : null);

        dto.dataDesejada = r.getDataDesejada();
        dto.horarioDesejado = r.getHorarioDesejado();
        dto.qtdPessoas = r.getQtdPessoas();

        dto.titulo = r.getTitulo();
        dto.descricao = r.getDescricao();
        dto.endereco = EnderecoResponse.of(r.getEndereco());

        if (r.getComidas() != null) {
            dto.comidas = r.getComidas()
                    .stream()
                    .map(ComidaResumoResponse::of)
                    .toList();
        } else {
            dto.comidas = List.of();
        }

        if (r.getServicos() != null) {
            dto.servicos = r.getServicos()
                    .stream()
                    .map(ServicoResumoResponse::of)
                    .toList();
        } else {
            dto.servicos = List.of();
        }

        return dto;
    }
}
