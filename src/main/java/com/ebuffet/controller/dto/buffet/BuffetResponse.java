package com.ebuffet.controller.dto.buffet;

import com.ebuffet.controller.dto.endereco.EnderecoResponse;
import com.ebuffet.models.Buffet;
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
public class BuffetResponse {
    private Long id;
    private String nome;
    private String telefone;
    private String email;
    private EnderecoResponse endereco;
    private EnumStatus status;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
    private Long ownerId;

    public BuffetResponse(Buffet b) {
        this.id = b.getId();
        this.nome = b.getNome();
        this.telefone = b.getTelefone();
        this.email = b.getEmail();
        this.endereco = EnderecoResponse.of(b.getEndereco());
        this.status = b.getStatus();
        this.dataCriacao = b.getDataCriacao();
        this.dataAtualizacao = b.getDataAtualizacao();
        this.ownerId = b.getOwner().getId();
    }
}
