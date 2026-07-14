package com.infinityfutbol.dto.response;

public record CredencialesResponse(

        String idUsuario,
        String username,
        String mensaje,
        boolean requiereNuevoInicioSesion

) {
}