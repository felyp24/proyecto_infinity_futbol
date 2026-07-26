package com.infinityfutbol.dto.response;

import com.infinityfutbol.entity.enums.EstadoPago;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record PagoClienteResponse(

        String idPago,

        String nombrePaquete,
        Integer cantidadCreditos,

        BigDecimal montoTotal,
        String moneda,
        String metodoPago,

        EstadoPago estadoPago,
        String estadoDetalle,

        LocalDateTime fechaPago,
        LocalDateTime fechaAprobacion,
        LocalDate fechaExpiracion,

        boolean puedeVerificarse,
        boolean acreditado

) {
}