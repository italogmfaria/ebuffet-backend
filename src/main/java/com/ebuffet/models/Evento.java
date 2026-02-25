package com.ebuffet.models;

import com.ebuffet.models.enums.EnumStatusEvento;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@Setter
public class Evento extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EnumStatusEvento statusEvento = EnumStatusEvento.AGENDADO;

    @OneToOne(optional = false)
    @JoinColumn(name = "reserva_id", unique = true)
    private Reserva reserva;

    private BigDecimal valor;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "buffet_id", foreignKey = @ForeignKey(name = "fk_evento_buffet"))
    private Buffet buffet;

    private LocalDate dataEvento;

    private LocalTime horaEvento;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(nullable = false)
    private Boolean bloquearCalendario = false;
}
