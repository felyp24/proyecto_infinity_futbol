package com.infinityfutbol.dto.response;

import com.infinityfutbol.entity.enums.EstadoAlumno;
import com.infinityfutbol.entity.enums.EstadoUsuario;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import com.infinityfutbol.entity.enums.TipoDocumento;

public record PerfilResponse(

        String idUsuario,
        String username,
        String correo,
        EstadoUsuario estadoUsuario,
        LocalDateTime fechaCreacion,
        LocalDateTime ultimoAcceso,

        String idAlumno,
        String nombres,
        String apellidos,
        TipoDocumento tipoDocumento,
        String numeroDocumento,
        LocalDate fechaNacimiento,
        String telefono,
        EstadoAlumno estadoAlumno,

        List<String> roles

) {
}