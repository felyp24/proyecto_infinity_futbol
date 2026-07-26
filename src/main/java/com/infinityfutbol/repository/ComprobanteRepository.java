package com.infinityfutbol.repository;

import com.infinityfutbol.entity.Comprobante;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ComprobanteRepository
        extends JpaRepository<Comprobante, String> {

    @EntityGraph(attributePaths = {
            "pago",
            "pago.alumno",
            "pago.alumno.usuario",
            "pago.paqueteCredito"
    })
    Optional<Comprobante> findByPago_IdPago(
            String idPago
    );

    boolean existsBySerieAndNumero(
            String serie,
            String numero
    );

    long countBySerie(
            String serie
    );
}