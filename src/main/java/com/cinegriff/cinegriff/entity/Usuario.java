package com.cinegriff.cinegriff.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "tb_usuario")
public class Usuario implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo_Usuario", nullable = false, unique = true)
    private int codigoUsuario;

    @NotBlank(message = "El nombre de usuario no puede estar vacío")
    @Size(max = 100, message = "El nombre de usuario debe tener como máximo 100 caracteres")
    @Column(name = "username_Usuario", nullable = false, unique = true)
    private String usernameUsuario;

    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(max = 50, message = "El nombre debe tener como máximo 50 caracteres")
    @Column(name = "nombre_Usuario", nullable = false)
    private String nombreUsuario;

    @NotBlank(message = "El apellido no puede estar vacío")
    @Size(max = 50, message = "El apellido debe tener como máximo 50 caracteres")
    @Column(name = "apellido_Usuario", nullable = false)
    private String apellidoUsuario;

    @NotBlank(message = "El correo no puede estar vacío")
    @Email(message = "Debe proporcionar un correo electrónico válido")
    @Column(name = "correo_Usuario", nullable = false, unique = true)
    private String correoUsuario;

    @NotBlank(message = "La contraseña no puede estar vacía")
    @Size(min = 8, max = 255, message = "La contraseña debe tener entre 8 y 255 caracteres")
    @Column(name = "contrasena_Usuario", nullable = false)
    private String contrasenaUsuario;

    @Column(name = "admin", nullable = false, columnDefinition = "INT DEFAULT 0")
    private int admin;
}
