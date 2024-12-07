package com.cinegriff.cinegriff.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
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
@Table(name = "tb_genero", uniqueConstraints = {
        @UniqueConstraint(columnNames = "nombreGenero")
})
public class Genero implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo_Genero", nullable = false, unique = true)
    private int codigoGenero;

    @NotBlank(message = "El nombre del género no puede estar vacío")
    @Size(max = 50, message = "El nombre del género debe tener como máximo 50 caracteres")
    @Column(name = "nombre_Genero", nullable = false, unique = true)
    private String nombreGenero;

}
