package com.cinegriff.cinegriff.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cinegriff.cinegriff.entity.Genero;
import com.cinegriff.cinegriff.model.ErrorResponse;
import com.cinegriff.cinegriff.model.SuccessResponse;
import com.cinegriff.cinegriff.service.GeneroService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/genero")
public class GeneroController {

    @Autowired
    private GeneroService generoService;

    // Obtener todos los géneros
    @GetMapping("/listar")
    public List<Genero> getAllGeneros() {
        return generoService.getAllGeneros();
    }

    // Obtener un género por codigoGenero
    @GetMapping("/{codigoGenero}")
    public ResponseEntity<Genero> getGeneroByCodigoGenero(@PathVariable int codigoGenero) {
        Optional<Genero> genero = generoService.getGeneroByCodigoGenero(codigoGenero);
        return genero.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Registrar un nuevo género (crear)
    @PostMapping("/register")
    public ResponseEntity<Object> registerGenero(@Valid @RequestBody Genero genero, BindingResult bindingResult) {
        // Si hay errores de validación
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.toList());
            return ResponseEntity.badRequest().body(new ErrorResponse(400, String.join(", ", errors)));
        }

        if (genero.getNombreGenero() == null || genero.getNombreGenero().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(400, "El nombre del género no puede estar vacío."));
        }

        // Verificar si el género ya existe
        Genero existingGenero = generoService.findByNombreGenero(genero.getNombreGenero());
        if (existingGenero != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(409, "Ya existe un género con este nombre: " + genero.getNombreGenero()));
        }

        Genero nuevoGenero = generoService.saveGenero(genero);
        return ResponseEntity.status(201).body(nuevoGenero);
    }

    // Manejo de errores para JSON mal formado (ejemplo de manejo de excepciones
    // globales)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleJsonParseException(HttpMessageNotReadableException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(400, "El JSON enviado es incorrecto o está mal formado"));
    }

    // Actualizar un género existente
    @PutMapping("/{codigoGenero}")
    public ResponseEntity<Object> updateGenero(@PathVariable int codigoGenero, @Valid @RequestBody Genero genero,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.toList());
            return ResponseEntity.badRequest().body(new ErrorResponse(400, String.join(", ", errors)));
        }

        if (genero.getNombreGenero().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(400, "El nombre del género no puede estar vacío"));
        }

        Genero existingGenero = generoService.findByNombreGenero(genero.getNombreGenero());
        if (existingGenero != null && existingGenero.getCodigoGenero() != codigoGenero) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(409, "Ya existe un género con este nombre: " + genero.getNombreGenero()));
        }

        Optional<Genero> generoOptional = generoService.getGeneroByCodigoGenero(codigoGenero);
        if (generoOptional.isPresent()) {
            Genero generoToUpdate = generoOptional.get();
            generoToUpdate.setNombreGenero(genero.getNombreGenero());
            Genero updatedGenero = generoService.saveGenero(generoToUpdate);
            return ResponseEntity.ok(updatedGenero);
        } else {
            return ResponseEntity.status(404).body(new ErrorResponse(404, "Género no encontrado"));
        }
    }

    // Eliminar un género
    @DeleteMapping("/{codigoGenero}")
    public ResponseEntity<Object> deleteGenero(@PathVariable int codigoGenero) {
        Optional<Genero> genero = generoService.getGeneroByCodigoGenero(codigoGenero);
        if (genero.isPresent()) {
            Genero generoToDelete = genero.get();
            String nombreGenero = generoToDelete.getNombreGenero();

            generoService.deleteGenero(codigoGenero);

            SuccessResponse successResponse = new SuccessResponse(
                    200,
                    String.format("Género %s con código %d eliminado con éxito", nombreGenero, codigoGenero));

            return ResponseEntity.ok(successResponse);
        } else {
            return ResponseEntity.status(404).body(new ErrorResponse(404, "Género no encontrado"));
        }
    }

    // Buscar por nombre de género
    @GetMapping("/nombre/{nombreGenero}")
    public ResponseEntity<Object> getGeneroByNombre(@PathVariable String nombreGenero) {
        Genero genero = generoService.findByNombreGenero(nombreGenero);
        if (genero != null) {
            return ResponseEntity.ok(genero);
        } else {
            return ResponseEntity.status(404).body(new ErrorResponse(404, "Género no encontrado"));
        }
    }
}
