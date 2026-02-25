package com.ebuffet.models;

import com.ebuffet.models.enums.EnumCategoria;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Comida extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(length = 1000)
    private String descricao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "imagem_id", foreignKey = @ForeignKey(name = "fk_comida_imagem"))
    private Arquivo imagem;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EnumCategoria categoria;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "buffet_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_comida_buffet"))
    private Buffet buffet;
}
