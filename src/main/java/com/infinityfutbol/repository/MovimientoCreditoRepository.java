package com.infinityfutbol.repository;

import com.infinityfutbol.entity.MovimientoCredito;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

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
}