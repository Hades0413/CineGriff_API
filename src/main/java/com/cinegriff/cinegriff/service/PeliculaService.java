package com.cinegriff.cinegriff.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.cinegriff.cinegriff.entity.Pelicula;
import com.cinegriff.cinegriff.repository.PeliculaRepository;

@Service
public class PeliculaService {

    @Autowired
    private PeliculaRepository peliculaRepository;

    // Obtener todas las películas
    public List<Pelicula> getAllPeliculas() {
        // Ordenamos las películas por codigo_Pelicula de forma ascendente
        return peliculaRepository.findAll(Sort.by(Sort.Order.asc("codigoPelicula")));
    }

    // Obtener una película por su id
    public Optional<Pelicula> getPeliculaById(int id) {
        return peliculaRepository.findById(id);
    }

    // Guardar o actualizar una película
    public Pelicula savePelicula(Pelicula pelicula) {
        return peliculaRepository.save(pelicula);
    }

    // Eliminar una película por id
    public void deletePelicula(int id) {
        peliculaRepository.deleteById(id);
    }

    // Buscar por título de la película
    public Pelicula findByTitulo(String titulo) {
        return peliculaRepository.findByTituloPelicula(titulo);
    }
}
