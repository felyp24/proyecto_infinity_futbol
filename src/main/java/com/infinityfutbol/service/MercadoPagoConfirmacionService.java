package com.infinityfutbol.service;

import com.infinityfutbol.dto.response.ConfirmacionPagoResponse;
import com.infinityfutbol.entity.Pago;
import com.infinityfutbol.repository.PagoRepository;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.core.MPRequestOptions;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.net.MPResultsResourcesPage;
import com.mercadopago.net.MPSearchRequest;
import com.mercadopago.resources.payment.Payment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
public class MercadoPagoConfirmacionService {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(
                    MercadoPagoConfirmacionService.class
            );

    private final PagoRepository pagoRepository;

    private final ProcesamientoPagoService
            procesamientoPagoService;

    private final String accessToken;

    public MercadoPagoConfirmacionService(
            PagoRepository pagoRepository,

            ProcesamientoPagoService
                    procesamientoPagoService,

            @Value("${mercadopago.access-token:}")
            String accessToken
    ) {
        this.pagoRepository =
                pagoRepository;

        this.procesamientoPagoService =
                procesamientoPagoService;

        this.accessToken =
                accessToken;
    }

    public ConfirmacionPagoResponse confirmarPago(
            String idUsuario,
            String idPago
    ) {
        validarAccessToken();

        Pago pagoLocal =
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
                pagoLocal.getIdPreferenciaExterna()
                        == null
                        || pagoLocal
                        .getIdPreferenciaExterna()
                        .isBlank()
        ) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El pago no tiene una preferencia de Mercado Pago asociada"
            );
        }

        Payment pagoExterno =
                buscarPagoEnMercadoPago(
                        pagoLocal.getIdPago()
                );

        return procesamientoPagoService
                .procesarPago(
                        idUsuario,
                        idPago,
                        pagoExterno
                );
    }

    private Payment buscarPagoEnMercadoPago(
            String referenciaExterna
    ) {
        try {
            PaymentClient cliente =
                    new PaymentClient();

            MPRequestOptions opciones =
                    MPRequestOptions.builder()
                            .accessToken(
                                    accessToken
                            )
                            .build();

            MPSearchRequest solicitud =
                    MPSearchRequest.builder()
                            .limit(20)
                            .offset(0)
                            .filters(
                                    Map.of(
                                            "external_reference",
                                            referenciaExterna
                                    )
                            )
                            .build();

            MPResultsResourcesPage<Payment>
                    resultado =
                    cliente.search(
                            solicitud,
                            opciones
                    );

            List<Payment> pagos =
                    resultado.getResults() != null
                            ? resultado.getResults()
                            : List.of();

            List<Payment> coincidencias =
                    pagos.stream()
                            .filter(pago ->
                                    referenciaExterna.equals(
                                            pago.getExternalReference()
                                    )
                            )
                            .toList();

            if (coincidencias.isEmpty()) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Mercado Pago todavía no registra un pago para esta operación"
                );
            }

            /*
             * Si hubo varios intentos para la misma
             * preferencia, se prioriza uno aprobado.
             */
            return coincidencias
                    .stream()
                    .filter(pago ->
                            "approved".equalsIgnoreCase(
                                    pago.getStatus()
                            )
                    )
                    .max(
                            compararPorActualizacion()
                    )
                    .orElseGet(() ->
                            coincidencias
                                    .stream()
                                    .max(
                                            compararPorActualizacion()
                                    )
                                    .orElseThrow()
                    );

        } catch (MPApiException exception) {

            String contenido = null;

            if (exception.getApiResponse() != null) {
                contenido =
                        exception
                                .getApiResponse()
                                .getContent();
            }

            LOGGER.error(
                    "Error consultando pago en Mercado Pago. "
                            + "Estado: {}. Respuesta: {}",
                    exception.getStatusCode(),
                    contenido
            );

            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Mercado Pago rechazó la consulta del pago"
            );

        } catch (MPException exception) {

            LOGGER.error(
                    "No fue posible consultar el pago en Mercado Pago",
                    exception
            );

            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "No fue posible comunicarse con Mercado Pago"
            );
        }
    }

    private Comparator<Payment>
    compararPorActualizacion() {

        return Comparator.comparing(
                Payment::getDateLastUpdated,
                Comparator.nullsLast(
                        Comparator
                                .<OffsetDateTime>
                                        naturalOrder()
                )
        );
    }

    private void validarAccessToken() {
        if (
                accessToken == null
                        || accessToken.isBlank()
        ) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "El Access Token de Mercado Pago no está configurado"
            );
        }
    }

    public boolean existePagoRegistrado(
            String referenciaExterna
    ) {
        try {
            buscarPagoEnMercadoPago(
                    referenciaExterna
            );

            return true;

        } catch (
                ResponseStatusException exception
        ) {
            boolean noExistePago =
                    exception
                            .getStatusCode()
                            .value() == 409

                            && "Mercado Pago todavía no registra un pago para esta operación"
                            .equals(
                                    exception.getReason()
                            );

            if (noExistePago) {
                return false;
            }

            throw exception;
        }
    }
}