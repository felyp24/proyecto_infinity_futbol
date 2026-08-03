package com.infinityfutbol.repository;

import com.infinityfutbol.entity.Sede;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SedeRepository
        extends JpaRepository<Sede, String> {

    List<Sede> findByEstadoTrueOrderByNombreAsc();

}