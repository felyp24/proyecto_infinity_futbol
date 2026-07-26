package com.infinityfutbol.repository;

import com.infinityfutbol.entity.PaqueteCredito;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaqueteCreditoRepository
        extends JpaRepository<
        PaqueteCredito,
        String
        > {

    List<PaqueteCredito>
    findByEstadoTrueOrderByCantidadCreditosAsc();

    Optional<PaqueteCredito>
    findByIdPaqueteCreditoAndEstadoTrue(
            String idPaqueteCredito
    );
}