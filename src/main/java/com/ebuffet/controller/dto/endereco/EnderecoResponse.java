package com.ebuffet.controller.dto.endereco;

import com.ebuffet.models.Endereco;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class EnderecoResponse {
    private String rua;
    private String numero;
    private String bairro;
    private String cidade;
    private String estado;
    private String cep;
    private String complemento;

    public static EnderecoResponse of(Endereco e) {
        if (e == null) return null;

        EnderecoResponse dto = new EnderecoResponse();
        dto.rua = e.getRua();
        dto.numero = e.getNumero();
        dto.bairro = e.getBairro();
        dto.cidade = e.getCidade();
        dto.estado = e.getEstado();
        dto.cep = e.getCep();
        dto.complemento = e.getComplemento();
        return dto;
    }
}
