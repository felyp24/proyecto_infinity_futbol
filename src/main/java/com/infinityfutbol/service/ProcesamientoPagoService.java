package com.infinityfutbol.service;

import com.infinityfutbol.dto.response.ConfirmacionPagoResponse;
import com.infinityfutbol.entity.CuentaCredito;
import com.infinityfutbol.entity.MovimientoCredito;
import com.infinityfutbol.entity.Pago;
import com.infinityfutbol.entity.enums.EstadoPago;
import com.infinityfutbol.entity.enums.TipoMovimientoCredito;
import com.infinityfutbol.repository.CuentaCreditoRepository;
import com.infinityfutbol.repository.MovimientoCreditoRepository;
import com.infinityfutbol.repository.PagoRepository;
import com.mercadopago.resources.payment.Payment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProcesamientoPagoService {

    private static final ZoneId ZONA_PERU =
            ZoneId.of("America/Lima");

    private final PagoRepository pagoRepository;

    private final CuentaCreditoRepository
            cuentaCreditoRepository;

    private final MovimientoCreditoRepository
            movimientoCreditoRepository;

    private final ComprobanteService
            comprobanteService;

    public ProcesamientoPagoService(
            PagoRepository pagoRepository,

            CuentaCreditoRepository
                    cuentaCreditoRepository,

            MovimientoCreditoRepository
                    movimientoCreditoRepository,
            ComprobanteService comprobanteService
    ) {
        this.pagoRepository =
                pagoRepository;

        this.cuentaCreditoRepository =
                cuentaCreditoRepository;

        this.movimientoCreditoRepository =
                movimientoCreditoRepository;

        this.comprobanteService =
                comprobanteService;
    }

    @Transactional
    public ConfirmacionPagoResponse procesarPago(
            String idUsuario,
            String idPagoLocal,
            Payment pagoExterno
    ) {
        Pago pago =
                pagoRepository
                        .buscarPagoClienteConBloqueo(
                                idPagoLocal,
                                idUsuario
                        )
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "El pago no existe o no pertenece al usuario"
                                )
                        );

        validarPagoExterno(
                pago,
                pagoExterno
        );

        actualizarDatosDelPago(
                pago,
                pagoExterno
        );

        if (
                pago.getEstadoPago()
                        != EstadoPago.APROBADO
        ) {
            pagoRepository.save(pago);

            return new ConfirmacionPagoResponse(
                    pago.getIdPago(),
                    pago.getIdPagoExterno(),

                    pago.getEstadoPago(),
                    pago.getEstadoDetalle(),

                    0,
                    obtenerSaldoSinBloqueo(pago),
                    null,

                    "El pago todavía no se encuentra aprobado"
            );
        }

        return acreditarCreditos(
                pago,
                pagoExterno
        );
    }

    private ConfirmacionPagoResponse acreditarCreditos(
            Pago pago,
            Payment pagoExterno
    ) {
        CuentaCredito cuentaCredito =
                cuentaCreditoRepository
                        .buscarPorAlumnoConBloqueo(
                                pago.getAlumno()
                                        .getIdAlumno()
                        )
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "El alumno no tiene una cuenta de créditos"
                                )
                        );

        Optional<MovimientoCredito>
                movimientoExistente =
                movimientoCreditoRepository
                        .findByIdPagoAndTipoMovimiento(
                                pago.getIdPago(),
                                TipoMovimientoCredito.RECARGA
                        );

        if (movimientoExistente.isPresent()) {
            pagoRepository.save(pago);

            comprobanteService.emitirSiNoExiste(
                    pago
            );

            return new ConfirmacionPagoResponse(
                    pago.getIdPago(),
                    pago.getIdPagoExterno(),

                    pago.getEstadoPago(),
                    pago.getEstadoDetalle(),

                    0,
                    cuentaCredito.getSaldoActual(),

                    movimientoExistente
                            .get()
                            .getFechaExpiracion(),

                    "El pago ya había sido acreditado anteriormente"
            );
        }

        int cantidadCreditos =
                pago.getPaqueteCredito()
                        .getCantidadCreditos();

        int saldoAnterior =
                cuentaCredito.getSaldoActual() != null
                        ? cuentaCredito.getSaldoActual()
                        : 0;

        cuentaCredito.setSaldoActual(
                saldoAnterior + cantidadCreditos
        );

        LocalDate fechaBase =
                obtenerFechaAprobacion(
                        pagoExterno
                );

        LocalDate fechaExpiracion =
                fechaBase.plusDays(
                        pago.getPaqueteCredito()
                                .getDiasVigencia()
                );

        MovimientoCredito movimiento =
                crearMovimientoRecarga(
                        pago,
                        cuentaCredito,
                        cantidadCreditos,
                        fechaExpiracion
                );

        pagoRepository.save(pago);

        cuentaCreditoRepository.save(
                cuentaCredito
        );

        movimientoCreditoRepository.save(
                movimiento
        );

        comprobanteService.emitirSiNoExiste(
                pago
        );

        return new ConfirmacionPagoResponse(
                pago.getIdPago(),
                pago.getIdPagoExterno(),

                pago.getEstadoPago(),
                pago.getEstadoDetalle(),

                cantidadCreditos,
                cuentaCredito.getSaldoActual(),
                fechaExpiracion,

                "Pago aprobado y créditos acreditados correctamente"
        );
    }

    private void validarPagoExterno(
            Pago pagoLocal,
            Payment pagoExterno
    ) {
        if (
                pagoExterno == null
                        || pagoExterno.getId() == null
        ) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Mercado Pago no devolvió un pago válido"
            );
        }

        if (
                pagoExterno.getExternalReference()
                        == null
                        || !pagoLocal.getIdPago()
                        .equals(
                                pagoExterno
                                        .getExternalReference()
                        )
        ) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "La referencia del pago no coincide"
            );
        }

        if (
                pagoExterno.getCurrencyId()
                        == null
                        || !pagoLocal.getMoneda()
                        .equalsIgnoreCase(
                                pagoExterno
                                        .getCurrencyId()
                        )
        ) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "La moneda del pago no coincide"
            );
        }

        BigDecimal montoExterno =
                pagoExterno.getTransactionAmount();

        if (
                montoExterno == null
                        || pagoLocal.getMontoTotal()
                        .compareTo(montoExterno) != 0
        ) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El monto pagado no coincide con el paquete seleccionado"
            );
        }
    }

    private void actualizarDatosDelPago(
            Pago pago,
            Payment pagoExterno
    ) {
        pago.setIdPagoExterno(
                pagoExterno.getId().toString()
        );

        pago.setEstadoPago(
                convertirEstado(
                        pagoExterno.getStatus()
                )
        );

        pago.setEstadoDetalle(
                pagoExterno.getStatusDetail()
        );

        if (
                pagoExterno.getPaymentMethodId()
                        != null
                        && !pagoExterno
                        .getPaymentMethodId()
                        .isBlank()
        ) {
            pago.setMetodoPago(
                    pagoExterno
                            .getPaymentMethodId()
            );
        }

        if (
                pagoExterno.getDateApproved()
                        != null
        ) {
            pago.setFechaAprobacion(
                    pagoExterno
                            .getDateApproved()
                            .atZoneSameInstant(
                                    ZONA_PERU
                            )
                            .toLocalDateTime()
            );
        }
    }

    private EstadoPago convertirEstado(
            String estadoExterno
    ) {
        if (
                estadoExterno == null
                        || estadoExterno.isBlank()
        ) {
            return EstadoPago.PENDIENTE;
        }

        return switch (
                estadoExterno.toLowerCase()
                ) {
            case "approved" ->
                    EstadoPago.APROBADO;

            case "authorized" ->
                    EstadoPago.AUTORIZADO;

            case "in_process" ->
                    EstadoPago.EN_PROCESO;

            case "rejected" ->
                    EstadoPago.RECHAZADO;

            case "cancelled" ->
                    EstadoPago.CANCELADO;

            case "refunded" ->
                    EstadoPago.REEMBOLSADO;

            case "charged_back" ->
                    EstadoPago.CONTRACARGO;

            case "in_mediation" ->
                    EstadoPago.EN_MEDIACION;

            default ->
                    EstadoPago.PENDIENTE;
        };
    }

    private MovimientoCredito crearMovimientoRecarga(
            Pago pago,
            CuentaCredito cuentaCredito,
            int cantidadCreditos,
            LocalDate fechaExpiracion
    ) {
        MovimientoCredito movimiento =
                new MovimientoCredito();

        movimiento.setIdMovimientoCredito(
                generarIdMovimiento()
        );

        movimiento.setCuentaCredito(
                cuentaCredito
        );

        movimiento.setIdPago(
                pago.getIdPago()
        );

        movimiento.setReserva(null);

        movimiento.setTipoMovimiento(
                TipoMovimientoCredito.RECARGA
        );

        movimiento.setCantidad(
                cantidadCreditos
        );

        movimiento.setFechaExpiracion(
                fechaExpiracion
        );

        movimiento.setDescripcion(
                "Recarga de "
                        + cantidadCreditos
                        + (
                        cantidadCreditos == 1
                                ? " crédito"
                                : " créditos"
                )
                        + " por compra de "
                        + pago.getPaqueteCredito()
                        .getNombre()
        );

        return movimiento;
    }

    private LocalDate obtenerFechaAprobacion(
            Payment pagoExterno
    ) {
        if (
                pagoExterno.getDateApproved()
                        == null
        ) {
            return LocalDate.now(
                    ZONA_PERU
            );
        }

        return pagoExterno
                .getDateApproved()
                .atZoneSameInstant(
                        ZONA_PERU
                )
                .toLocalDate();
    }

    private Integer obtenerSaldoSinBloqueo(
            Pago pago
    ) {
        return cuentaCreditoRepository
                .findByAlumno_IdAlumno(
                        pago.getAlumno()
                                .getIdAlumno()
                )
                .map(CuentaCredito::getSaldoActual)
                .orElse(0);
    }

    private String generarIdMovimiento() {
        return "MOV-"
                + UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 20)
                .toUpperCase();
    }
}