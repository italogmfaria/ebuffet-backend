package com.ebuffet.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Buffet extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @OneToOne(optional = false, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "endereco_id",
            foreignKey = @ForeignKey(name = "fk_buffet_endereco"))
    private Endereco endereco;

    private String telefone;

    private String email;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_buffet_owner"))
    private User owner;

    @OneToMany(mappedBy = "buffet")
    private List<Servico> servicos = new ArrayList<>();

    @OneToMany(mappedBy = "buffet")
    private List<Comida> comidas = new ArrayList<>();

    @OneToMany(mappedBy = "buffet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Evento> eventos = new ArrayList<>();
}
