package com.infinityfutbol.dto.response;

import com.infinityfutbol.entity.enums.EstadoPago;

import java.time.LocalDate;

public record ConfirmacionPagoResponse(

        String idPago,
        String idPagoExterno,

        EstadoPago estadoPago,
        String estadoDetalle,

        Integer creditosAcreditados,
        Integer saldoActual,
        LocalDate fechaExpiracion,

        String mensaje

) {
}