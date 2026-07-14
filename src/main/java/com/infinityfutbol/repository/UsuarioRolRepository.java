package com.infinityfutbol.repository;

import com.infinityfutbol.entity.UsuarioRol;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UsuarioRolRepository
        extends JpaRepository<UsuarioRol, String> {

    @Override
    @EntityGraph(attributePaths = {"usuario", "rol"})
    List<UsuarioRol> findAll();

    @EntityGraph(attributePaths = "rol")
    List<UsuarioRol> findByUsuario_IdUsuario(String idUsuario);

    boolean existsByUsuario_IdUsuarioAndRol_IdRol(
            String idUsuario,
            String idRol
    );

    void deleteByUsuario_IdUsuario(String idUsuario);
}