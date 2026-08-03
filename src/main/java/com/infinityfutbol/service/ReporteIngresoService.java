package com.infinityfutbol.service;

import com.infinityfutbol.dto.response.IngresoDetalleResponse;
import com.infinityfutbol.dto.response.ReporteIngresoResponse;
import com.infinityfutbol.entity.Alumno;
import com.infinityfutbol.entity.Pago;
import com.infinityfutbol.entity.enums.EstadoPago;
import com.infinityfutbol.repository.PagoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ReporteIngresoService {

    private static final int MAXIMO_DIAS_REPORTE =
            366;

    private final PagoRepository pagoRepository;

    public ReporteIngresoService(
            PagoRepository pagoRepository
    ) {
        this.pagoRepository =
                pagoRepository;
    }

    public ReporteIngresoResponse generarReporte(
            LocalDate fechaInicio,
            LocalDate fechaFin
    ) {
        validarRangoFechas(
                fechaInicio,
                fechaFin
        );

        LocalDateTime inicio =
                fechaInicio.atStartOfDay();

        LocalDateTime finExclusivo =
                fechaFin
                        .plusDays(1)
                        .atStartOfDay();

        List<Pago> pagos =
                pagoRepository
                        .listarIngresosAprobados(
                                EstadoPago.APROBADO,
                                inicio,
                                finExclusivo
                        );

        List<IngresoDetalleResponse> ingresos =
                pagos.stream()
                        .map(this::convertirDetalle)
                        .toList();

        BigDecimal totalIngresos =
                pagos.stream()
                        .map(Pago::getMontoTotal)
                        .filter(monto ->
                                monto != null
                        )
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );

        int totalCreditos =
                pagos.stream()
                        .map(Pago::getPaqueteCredito)
                        .filter(paquete ->
                                paquete != null
                        )
                        .mapToInt(paquete ->
                                paquete.getCantidadCreditos()
                                        == null
                                        ? 0
                                        : paquete
                                        .getCantidadCreditos()
                        )
                        .sum();

        long cantidadPagos =
                pagos.size();

        BigDecimal ticketPromedio =
                cantidadPagos == 0
                        ? BigDecimal.ZERO
                        : totalIngresos.divide(
                        BigDecimal.valueOf(
                                cantidadPagos
                        ),
                        2,
                        RoundingMode.HALF_UP
                );

        return new ReporteIngresoResponse(
                fechaInicio,
                fechaFin,

                totalIngresos
                        .setScale(
                                2,
                                RoundingMode.HALF_UP
                        ),

                cantidadPagos,
                totalCreditos,

                ticketPromedio,

                obtenerMoneda(pagos),

                ingresos
        );
    }

    private IngresoDetalleResponse convertirDetalle(
            Pago pago
    ) {
        Alumno alumno =
                pago.getAlumno();

        String nombreCompleto =
                (
                        alumno.getNombres()
                                + " "
                                + alumno.getApellidos()
                ).trim();

        String tipoDocumento =
                alumno.getTipoDocumento() == null
                        ? null
                        : alumno.getTipoDocumento()
                        .name();

        return new IngresoDetalleResponse(
                pago.getIdPago(),
                pago.getFechaAprobacion(),

                alumno.getIdAlumno(),
                nombreCompleto,

                tipoDocumento,
                alumno.getNumeroDocumento(),

                pago.getPaqueteCredito()
                        .getNombre(),

                pago.getPaqueteCredito()
                        .getCantidadCreditos(),

                pago.getMontoTotal(),
                pago.getMoneda(),
                pago.getMetodoPago()
        );
    }

    private String obtenerMoneda(
            List<Pago> pagos
    ) {
        return pagos.stream()
                .map(Pago::getMoneda)
                .filter(moneda ->
                        moneda != null
                                && !moneda.isBlank()
                )
                .findFirst()
                .orElse("PEN");
    }

    private void validarRangoFechas(
            LocalDate fechaInicio,
            LocalDate fechaFin
    ) {
        if (
                fechaInicio == null
                        || fechaFin == null
        ) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debe indicar la fecha inicial y la fecha final"
            );
        }

        if (fechaInicio.isAfter(fechaFin)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La fecha inicial no puede ser posterior a la fecha final"
            );
        }

        long cantidadDias =
                ChronoUnit.DAYS.between(
                        fechaInicio,
                        fechaFin
                );

        if (
                cantidadDias
                        > MAXIMO_DIAS_REPORTE
        ) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El periodo del reporte no puede superar los 366 días"
            );
        }
    }
}