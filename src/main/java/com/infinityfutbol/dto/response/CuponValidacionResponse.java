package com.infinityfutbol.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CuponValidacionResponse(

        String codigo,
        BigDecimal porcentajeDescuento,
        LocalDate fechaExpiracion,
        String mensaje

) {
}