package com.infinityfutbol.dto.response;

import com.infinityfutbol.entity.enums.EstadoComprobante;
import com.infinityfutbol.entity.enums.TipoComprobante;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ComprobanteAdminResponse(

        String idComprobante,

        TipoComprobante tipoComprobante,
        String serie,
        String numero,
        String numeroCompleto,

        LocalDateTime fechaEmision,
        EstadoComprobante estado,

        String idPago,
        String idPagoExterno,

        String idAlumno,
        String nombreCliente,
        String username,
        String correo,

        String tipoDocumento,
        String numeroDocumento,

        String nombrePaquete,
        Integer cantidadCreditos,

        BigDecimal montoTotal,
        String moneda,
        String metodoPago

) {
}