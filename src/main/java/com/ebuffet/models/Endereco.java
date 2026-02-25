package com.ebuffet.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Entity
@Getter
@Setter
public class Endereco extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String rua;

    @Column(nullable = false, length = 20)
    private String numero;

    @Column(length = 80)
    private String bairro;

    @Column(nullable = false, length = 80)
    private String cidade;

    @Column(nullable = false, length = 2)
    private String estado;

    @Column(nullable = false, length = 9)
    private String cep;

    @Column(length = 120)
    private String complemento;

    public String toLinhaUnica() {
        StringBuilder sb = new StringBuilder();
        sb.append(rua).append(", ").append(numero);
        if (complemento != null && !complemento.isBlank())
            sb.append(" - ").append(complemento);
        if (bairro != null && !bairro.isBlank())
            sb.append(" - ").append(bairro);
        sb.append(" — ").append(cidade).append("/").append(estado)
                .append(" ").append(cep);
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Endereco)) return false;
        Endereco that = (Endereco) o;
        return id != null && id.equals(that.id);
    }
    @Override
    public int hashCode() { return Objects.hashCode(id); }
}
