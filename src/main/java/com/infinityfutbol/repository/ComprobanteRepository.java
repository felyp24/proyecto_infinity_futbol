package com.infinityfutbol.repository;

import com.infinityfutbol.entity.Comprobante;
import com.infinityfutbol.entity.enums.EstadoComprobante;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    /*
     * Listado administrativo de comprobantes emitidos.
     *
     * Permite buscar por:
     * - Serie y número.
     * - ID del comprobante.
     * - ID del pago.
     * - Nombre y apellido.
     * - Documento.
     * - Usuario.
     * - Correo.
     */
    @EntityGraph(attributePaths = {
            "pago",
            "pago.alumno",
            "pago.alumno.usuario",
            "pago.paqueteCredito"
    })
    @Query("""
        SELECT c
        FROM Comprobante c
        WHERE c.estado = :estado
          AND (
                :texto = ''

                OR LOWER(c.idComprobante)
                    LIKE LOWER(CONCAT('%', :texto, '%'))

                OR LOWER(c.serie)
                    LIKE LOWER(CONCAT('%', :texto, '%'))

                OR LOWER(c.numero)
                    LIKE LOWER(CONCAT('%', :texto, '%'))

                OR LOWER(
                    CONCAT(
                        CONCAT(c.serie, '-'),
                        c.numero
                    )
                )
                    LIKE LOWER(CONCAT('%', :texto, '%'))

                OR LOWER(c.pago.idPago)
                    LIKE LOWER(CONCAT('%', :texto, '%'))

                OR LOWER(c.pago.alumno.nombres)
                    LIKE LOWER(CONCAT('%', :texto, '%'))

                OR LOWER(c.pago.alumno.apellidos)
                    LIKE LOWER(CONCAT('%', :texto, '%'))

                OR LOWER(c.pago.alumno.numeroDocumento)
                    LIKE LOWER(CONCAT('%', :texto, '%'))

                OR LOWER(c.pago.alumno.usuario.username)
                    LIKE LOWER(CONCAT('%', :texto, '%'))

                OR LOWER(c.pago.alumno.usuario.correo)
                    LIKE LOWER(CONCAT('%', :texto, '%'))
          )
        ORDER BY c.fechaEmision DESC
        """)
    Page<Comprobante> buscarComprobantesEmitidos(
            @Param("estado")
            EstadoComprobante estado,

            @Param("texto")
            String texto,

            Pageable pageable
    );

    /*
     * Obtiene un comprobante específico junto con
     * todos los datos necesarios para mostrarlo.
     */
    @EntityGraph(attributePaths = {
            "pago",
            "pago.alumno",
            "pago.alumno.usuario",
            "pago.paqueteCredito"
    })
    Optional<Comprobante>
    findByIdComprobanteAndEstado(
            String idComprobante,
            EstadoComprobante estado
    );
}