package com.infinityfutbol.dto.response;

import com.infinityfutbol.entity.enums.EstadoNotificacion;
import com.infinityfutbol.entity.enums.TipoNotificacion;

import java.time.LocalDateTime;

public record NotificacionResponse(

        String idNotificacion,
        String titulo,
        String mensaje,

        TipoNotificacion tipo,
        EstadoNotificacion estado,

        LocalDateTime fechaEnvio,

        String idReserva,
        String idClase

) {
}