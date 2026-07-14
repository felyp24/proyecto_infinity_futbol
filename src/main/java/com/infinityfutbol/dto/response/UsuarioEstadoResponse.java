package com.infinityfutbol.dto.response;

import com.infinityfutbol.entity.enums.EstadoUsuario;

public record UsuarioEstadoResponse(

        String idUsuario,
        String username,
        EstadoUsuario estado,
        String mensaje

) {
}