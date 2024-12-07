package com.cinegriff.cinegriff.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    private String correoUsuario;
    private String usernameUsuario;
    private String contrasenaUsuario;
}
