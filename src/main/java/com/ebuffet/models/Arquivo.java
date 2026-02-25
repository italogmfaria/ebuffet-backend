package com.ebuffet.models;

import com.ebuffet.models.enums.EnumTipoArquivo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Arquivo extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EnumTipoArquivo tipo;

    @Column(nullable = false)
    private String url;
}
