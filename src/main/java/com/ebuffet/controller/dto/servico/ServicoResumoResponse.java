package com.ebuffet.controller.dto.servico;

import com.ebuffet.models.Servico;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServicoResumoResponse {
    private Long id;
    private String nome;
    private String descricao;
    private String imagemUrl;

    public static ServicoResumoResponse of(Servico s) {
        ServicoResumoResponse dto = new ServicoResumoResponse();
        dto.id = s.getId();
        dto.nome = s.getNome();
        dto.descricao = s.getDescricao();
        dto.imagemUrl = (s.getImagem() != null) ? s.getImagem().getUrl() : null;
        return dto;
    }
}
