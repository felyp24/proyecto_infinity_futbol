package com.infinityfutbol.dto.response;

public record RegistroUsuarioResponse(

        String idUsuario,
        String idAlumno,
        String username,
        String correo,
        String mensaje

) {
}