package com.ebuffet.controller.dto.comida;

import com.ebuffet.models.Comida;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ComidaResumoResponse {
    private Long id;
    private String nome;
    private String descricao;
    private String imagemUrl;

    public static ComidaResumoResponse of(Comida c) {
        ComidaResumoResponse dto = new ComidaResumoResponse();
        dto.id = c.getId();
        dto.nome = c.getNome();
        dto.descricao = c.getDescricao();
        dto.imagemUrl = (c.getImagem() != null) ? c.getImagem().getUrl() : null;
        return dto;
    }
}
