package com.infinityfutbol.dto.response;

import com.infinityfutbol.entity.enums.EstadoAlumno;
import com.infinityfutbol.entity.enums.EstadoUsuario;
import com.infinityfutbol.entity.enums.TipoDocumento;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record AlumnoListaResponse(

        String idAlumno,
        String idUsuario,

        String nombres,
        String apellidos,

        TipoDocumento tipoDocumento,
        String numeroDocumento,

        LocalDate fechaNacimiento,
        String telefono,

        String correo,

        EstadoAlumno estadoAlumno,
        EstadoUsuario estadoUsuario,

        LocalDateTime fechaRegistro

) {
}