package com.infinityfutbol.dto.response;

import com.infinityfutbol.entity.enums.EstadoUsuario;

import java.time.LocalDateTime;
import java.util.List;

public record UsuarioRolResponse(

        String idUsuario,
        String username,
        String correo,
        EstadoUsuario estado,
        LocalDateTime fechaCreacion,
        List<String> roles

) {
}