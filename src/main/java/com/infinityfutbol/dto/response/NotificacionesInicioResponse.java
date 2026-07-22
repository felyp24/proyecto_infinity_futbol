package com.infinityfutbol.dto.response;

import java.util.List;

public record NotificacionesInicioResponse(

        long noLeidas,
        List<NotificacionResponse> notificaciones

) {
}