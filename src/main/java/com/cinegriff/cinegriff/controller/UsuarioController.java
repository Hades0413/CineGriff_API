package com.cinegriff.cinegriff.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
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

import com.cinegriff.cinegriff.entity.Usuario;
import com.cinegriff.cinegriff.model.ErrorResponse;
import com.cinegriff.cinegriff.model.LoginRequest;
import com.cinegriff.cinegriff.model.SuccessResponse;
import com.cinegriff.cinegriff.service.UsuarioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/usuario")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // Obtener lista de usuarios
    @GetMapping("/listar")
    public ResponseEntity<List<Usuario>> listarUsuarios() {
        List<Usuario> usuarios = usuarioService.listarUsuarios();
        if (usuarios.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(usuarios);
        }
        return ResponseEntity.ok(usuarios);
    }

    // Obtener lista de usuarios por usernameUsuario
    @GetMapping("/listar/por-username/{usernameUsuario}")
    public ResponseEntity<List<Usuario>> listarUsuariosPorUsernameUsuario(@PathVariable String usernameUsuario) {
        List<Usuario> usuarios = usuarioService.listarUsuariosPorUsernameUsuario(usernameUsuario);
        if (usuarios.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(usuarios);
        }
        return ResponseEntity.ok(usuarios);
    }

    // Obtener un usuario por codigoUsuario
    @GetMapping("/{codigoUsuario}")
    public ResponseEntity<Object> obtenerUsuarioPorCodigoUsuario(@PathVariable int codigoUsuario) {
        Optional<Usuario> usuarioOpt = usuarioService.obtenerUsuarioPorCodigoUsuario(codigoUsuario);

        if (usuarioOpt.isPresent()) {
            return ResponseEntity.ok(usuarioOpt.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("404", "Usuario no encontrado"));
        }
    }

    // Registrar un nuevo usuario
    @PostMapping("/register")
    public ResponseEntity<Object> registrarUsuario(@Valid @RequestBody Usuario usuario) {
        List<String> errores = validarCampos(usuario);
        if (!errores.isEmpty()) {
            return generarError(HttpStatus.BAD_REQUEST, String.join(", ", errores));
        }

        if (usuario.getAdmin() != 1) {
            usuario.setAdmin(0);
        }

        usuarioService.guardarUsuario(usuario);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new SuccessResponse("201", "Usuario registrado exitosamente"));
    }

    // Actualizar un usuario
    @PutMapping("/{codigoUsuario}")
    public ResponseEntity<Object> actualizarUsuario(@PathVariable int codigoUsuario,
            @Valid @RequestBody Usuario usuario) {
        Optional<Usuario> existingUserOpt = usuarioService.obtenerUsuarioPorCodigoUsuario(codigoUsuario);

        if (!existingUserOpt.isPresent()) {
            ErrorResponse errorResponse = new ErrorResponse("404", "Usuario no encontrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

        List<String> errores = validarCampos(usuario);
        if (!errores.isEmpty()) {
            return generarError(HttpStatus.BAD_REQUEST, String.join(", ", errores));
        }

        usuario.setCodigoUsuario(codigoUsuario);

        if (usuario.getAdmin() != 1) {
            usuario.setAdmin(0);
        }

        Usuario usuarioActualizado = usuarioService.actualizarUsuario(usuario);

        return ResponseEntity.ok(usuarioActualizado);
    }

    // Eliminar un usuario
    @DeleteMapping("/{codigoUsuario}")
    public ResponseEntity<Object> eliminarUsuario(@PathVariable int codigoUsuario) {
        Optional<Usuario> usuario = usuarioService.obtenerUsuarioPorCodigoUsuario(codigoUsuario);
        if (usuario.isPresent()) {
            Usuario usuarioToDelete = usuario.get();
            String nombreCompleto = usuarioToDelete.getNombreUsuario() + " " + usuarioToDelete.getApellidoUsuario();

            usuarioService.eliminarUsuario(codigoUsuario);

            SuccessResponse successResponse = new SuccessResponse(
                    "200",
                    String.format("Usuario %s con código %d eliminado con éxito", nombreCompleto, codigoUsuario));

            return ResponseEntity.ok(successResponse);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("404", "Usuario no encontrado"));
        }
    }

    // Login de usuario
    @PostMapping("/login")
    public ResponseEntity<Object> login(@Valid @RequestBody LoginRequest loginRequest) {

        if ((loginRequest.getCorreoUsuario() == null || loginRequest.getCorreoUsuario().isEmpty()) &&
                (loginRequest.getUsernameUsuario() == null || loginRequest.getUsernameUsuario().isEmpty())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("400", "El correo electrónico o el nombre de usuario son obligatorios"));
        }

        if (loginRequest.getContrasenaUsuario() == null || loginRequest.getContrasenaUsuario().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("400", "La contraseña es obligatoria"));
        }

        Usuario usuario = null;

        if (loginRequest.getCorreoUsuario() != null && !loginRequest.getCorreoUsuario().isEmpty()) {
            usuario = usuarioService.authenticateUsuarioByCorreoUsuario(loginRequest.getCorreoUsuario(),
                    loginRequest.getContrasenaUsuario());
        } else if (loginRequest.getUsernameUsuario() != null && !loginRequest.getUsernameUsuario().isEmpty()) {
            usuario = usuarioService.authenticateUsuarioByUsernameUsuario(loginRequest.getUsernameUsuario(),
                    loginRequest.getContrasenaUsuario());
        }

        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("401", "Credenciales incorrectas"));
        }

        // Aquí puedes retornar una respuesta de éxito con código y mensaje
        return ResponseEntity.ok(new SuccessResponse("200", "Login exitoso"));
    }

    // Manejo de excepciones (por ejemplo, JSON mal formado)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("500", "Error interno del servidor: " + ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .reduce("", (msg1, msg2) -> msg1 + ", " + msg2);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("400", "Errores de validación: " + errorMessage));
    }

    // Manejo de errores para JSON mal formado (ejemplo de manejo de excepciones
    // globales)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleJsonParseException(HttpMessageNotReadableException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("400", "El JSON enviado es incorrecto o está mal formado"));
    }

    private List<String> validarCampos(Usuario usuario) {
        List<String> errores = new ArrayList<>();

        // Validar el username
        if (usuario.getUsernameUsuario() == null || usuario.getUsernameUsuario().isEmpty()) {
            errores.add("El username es obligatorio");
        } else if (usuarioService.usernameUsuarioExists(usuario.getUsernameUsuario())) {
            errores.add("El nombre de usuario ya está en uso");
        }

        // Validar el nombre
        if (usuario.getNombreUsuario() == null || usuario.getNombreUsuario().isEmpty()) {
            errores.add("El nombre es obligatorio");
        }

        // Validar el apellido
        if (usuario.getApellidoUsuario() == null || usuario.getApellidoUsuario().isEmpty()) {
            errores.add("El apellido es obligatorio");
        }

        // Validar el correo electrónico
        if (usuario.getCorreoUsuario() == null || usuario.getCorreoUsuario().isEmpty()) {
            errores.add("El correo electrónico es obligatorio");
        } else if (!esFormatoCorreoValido(usuario.getCorreoUsuario())) {
            errores.add("El formato del correo electrónico no es válido");
        } else if (!esDominioPermitido(usuario.getCorreoUsuario())) {
            errores.add("Solo se permiten correos de Gmail, Hotmail, Yahoo, Outlook o iCloud");
        } else if (usuarioService.correoUsuarioExists(usuario.getCorreoUsuario())) {
            errores.add("El correo electrónico ya está en uso");
        }

        // Validar la contraseña
        if (usuario.getContrasenaUsuario() == null || usuario.getContrasenaUsuario().isEmpty()) {
            errores.add("La contraseña es obligatoria");
        } else if (usuario.getContrasenaUsuario().length() < 8 || usuario.getContrasenaUsuario().length() > 255) {
            errores.add("La contraseña debe tener entre 8 y 255 caracteres");
        }

        return errores;
    }

    // Métodos auxiliares
    private boolean esFormatoCorreoValido(String correo) {
        return correo.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    private boolean esDominioPermitido(String correo) {
        List<String> dominiosPermitidos = Arrays.asList("gmail.com", "hotmail.com", "yahoo.com", "outlook.com",
                "icloud.com");
        String dominioCorreo = correo.substring(correo.indexOf('@') + 1);
        return dominiosPermitidos.contains(dominioCorreo);
    }

    private ResponseEntity<Object> generarError(HttpStatus status, String mensaje) {
        return ResponseEntity.status(status).body(new ErrorResponse(String.valueOf(status.value()), mensaje));
    }
}
