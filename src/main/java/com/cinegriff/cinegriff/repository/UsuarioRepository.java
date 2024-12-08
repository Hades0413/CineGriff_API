package com.cinegriff.cinegriff.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cinegriff.cinegriff.entity.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Usuario findByUsernameUsuario(String usernameUsuario);

    Usuario findByCorreoUsuario(String correoUsuario);

    List<Usuario> findByIsadminUsuario(int isadminUsuario);

    // Método para buscar usuarios por usernameUsuario
    List<Usuario> findByUsernameUsuarioContaining(String usernameUsuario);
}
