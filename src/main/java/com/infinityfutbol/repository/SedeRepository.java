package com.infinityfutbol.repository;

import com.infinityfutbol.entity.Sede;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SedeRepository
        extends JpaRepository<Sede, String> {
}