package com.infinityfutbol.dto.request;

import com.infinityfutbol.entity.enums.EstadoUsuario;
import jakarta.validation.constraints.NotNull;

public record CambiarEstadoUsuarioRequest(

        @NotNull(message = "Debe seleccionar un estado")
        EstadoUsuario estado

) {
}