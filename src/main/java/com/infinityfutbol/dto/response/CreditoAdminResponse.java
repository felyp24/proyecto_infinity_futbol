package com.infinityfutbol.dto.response;

import com.infinityfutbol.entity.enums.EstadoAlumno;
import com.infinityfutbol.entity.enums.EstadoUsuario;
import com.infinityfutbol.entity.enums.TipoDocumento;

import java.time.LocalDateTime;

public record CreditoAdminResponse(

        String idCuentaCredito,
        String idAlumno,
        String idUsuario,

        String nombres,
        String apellidos,
        String nombreCompleto,

        String username,
        String correo,

        TipoDocumento tipoDocumento,
        String numeroDocumento,

        EstadoAlumno estadoAlumno,
        EstadoUsuario estadoUsuario,

        Integer saldoActual,
        LocalDateTime fechaActualizacion

) {
}