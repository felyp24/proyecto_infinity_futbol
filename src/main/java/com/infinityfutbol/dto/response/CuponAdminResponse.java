package com.infinityfutbol.dto.response;

import com.infinityfutbol.entity.enums.EstadoCupon;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CuponAdminResponse(

        String idCupon,
        String codigo,

        BigDecimal porcentajeDescuento,

        LocalDate fechaInicio,
        LocalDate fechaExpiracion,

        EstadoCupon estado,

        boolean vigente,
        String situacion

) {
}