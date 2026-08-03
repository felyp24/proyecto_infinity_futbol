package com.infinityfutbol.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record IngresoDetalleResponse(

        String idPago,
        LocalDateTime fechaAprobacion,

        String idAlumno,
        String nombreCliente,

        String tipoDocumento,
        String numeroDocumento,

        String nombrePaquete,
        Integer cantidadCreditos,

        BigDecimal montoTotal,
        String moneda,
        String metodoPago

) {
}