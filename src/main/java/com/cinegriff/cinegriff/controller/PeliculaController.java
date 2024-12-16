package com.cinegriff.cinegriff.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cinegriff.cinegriff.entity.Pelicula;
import com.cinegriff.cinegriff.model.ErrorResponse;
import com.cinegriff.cinegriff.model.SuccessResponse;
import com.cinegriff.cinegriff.service.PeliculaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/pelicula")
public class PeliculaController {

    @Autowired
    private PeliculaService peliculaService;

    // Obtener todas las películas
    @GetMapping("/listar")
    public List<Pelicula> getAllPeliculas() {
        return peliculaService.getAllPeliculas();
    }

    // Obtener una película por id
    @GetMapping("/{id}")
    public ResponseEntity<Object> getPeliculaById(@PathVariable int id) {
        Optional<Pelicula> pelicula = peliculaService.getPeliculaById(id);

        if (pelicula.isPresent()) {
            return ResponseEntity.ok(pelicula.get());
        } else {
            // Si no se encuentra la película, devolver una respuesta 404 con un mensaje de
            // error
            ErrorResponse errorResponse = new ErrorResponse(404, "No se encontró la película con el ID: " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    // Crear una película
    @PostMapping("/register")
    public ResponseEntity<Object> createPelicula(@RequestBody Pelicula pelicula, BindingResult bindingResult) {
        // Validar campos de la película
        List<String> errores = validarCampos(pelicula);

        if (!errores.isEmpty()) {
            return ResponseEntity.badRequest().body(new ErrorResponse(400, String.join(", ", errores)));
        }

        // Verificar si ya existe una película con el mismo título
        try {
            Optional<Pelicula> existingPelicula = Optional
                    .ofNullable(peliculaService.findByTitulo(pelicula.getTituloPelicula()));
            if (existingPelicula.isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ErrorResponse(409,
                                "El título de la película ya existe: " + pelicula.getTituloPelicula()));
            }

            // Guardar la nueva película y obtener la película guardada
            peliculaService.savePelicula(pelicula);

            // Enviar respuesta con código 201 (Created) y mensaje de éxito
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new SuccessResponse(201, "Película registrada con éxito"));
        } catch (DataIntegrityViolationException e) {
            // Manejar cualquier error de integridad
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse(409, "Error al guardar la película: " + e.getMessage()));
        } catch (Exception e) {
            // Capturamos errores generales
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(500, "Error interno del servidor: " + e.getMessage()));
        }
    }

    // Actualizar una película existente
    @PutMapping("/{id}")
    public ResponseEntity<Object> updatePelicula(@PathVariable int id, @Valid @RequestBody Pelicula pelicula,
            BindingResult bindingResult) {
        // Validar campos de la película
        List<String> errores = validarCampos(pelicula);

        // Si hay errores de validación personalizada, devolverlos en la respuesta
        if (!errores.isEmpty()) {
            return ResponseEntity.badRequest().body(new ErrorResponse(400, String.join(", ", errores)));
        }

        // Validar errores de la anotación @Valid
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.toList());
            return ResponseEntity.badRequest().body(new ErrorResponse(400, String.join(", ", errors)));
        }

        // Buscar la película existente por ID
        Optional<Pelicula> existingPelicula = peliculaService.getPeliculaById(id);
        if (existingPelicula.isPresent()) {
            Pelicula peliculaToUpdate = existingPelicula.get();
            peliculaToUpdate.setTituloPelicula(pelicula.getTituloPelicula());
            peliculaToUpdate.setDirectorPelicula(pelicula.getDirectorPelicula());
            peliculaToUpdate.setGenero(pelicula.getGenero());
            peliculaToUpdate.setFechaEstrenoPelicula(pelicula.getFechaEstrenoPelicula());

            try {
                // Guardar la película actualizada y responder con éxito
                peliculaService.savePelicula(peliculaToUpdate);

                // Responder con mensaje de éxito al actualizar la película
                SuccessResponse successResponse = new SuccessResponse(200, "Película actualizada con éxito");

                // Enviar la respuesta con el código 200 (OK) y el mensaje de éxito
                return ResponseEntity.status(HttpStatus.OK).body(successResponse);
            } catch (DataIntegrityViolationException e) {
                // Manejar la excepción de título duplicado
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ErrorResponse(409,
                                "El título de la película ya existe: " + pelicula.getTituloPelicula()));
            }
        } else {
            // Respuesta personalizada si no se encuentra la película
            ErrorResponse errorResponse = new ErrorResponse(404, "No se encontró la película con ID " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse); // Código 404 y mensaje de error
        }
    }

    // Eliminar una película
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deletePelicula(@PathVariable int id) {
        Optional<Pelicula> pelicula = peliculaService.getPeliculaById(id);
        if (pelicula.isPresent()) {
            Pelicula peliculaToDelete = pelicula.get();
            String tituloPelicula = peliculaToDelete.getTituloPelicula();

            peliculaService.deletePelicula(id);

            // Respuesta de éxito personalizada
            SuccessResponse successResponse = new SuccessResponse(
                    200,
                    String.format("Película '%s' con ID %d eliminada con éxito", tituloPelicula, id));

            return ResponseEntity.ok(successResponse);
        } else {
            // Respuesta de error personalizada si no se encuentra la película
            return ResponseEntity.status(404)
                    .body(new ErrorResponse(400, String.format("Película con ID %d no encontrada", id)));
        }
    }

    // Buscar por título de película
    @GetMapping("/titulo/{titulo}")
    public ResponseEntity<Object> getPeliculaByTitulo(@PathVariable String titulo) {
        Pelicula pelicula = peliculaService.findByTitulo(titulo);

        if (pelicula != null) {
            // Película encontrada, retornamos la película
            return ResponseEntity.ok(pelicula);
        } else {
            // Si la película no se encuentra, enviamos un mensaje de error personalizado
            ErrorResponse errorResponse = new ErrorResponse(404, "No se encontró la película con el título: " + titulo);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        String mensajeError = "Error de integridad de datos: " + ex.getMostSpecificCause().getMessage();
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(409, mensajeError));
    }

    // Manejo de errores para JSON mal formado (ejemplo de manejo de excepciones
    // globales)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleJsonParseException(HttpMessageNotReadableException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(400, "El JSON enviado es incorrecto o está mal formado"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .reduce("", (msg1, msg2) -> msg1 + ", " + msg2);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(400, "Errores de validación: " + errorMessage));
    }

    private List<String> validarCampos(Pelicula pelicula) {
        List<String> errores = new ArrayList<>();

        // Validar el título de la película
        if (pelicula.getTituloPelicula() == null || pelicula.getTituloPelicula().isEmpty()) {
            errores.add("El título de la película es obligatorio");
        }

        // Validar la descripción de la película
        if (pelicula.getDescripcionPelicula() == null || pelicula.getDescripcionPelicula().isEmpty()) {
            errores.add("La descripción de la película es obligatoria");
        }

        // Validar la duración de la película
        if (pelicula.getDuracionPelicula() == null || pelicula.getDuracionPelicula().isEmpty()) {
            errores.add("La duración de la película es obligatoria");
        }

        // Validar el nombre del director
        if (pelicula.getDirectorPelicula() == null || pelicula.getDirectorPelicula().isEmpty()) {
            errores.add("El nombre del director es obligatorio");
        }

        // Validar el género
        if (pelicula.getGenero() == null || pelicula.getGenero().getNombreGenero().isEmpty()) {
            errores.add("El género de la película es obligatorio");
        }

        // Validar la fecha de estreno
        if (pelicula.getFechaEstrenoPelicula() == null) {
            errores.add("La fecha de estreno de la película es obligatoria");
        }

        // Validar la clasificación de edad
        if (pelicula.getClasificacionEdad() < 1) {
            errores.add("La clasificación de edad es obligatoria y debe ser al menos 1");
        }

        return errores;
    }

}
