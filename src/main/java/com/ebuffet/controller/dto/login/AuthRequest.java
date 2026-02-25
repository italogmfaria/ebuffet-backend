package com.ebuffet.controller.dto.login;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AuthRequest {

    @NotNull
    private String username;

    @NotNull
    private String password;
}
