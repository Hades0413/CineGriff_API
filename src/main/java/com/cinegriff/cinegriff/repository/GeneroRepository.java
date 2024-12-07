package com.cinegriff.cinegriff.repository;

import com.cinegriff.cinegriff.entity.Genero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GeneroRepository extends JpaRepository<Genero, Integer> {
    Genero findByNombreGenero(String nombreGenero);
}
