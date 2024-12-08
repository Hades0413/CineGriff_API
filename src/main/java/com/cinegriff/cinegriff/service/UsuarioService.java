package com.cinegriff.cinegriff.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.cinegriff.cinegriff.entity.Usuario;
import com.cinegriff.cinegriff.repository.UsuarioRepository;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Método para listar todos los usuarios
    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    // Método para listar usuarios por usernameUsuario
    public List<Usuario> listarUsuariosPorUsernameUsuario(String usernameUsuario) {
        return usuarioRepository.findByUsernameUsuarioContaining(usernameUsuario);
    }

    public List<Usuario> listarAdministradores() {
        return usuarioRepository.findAll().stream()
                .filter(usuario -> usuario.getIsadminUsuario() == 1)
                .toList();
    }

    // Obtener un usuario por codigoUsuario
    public Optional<Usuario> obtenerUsuarioPorCodigoUsuario(int codigoUsuario) {
        return usuarioRepository.findById(codigoUsuario);
    }

    // Guardar un nuevo usuario
    public Usuario guardarUsuario(Usuario usuario) {
        usuario.setContrasenaUsuario(passwordEncoder.encode(usuario.getContrasenaUsuario()));
        return usuarioRepository.save(usuario);
    }

    // Actualizar un usuario
    public Usuario actualizarUsuario(Usuario usuario) {
        usuario.setContrasenaUsuario(passwordEncoder.encode(usuario.getContrasenaUsuario()));
        return usuarioRepository.save(usuario);
    }

    // Eliminar un usuario por codigoUsuario
    public void eliminarUsuario(int codigoUsuario) {
        usuarioRepository.deleteById(codigoUsuario);
    }

    // Método para autenticar usuario por email
    public Usuario authenticateUsuarioByCorreoUsuario(String correoUsuario, String contrasenaUsuario) {
        Usuario usuario = usuarioRepository.findByCorreoUsuario(correoUsuario);
        if (usuario == null || !passwordEncoder.matches(contrasenaUsuario, usuario.getContrasenaUsuario())) {
            return null;
        }
        return usuario;
    }

    // Método para autenticar usuario por nombre de usuario
    public Usuario authenticateUsuarioByUsernameUsuario(String usernameUsuario, String contrasenaUsuario) {
        Usuario usuario = usuarioRepository.findByUsernameUsuario(usernameUsuario);
        if (usuario == null || !passwordEncoder.matches(contrasenaUsuario, usuario.getContrasenaUsuario())) {
            return null;
        }
        return usuario;
    }

    public boolean usernameUsuarioExists(String usernameUsuario) {
        return usuarioRepository.findByUsernameUsuario(usernameUsuario) != null;
    }

    public boolean correoUsuarioExists(String correoUsuario) {
        return usuarioRepository.findByCorreoUsuario(correoUsuario) != null;
    }

}
