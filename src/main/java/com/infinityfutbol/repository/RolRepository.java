package com.infinityfutbol.repository;

import com.infinityfutbol.entity.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RolRepository extends JpaRepository<Rol, String> {

    Optional<Rol> findByNombreRolIgnoreCase(String nombreRol);

    List<Rol> findByEstadoTrueOrderByNombreRolAsc();
}