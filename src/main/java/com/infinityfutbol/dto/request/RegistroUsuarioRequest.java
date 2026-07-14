package com.infinityfutbol.dto.request;

import com.infinityfutbol.entity.enums.TipoDocumento;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record RegistroUsuarioRequest(

        @NotBlank(message = "El nombre de usuario es obligatorio")
        @Size(
                min = 4,
                max = 50,
                message = "El usuario debe tener entre 4 y 50 caracteres"
        )
        String username,

        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "Debe ingresar un correo válido")
        @Size(
                max = 100,
                message = "El correo no puede superar los 100 caracteres"
        )
        String correo,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(
                min = 8,
                max = 100,
                message = "La contraseña debe tener entre 8 y 100 caracteres"
        )
        String password,

        @NotBlank(message = "Debe confirmar la contraseña")
        String confirmarPassword,

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