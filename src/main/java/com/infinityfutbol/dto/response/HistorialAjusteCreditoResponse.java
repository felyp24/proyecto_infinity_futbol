package com.infinityfutbol.dto.response;

import java.time.LocalDateTime;

public record HistorialAjusteCreditoResponse(

        String idMovimiento,

        String idAlumno,
        String nombreCompleto,
        String username,
        String numeroDocumento,

        Integer cambioCreditos,
        String tipoCambio,

        LocalDateTime fechaMovimiento,
        String descripcion

) {
}