package com.ebuffet.controller.dto.servico;

import com.ebuffet.models.Servico;
import com.ebuffet.models.enums.EnumCategoria;
import com.ebuffet.models.enums.EnumStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServicoResponse {
    private Long id;
    private String nome;
    private String descricao;
    private String imagemUrl;
    private EnumCategoria categoria;
    private Long buffetId;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
    private EnumStatus status;

    public ServicoResponse(Servico s) {
        this.id = s.getId();
        this.nome = s.getNome();
        this.descricao = s.getDescricao();
        this.imagemUrl = (s.getImagem() != null) ? s.getImagem().getUrl() : null;
        this.categoria = s.getCategoria();
        this.buffetId = s.getBuffet() != null ? s.getBuffet().getId() : null;
        this.dataCriacao = s.getDataCriacao();
        this.dataAtualizacao = s.getDataAtualizacao();
        this.status = s.getStatus();
    }
}
