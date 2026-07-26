package com.infinityfutbol.dto.response;

import com.infinityfutbol.entity.enums.EstadoPago;

public record PreferenciaPagoResponse(

        String idPago,
        String idPreferencia,
        String urlCheckout,
        EstadoPago estadoPago

) {
}