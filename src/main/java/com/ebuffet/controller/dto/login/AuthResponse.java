package com.ebuffet.controller.dto.login;

import com.ebuffet.models.enums.EnumUserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AuthResponse {

    private String token;

    private Long buffetId;

    private EnumUserRole role;

    public AuthResponse(String token) {
        this.token = token;
    }

}
