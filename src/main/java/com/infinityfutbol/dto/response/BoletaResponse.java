package com.infinityfutbol.dto.response;

import com.infinityfutbol.entity.enums.EstadoComprobante;
import com.infinityfutbol.entity.enums.TipoComprobante;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BoletaResponse(

        String idComprobante,

        TipoComprobante tipoComprobante,
        String serie,
        String numero,
        LocalDateTime fechaEmision,
        EstadoComprobante estado,

        String idPago,

        String nombreCliente,
        String tipoDocumento,
        String numeroDocumento,
        String correo,

        String nombrePaquete,
        Integer cantidadCreditos,

        BigDecimal montoTotal,
        String moneda,
        String metodoPago

) {
}