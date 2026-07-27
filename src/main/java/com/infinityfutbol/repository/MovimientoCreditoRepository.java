package com.infinityfutbol.repository;

import com.infinityfutbol.entity.MovimientoCredito;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import com.infinityfutbol.entity.enums.TipoMovimientoCredito;
import java.util.Optional;
import java.util.List;
import com.infinityfutbol.entity.enums.TipoMovimientoCredito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MovimientoCreditoRepository
        extends JpaRepository<MovimientoCredito, String> {

    @EntityGraph(attributePaths = {
            "cuentaCredito",
            "reserva"
    })
    List<MovimientoCredito>
    findByCuentaCredito_Alumno_Usuario_IdUsuarioOrderByFechaMovimientoDesc(
            String idUsuario
    );

    Optional<MovimientoCredito>
    findByIdPagoAndTipoMovimiento(
            String idPago,
            TipoMovimientoCredito tipoMovimiento
    );

    @EntityGraph(attributePaths = {
            "cuentaCredito",
            "cuentaCredito.alumno",
            "cuentaCredito.alumno.usuario"
    })
    @Query("""
        SELECT mc
        FROM MovimientoCredito mc
        WHERE mc.tipoMovimiento = :tipoMovimiento
          AND (
                :texto = ''
                OR LOWER(mc.cuentaCredito.alumno.nombres)
                    LIKE LOWER(CONCAT('%', :texto, '%'))
                OR LOWER(mc.cuentaCredito.alumno.apellidos)
                    LIKE LOWER(CONCAT('%', :texto, '%'))
                OR LOWER(mc.cuentaCredito.alumno.numeroDocumento)
                    LIKE LOWER(CONCAT('%', :texto, '%'))
                OR LOWER(mc.cuentaCredito.alumno.usuario.username)
                    LIKE LOWER(CONCAT('%', :texto, '%'))
                OR LOWER(mc.descripcion)
                    LIKE LOWER(CONCAT('%', :texto, '%'))
          )
        ORDER BY mc.fechaMovimiento DESC
        """)
    Page<MovimientoCredito> buscarHistorialAjustes(
            @Param("tipoMovimiento")
            TipoMovimientoCredito tipoMovimiento,

            @Param("texto")
            String texto,

            Pageable pageable
    );
}