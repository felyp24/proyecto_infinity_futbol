package com.infinityfutbol.dto.response;

public record CanchaOpcionResponse(

        String idCancha,
        Integer numeroCancha,
        String tipoSuperficie,

        String idSede,
        String nombreSede,
        String direccionSede,

        String distrito

) {
}