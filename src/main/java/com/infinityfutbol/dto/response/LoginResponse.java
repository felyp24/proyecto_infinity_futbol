package com.infinityfutbol.dto.response;

import java.util.List;

public record LoginResponse(

        String idUsuario,
        String username,
        List<String> roles,
        String rutaDestino,
        String mensaje

) {
}