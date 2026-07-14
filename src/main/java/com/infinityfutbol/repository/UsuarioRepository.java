package com.infinityfutbol.repository;

import com.infinityfutbol.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository
        extends JpaRepository<Usuario, String> {

    Optional<Usuario> findByUsernameIgnoreCase(
            String username
    );

    boolean existsByUsernameIgnoreCase(
            String username
    );

    boolean existsByCorreoIgnoreCase(
            String correo
    );

    boolean existsByUsernameIgnoreCaseAndIdUsuarioNot(
            String username,
            String idUsuario
    );

    boolean existsByCorreoIgnoreCaseAndIdUsuarioNot(
            String correo,
            String idUsuario
    );
}