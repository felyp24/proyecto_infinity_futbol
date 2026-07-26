package com.infinityfutbol.service;

import com.infinityfutbol.dto.response.BoletaResponse;
import com.infinityfutbol.entity.Alumno;
import com.infinityfutbol.entity.Comprobante;
import com.infinityfutbol.entity.Pago;
import com.infinityfutbol.entity.enums.EstadoComprobante;
import com.infinityfutbol.entity.enums.EstadoPago;
import com.infinityfutbol.entity.enums.TipoComprobante;
import com.infinityfutbol.repository.ComprobanteRepository;
import com.infinityfutbol.repository.PagoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class ComprobanteService {

    private static final String SERIE_BOLETA =
            "B001";

    private final ComprobanteRepository
            comprobanteRepository;

    private final PagoRepository pagoRepository;

    public ComprobanteService(
            ComprobanteRepository
                    comprobanteRepository,

            PagoRepository pagoRepository
    ) {
        this.comprobanteRepository =
                comprobanteRepository;

        this.pagoRepository =
                pagoRepository;
    }

    /**
     * Genera el comprobante únicamente si el pago
     * todavía no tiene uno asociado.
     */
    @Transactional
    public Comprobante emitirSiNoExiste(
            Pago pago
    ) {
        return comprobanteRepository
                .findByPago_IdPago(
                        pago.getIdPago()
                )
                .orElseGet(() ->
                        crearComprobante(pago)
                );
    }

    @Transactional
    public BoletaResponse obtenerBoletaCliente(
            String idUsuario,
            String idPago
    ) {
        Pago pago =
                pagoRepository
                        .findByIdPagoAndAlumno_Usuario_IdUsuario(
                                idPago,
                                idUsuario
                        )
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "El pago no existe o no pertenece al usuario"
                                )
                        );

        if (
                pago.getEstadoPago()
                        != EstadoPago.APROBADO
        ) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Solo se puede mostrar la boleta de un pago aprobado"
            );
        }

        /*
         * También permite generar la boleta para pagos
         * aprobados antes de implementar este módulo.
         */
        Comprobante comprobante =
                emitirSiNoExiste(pago);

        return convertirResponse(
                comprobante
        );
    }

    private Comprobante crearComprobante(
            Pago pago
    ) {
        if (
                pago.getEstadoPago()
                        != EstadoPago.APROBADO
        ) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No se puede emitir un comprobante para un pago no aprobado"
            );
        }

        Comprobante comprobante =
                new Comprobante();

        comprobante.setIdComprobante(
                generarIdComprobante()
        );

        comprobante.setPago(pago);

        comprobante.setTipoComprobante(
                TipoComprobante.BOLETA
        );

        comprobante.setSerie(
                SERIE_BOLETA
        );

        comprobante.setNumero(
                generarNumeroComprobante()
        );

        comprobante.setMontoTotal(
                pago.getMontoTotal()
        );

        comprobante.setEstado(
                EstadoComprobante.EMITIDO
        );

        return comprobanteRepository.save(
                comprobante
        );
    }

    /**
     * Para la demostración local genera:
     *
     * B001-00000001
     * B001-00000002
     * B001-00000003
     */
    private synchronized String
    generarNumeroComprobante() {

        long siguienteNumero =
                comprobanteRepository
                        .countBySerie(
                                SERIE_BOLETA
                        )
                        + 1;

        String numero =
                formatearNumero(
                        siguienteNumero
                );

        while (
                comprobanteRepository
                        .existsBySerieAndNumero(
                                SERIE_BOLETA,
                                numero
                        )
        ) {
            siguienteNumero++;

            numero =
                    formatearNumero(
                            siguienteNumero
                    );
        }

        return numero;
    }

    private String formatearNumero(
            long numero
    ) {
        return String.format(
                "%08d",
                numero
        );
    }

    private String generarIdComprobante() {
        return "CMP-"
                + UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 16)
                .toUpperCase();
    }

    private BoletaResponse convertirResponse(
            Comprobante comprobante
    ) {
        Pago pago =
                comprobante.getPago();

        Alumno alumno =
                pago.getAlumno();

        String nombreCompleto =
                (
                        alumno.getNombres()
                                + " "
                                + alumno.getApellidos()
                ).trim();

        return new BoletaResponse(
                comprobante.getIdComprobante(),

                comprobante.getTipoComprobante(),
                comprobante.getSerie(),
                comprobante.getNumero(),
                comprobante.getFechaEmision(),
                comprobante.getEstado(),

                pago.getIdPago(),

                nombreCompleto,
                alumno.getTipoDocumento().name(),
                alumno.getNumeroDocumento(),
                alumno.getUsuario().getCorreo(),

                pago.getPaqueteCredito()
                        .getNombre(),

                pago.getPaqueteCredito()
                        .getCantidadCreditos(),

                comprobante.getMontoTotal(),
                pago.getMoneda(),
                pago.getMetodoPago()
        );
    }
}