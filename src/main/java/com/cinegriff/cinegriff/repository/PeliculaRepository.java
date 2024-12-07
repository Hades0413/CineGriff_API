package com.cinegriff.cinegriff.repository;

import com.cinegriff.cinegriff.entity.Pelicula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PeliculaRepository extends JpaRepository<Pelicula, Integer> {
    Pelicula findByTituloPelicula(String tituloPelicula);
}
