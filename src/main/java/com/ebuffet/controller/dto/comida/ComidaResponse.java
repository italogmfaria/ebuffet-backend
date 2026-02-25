package com.ebuffet.controller.dto.comida;

import com.ebuffet.models.Comida;
import com.ebuffet.models.enums.EnumCategoria;
import com.ebuffet.models.enums.EnumStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ComidaResponse {
    private Long id;
    private String nome;
    private String descricao;
    private String imagemUrl;
    private EnumCategoria categoria;
    private Long buffetId;
    private EnumStatus status;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;

    public ComidaResponse(Comida c) {
        this.id = c.getId();
        this.nome = c.getNome();
        this.descricao = c.getDescricao();
        this.imagemUrl = (c.getImagem() != null) ? c.getImagem().getUrl() : null;
        this.categoria = c.getCategoria();
        this.buffetId = c.getBuffet().getId();
        this.status = c.getStatus();
        this.dataCriacao = c.getDataCriacao();
        this.dataAtualizacao = c.getDataAtualizacao();
    }
}
