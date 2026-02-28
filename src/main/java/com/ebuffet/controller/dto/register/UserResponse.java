package com.ebuffet.controller.dto.register;

import com.ebuffet.models.User;
import com.ebuffet.models.enums.EnumUserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserResponse {

    private Long id;
    private String nome;
    private String email;
    private String telefone;
    private EnumUserRole roles;
    private String fotoUrl;

    public UserResponse(User user) {
        this.id = user.getId();
        this.nome = user.getNome();
        this.email = user.getEmail();
        this.telefone = user.getTelefone();
        this.roles = user.getRole();
        this.fotoUrl = (user.getFoto() != null) ? user.getFoto().getUrl() : null;
    }
}
