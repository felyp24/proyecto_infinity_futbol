package com.infinityfutbol.dto.response;

import java.math.BigDecimal;

public record PaqueteCreditoResponse(

        String idPaqueteCredito,
        String nombre,
        Integer cantidadCreditos,
        BigDecimal precio,
        Integer diasVigencia

) {
}