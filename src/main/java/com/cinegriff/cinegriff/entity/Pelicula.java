package com.cinegriff.cinegriff.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "tb_pelicula", uniqueConstraints = {
        @UniqueConstraint(columnNames = "titulo_Pelicula")
})
public class Pelicula implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo_Pelicula", nullable = false, unique = true)
    private int codigoPelicula;

    @NotNull(message = "El título de la película no puede estar vacío")
    @Size(max = 255, message = "El título de la película debe tener como máximo 255 caracteres")
    @Column(name = "titulo_Pelicula", nullable = false, unique = true)
    private String tituloPelicula;

    @NotNull(message = "La descripción de la película no puede estar vacía")
    @Column(name = "descripcion_Pelicula", nullable = false, columnDefinition = "TEXT")
    private String descripcionPelicula;

    @NotNull(message = "La duración de la película no puede estar vacía")
    @Size(max = 20, message = "La duración debe tener como máximo 20 caracteres")
    @Column(name = "duracion_Pelicula", nullable = false)
    private String duracionPelicula;

    @NotNull(message = "El nombre del director no puede estar vacío")
    @Size(max = 255, message = "El nombre del director debe tener como máximo 255 caracteres")
    @Column(name = "director_Pelicula", nullable = false)
    private String directorPelicula;

    @NotNull(message = "El género no puede estar vacío")
    @ManyToOne
    @JoinColumn(name = "codigo_Genero")
    private Genero genero;

    @NotNull(message = "La fecha de estreno no puede estar vacía")
    @Column(name = "fechaestreno_Pelicula", nullable = false)
    private String fechaEstrenoPelicula;

    @Min(value = 1, message = "La clasificación de edad debe ser al menos 1")
    @Column(name = "clasificacionedad_Pelicula", nullable = false)
    private int clasificacionEdad;
}
