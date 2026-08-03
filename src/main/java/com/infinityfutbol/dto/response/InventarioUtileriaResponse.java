package com.infinityfutbol.dto.response;

import java.util.List;

public record InventarioUtileriaResponse(

        long itemsActivos,
        long itemsBajoStock,
        long itemsAgotados,
        int unidadesFaltantes,

        List<UtileriaResponse> utileria

) {
}