package com.infinityfutbol.dto.request;

import com.infinityfutbol.entity.enums.TipoDocumento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record ActualizarPerfilRequest(

        @NotBlank(message = "Los nombres son obligatorios")
        @Size(
                max = 100,
                message = "Los nombres no pueden superar los 100 caracteres"
        )
        String nombres,

        @NotBlank(message = "Los apellidos son obligatorios")
        @Size(
                max = 100,
                message = "Los apellidos no pueden superar los 100 caracteres"
        )
        String apellidos,

        @NotNull(message = "Debe seleccionar un tipo de documento")
        TipoDocumento tipoDocumento,

        @NotBlank(message = "El número de documento es obligatorio")
        @Size(
                max = 20,
                message = "El documento no puede superar los 20 caracteres"
        )
        String numeroDocumento,

        @Past(message = "La fecha de nacimiento debe ser anterior a hoy")
        LocalDate fechaNacimiento,

        @Size(
                max = 20,
                message = "El teléfono no puede superar los 20 caracteres"
        )
        String telefono

) {
}