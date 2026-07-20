package com.infinityfutbol.dto.response;

public record ResumenInicioResponse(

        String nombreCompleto,
        Integer saldoCreditos,
        Long reservasProximas

) {
}