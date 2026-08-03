package com.infinityfutbol.service;

import com.infinityfutbol.dto.response.PaqueteCreditoResponse;
import com.infinityfutbol.entity.Alumno;
import com.infinityfutbol.entity.Pago;
import com.infinityfutbol.entity.PaqueteCredito;
import com.infinityfutbol.entity.enums.EstadoAlumno;
import com.infinityfutbol.entity.enums.EstadoPago;
import com.infinityfutbol.repository.AlumnoRepository;
import com.infinityfutbol.repository.PagoRepository;
import com.infinityfutbol.repository.PaqueteCreditoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.infinityfutbol.dto.response.PagoClienteResponse;
import com.infinityfutbol.entity.MovimientoCredito;
import com.infinityfutbol.entity.enums.TipoMovimientoCredito;
import com.infinityfutbol.repository.MovimientoCreditoRepository;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.Optional;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class CreditoService {

    private static final String MONEDA = "PEN";

    private static final String PROVEEDOR_PAGO =
            "MERCADO_PAGO";

    private static final String METODO_PAGO =
            "CHECKOUT_PRO";

    private final PaqueteCreditoRepository
            paqueteCreditoRepository;


    private final AlumnoRepository alumnoRepository;

    private final PagoRepository pagoRepository;

    private final MovimientoCreditoRepository
            movimientoCreditoRepository;

    private final CuponDescuentoService
            cuponDescuentoService;

    public CreditoService(
            PaqueteCreditoRepository
                    paqueteCreditoRepository,

            AlumnoRepository alumnoRepository,

            PagoRepository pagoRepository,

            MovimientoCreditoRepository
                    movimientoCreditoRepository,
            CuponDescuentoService
                    cuponDescuentoService
    ) {
        this.paqueteCreditoRepository =
                paqueteCreditoRepository;

        this.alumnoRepository =
                alumnoRepository;

        this.pagoRepository =
                pagoRepository;

        this.movimientoCreditoRepository =
                movimientoCreditoRepository;

        this.cuponDescuentoService =
                cuponDescuentoService;
    }

    @Transactional(readOnly = true)
    public List<PaqueteCreditoResponse>
    listarPaquetesActivos() {

        return paqueteCreditoRepository
                .findByEstadoTrueOrderByCantidadCreditosAsc()
                .stream()
                .map(this::convertirPaqueteResponse)
                .toList();
    }

    /*
     * Este método todavía no tendrá un endpoint propio.
     *
     * En el siguiente paso será utilizado por el servicio
     * que creará la preferencia en Mercado Pago.
     */
    @Transactional
    public Pago crearPagoPendiente(
            String idUsuario,
            String idPaqueteCredito,
            String codigoCupon
    ) {
        validarIdentificadores(
                idUsuario,
                idPaqueteCredito
        );

        Alumno alumno =
                buscarAlumnoActivo(idUsuario);

        PaqueteCredito paquete =
                buscarPaqueteActivo(
                        idPaqueteCredito
                );

        validarPrecioPaquete(paquete);

        CuponCalculo calculo =
                cuponDescuentoService
                        .calcularDescuento(
                                codigoCupon,
                                paquete.getPrecio()
                        );

        Pago pago = new Pago();

        pago.setIdPago(
                generarIdPago()
        );

        pago.setAlumno(alumno);

        pago.setPaqueteCredito(
                paquete
        );

        pago.setCupon(
                calculo.cupon()
        );

        pago.setMontoBruto(
                calculo.montoBruto()
        );

        pago.setMontoDescuento(
                calculo.montoDescuento()
        );

        pago.setMontoTotal(
                calculo.montoTotal()
        );

        pago.setMoneda(
                MONEDA
        );

        pago.setMetodoPago(
                METODO_PAGO
        );

        pago.setProveedorPago(
                PROVEEDOR_PAGO
        );

        pago.setIdPreferenciaExterna(
                null
        );

        pago.setIdPagoExterno(
                null
        );

        pago.setEstadoPago(
                EstadoPago.PENDIENTE
        );

        pago.setEstadoDetalle(null);

        return pagoRepository.save(pago);
    }

    @Transactional(readOnly = true)
    public PaqueteCredito buscarPaqueteActivo(
            String idPaqueteCredito
    ) {
        String idLimpio =
                idPaqueteCredito == null
                        ? ""
                        : idPaqueteCredito.trim();

        return paqueteCreditoRepository
                .findByIdPaqueteCreditoAndEstadoTrue(
                        idLimpio
                )
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "El paquete seleccionado no existe o está inactivo"
                        )
                );
    }

    private Alumno buscarAlumnoActivo(
            String idUsuario
    ) {
        Alumno alumno =
                alumnoRepository
                        .findByUsuario_IdUsuario(
                                idUsuario
                        )
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "El usuario no tiene un perfil de alumno"
                                )
                        );

        if (
                alumno.getEstado()
                        != EstadoAlumno.ACTIVO
        ) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El alumno se encuentra inactivo"
            );
        }

        return alumno;
    }

    private void validarIdentificadores(
            String idUsuario,
            String idPaqueteCredito
    ) {
        if (
                idUsuario == null
                        || idUsuario.isBlank()
        ) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "No se pudo identificar al usuario"
            );
        }

        if (
                idPaqueteCredito == null
                        || idPaqueteCredito.isBlank()
        ) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debe seleccionar un paquete de créditos"
            );
        }
    }

    private void validarPrecioPaquete(
            PaqueteCredito paquete
    ) {
        if (
                paquete.getPrecio() == null
                        || paquete.getPrecio()
                        .compareTo(BigDecimal.ZERO)
                        <= 0
        ) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El paquete no tiene un precio válido"
            );
        }

        if (
                paquete.getCantidadCreditos()
                        == null
                        || paquete.getCantidadCreditos()
                        <= 0
        ) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El paquete no tiene una cantidad de créditos válida"
            );
        }

        if (
                paquete.getDiasVigencia()
                        == null
                        || paquete.getDiasVigencia()
                        <= 0
        ) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El paquete no tiene una vigencia válida"
            );
        }
    }

    private PaqueteCreditoResponse
    convertirPaqueteResponse(
            PaqueteCredito paquete
    ) {
        return new PaqueteCreditoResponse(
                paquete.getIdPaqueteCredito(),
                paquete.getNombre(),
                paquete.getCantidadCreditos(),
                paquete.getPrecio(),
                paquete.getDiasVigencia()
        );
    }

    private String generarIdPago() {
        return "PAG-"
                + UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 16)
                .toUpperCase();
    }

    @Transactional(readOnly = true)
    public List<PagoClienteResponse> listarPagosCliente(
            String idUsuario
    ) {
        if (
                idUsuario == null
                        || idUsuario.isBlank()
        ) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "No se pudo identificar al usuario"
            );
        }

        return pagoRepository
                .findByAlumno_Usuario_IdUsuarioOrderByFechaPagoDesc(
                        idUsuario,
                        PageRequest.of(0, 20)
                )
                .stream()
                .map(this::convertirPagoResponse)
                .toList();
    }

    private PagoClienteResponse convertirPagoResponse(
            Pago pago
    ) {
        Optional<MovimientoCredito> recarga =
                movimientoCreditoRepository
                        .findByIdPagoAndTipoMovimiento(
                                pago.getIdPago(),
                                TipoMovimientoCredito.RECARGA
                        );

        boolean acreditado =
                recarga.isPresent();

        LocalDate fechaExpiracion =
                recarga
                        .map(
                                MovimientoCredito::
                                        getFechaExpiracion
                        )
                        .orElse(null);

        boolean puedeVerificarse =
                !acreditado
                        && permiteVerificacion(
                        pago.getEstadoPago()
                );

        return new PagoClienteResponse(
                pago.getIdPago(),

                pago.getPaqueteCredito()
                        .getNombre(),

                pago.getPaqueteCredito()
                        .getCantidadCreditos(),

                pago.getMontoTotal(),
                pago.getMoneda(),
                pago.getMetodoPago(),

                pago.getEstadoPago(),
                pago.getEstadoDetalle(),

                pago.getFechaPago(),
                pago.getFechaAprobacion(),
                fechaExpiracion,

                puedeVerificarse,
                acreditado
        );
    }

    private boolean permiteVerificacion(
            EstadoPago estadoPago
    ) {
        if (estadoPago == null) {
            return true;
        }

        return switch (estadoPago) {
            case PENDIENTE,
                 EN_PROCESO,
                 AUTORIZADO -> true;

            case APROBADO,
                 RECHAZADO,
                 CANCELADO,
                 REEMBOLSADO,
                 CONTRACARGO,
                 EN_MEDIACION -> false;
        };
    }
}