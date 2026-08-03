package com.infinityfutbol.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record ReporteIngresoResponse(

        LocalDate fechaInicio,
        LocalDate fechaFin,

        BigDecimal totalIngresos,
        Long cantidadPagos,
        Integer totalCreditosVendidos,
        BigDecimal ticketPromedio,

        String moneda,

        List<IngresoDetalleResponse> ingresos

) {
}