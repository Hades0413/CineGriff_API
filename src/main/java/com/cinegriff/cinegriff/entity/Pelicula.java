package com.cinegriff.cinegriff.entity;

import java.io.Serializable;
import java.sql.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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
@Table(name = "tb_pelicula", uniqueConstraints = {
        @UniqueConstraint(columnNames = "titulo_Pelicula")
})
public class Pelicula implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo_Pelicula", nullable = false, unique = true)
    private int codigoPelicula;

    @NotNull(message = "El título de la película no puede estar vacío")
    @Size(max = 150, message = "El título de la película debe tener como máximo 255 caracteres")
    @Column(name = "titulo_Pelicula", nullable = false, unique = true)
    private String tituloPelicula;

    @NotNull(message = "El banner de la película no puede estar vacío")
    @Size(max = 150, message = "El banner de la película debe tener como máximo 150 caracteres")
    @Column(name = "banner_Pelicula", nullable = false, columnDefinition = "VARCHAR(150) DEFAULT 'default'")
    private String bannerPelicula;

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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    @Column(name = "fechaestreno_Pelicula", nullable = false)
    private Date fechaEstrenoPelicula;

    @Min(value = 1, message = "La clasificación de edad debe ser al menos 1")
    @Column(name = "clasificacionedad_Pelicula", nullable = false)
    private int clasificacionEdad;

    @PrePersist
    public void setDefaultValues() {
        // Si bannerPelicula es null o vacío, se asigna "default"
        if (this.bannerPelicula == null || this.bannerPelicula.trim().isEmpty()) {
            this.bannerPelicula = "default";
        }
    }
}
