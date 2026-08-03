package com.infinityfutbol.dto.request;

import com.infinityfutbol.entity.enums.EstadoCupon;
import jakarta.validation.constraints.NotNull;

public record CambiarEstadoCuponRequest(

        @NotNull(
                message =
                        "Debe seleccionar el estado del cupón"
        )
        EstadoCupon estado

) {
}