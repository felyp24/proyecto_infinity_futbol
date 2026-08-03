package com.infinityfutbol.service;

import com.infinityfutbol.entity.CuponDescuento;

import java.math.BigDecimal;

public record CuponCalculo(

        CuponDescuento cupon,

        BigDecimal montoBruto,
        BigDecimal montoDescuento,
        BigDecimal montoTotal

) {
}