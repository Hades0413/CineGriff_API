package com.cinegriff.cinegriff.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cinegriff.cinegriff.entity.Genero;
import com.cinegriff.cinegriff.repository.GeneroRepository;

@Service
public class GeneroService {

    @Autowired
    private GeneroRepository generoRepository;

    // Obtener todos los géneros
    public List<Genero> getAllGeneros() {
        return generoRepository.findAll();
    }

    // Obtener un género por su codigoGenero
    public Optional<Genero> getGeneroByCodigoGenero(int codigoGenero) {
        return generoRepository.findById(codigoGenero);
    }

    // Guardar o actualizar un género
    public Genero saveGenero(Genero genero) {
        return generoRepository.save(genero);
    }

    // Eliminar un género por codigoGenero
    public void deleteGenero(int codigoGenero) {
        generoRepository.deleteById(codigoGenero);
    }

    // Buscar por nombre de género
    public Genero findByNombreGenero(String nombreGenero) {
        return generoRepository.findByNombreGenero(nombreGenero);
    }
}
