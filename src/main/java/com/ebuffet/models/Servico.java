package com.ebuffet.models;

import com.ebuffet.models.enums.EnumCategoria;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Servico extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String descricao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "imagem_id", foreignKey = @ForeignKey(name = "fk_servico_imagem"))
    private Arquivo imagem;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EnumCategoria categoria;

    @ManyToOne(optional = false)
    @JoinColumn(name = "buffet_id")
    private Buffet buffet;
}
