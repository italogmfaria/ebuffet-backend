package com.ebuffet.models;

import com.ebuffet.models.enums.EnumStatusReserva;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Reserva extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_reserva_cliente"))
    private User cliente;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "buffet_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_reserva_buffet"))
    private Buffet buffet;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EnumStatusReserva statusReserva = EnumStatusReserva.PENDENTE;

    @OneToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "endereco_id",
            foreignKey = @ForeignKey(name = "fk_reserva_endereco"))
    private Endereco endereco;

    private Integer qtdPessoas;
    private LocalTime horarioDesejado;
    private LocalDate dataDesejada;

    @ManyToMany
    @JoinTable(
            name = "reserva_servico",
            joinColumns = @JoinColumn(name = "reserva_id"),
            inverseJoinColumns = @JoinColumn(name = "servico_id"),
            uniqueConstraints = @UniqueConstraint(
                    name = "uk_reserva_servico",
                    columnNames = {"reserva_id","servico_id"})
    )
    private List<Servico> servicos = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "reserva_comida",
            joinColumns = @JoinColumn(name = "reserva_id"),
            inverseJoinColumns = @JoinColumn(name = "comida_id")
    )
    private List<Comida> comidas = new ArrayList<>();

    @OneToOne(mappedBy = "reserva", cascade = CascadeType.ALL, orphanRemoval = true)
    private Evento evento;

    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descricao;
}

