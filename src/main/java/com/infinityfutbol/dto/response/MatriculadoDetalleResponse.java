package com.infinityfutbol.dto.response;

import com.infinityfutbol.entity.enums.EstadoAlumno;
import com.infinityfutbol.entity.enums.EstadoUsuario;
import com.infinityfutbol.entity.enums.TipoDocumento;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record MatriculadoDetalleResponse(

        String idAlumno,
        LocalDateTime fechaRegistro,

        String nombres,
        String apellidos,
        String nombreCompleto,

        TipoDocumento tipoDocumento,
        String numeroDocumento,

        LocalDate fechaNacimiento,
        String telefono,

        String idUsuario,
        String username,
        String correo,

        EstadoAlumno estadoAlumno,
        EstadoUsuario estadoUsuario

) {
}