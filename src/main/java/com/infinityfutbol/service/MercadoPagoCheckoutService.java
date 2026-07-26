package com.infinityfutbol.service;

import com.infinityfutbol.dto.response.PreferenciaPagoResponse;
import com.infinityfutbol.entity.Pago;
import com.infinityfutbol.entity.PaqueteCredito;
import com.infinityfutbol.repository.PagoRepository;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferencePayerRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.core.MPRequestOptions;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.preference.Preference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.infinityfutbol.entity.enums.EstadoPago;
import java.time.OffsetDateTime;
import java.util.List;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;

@Service
public class MercadoPagoCheckoutService {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(
                    MercadoPagoCheckoutService.class
            );

    private static final String MONEDA = "PEN";

    private final CreditoService creditoService;

    private final PagoRepository pagoRepository;

    private final MercadoPagoConfirmacionService
            mercadoPagoConfirmacionService;

    private final String accessToken;

    private final String appPublicUrl;

    public MercadoPagoCheckoutService(
            CreditoService creditoService,

            PagoRepository pagoRepository,

            MercadoPagoConfirmacionService
                    mercadoPagoConfirmacionService,

            @Value("${mercadopago.access-token:}")
            String accessToken,

            @Value("${app.public-url:}")
            String appPublicUrl
    ) {
        this.creditoService =
                creditoService;

        this.pagoRepository =
                pagoRepository;

        this.mercadoPagoConfirmacionService =
                mercadoPagoConfirmacionService;

        this.accessToken =
                accessToken;

        this.appPublicUrl =
                appPublicUrl;
    }
    @Transactional
    public PreferenciaPagoResponse crearPreferencia(
            String idUsuario,
            String idPaqueteCredito
    ) {
        validarAccessToken();

        /*
         * Se crea el pago local dentro de la misma
         * transacción.
         *
         * Si Mercado Pago rechaza la preferencia,
         * la transacción se revierte.
         */
        Pago pago =
                creditoService.crearPagoPendiente(
                        idUsuario,
                        idPaqueteCredito
                );

        PaqueteCredito paquete =
                pago.getPaqueteCredito();

        PreferenceItemRequest item =
                construirItem(paquete);

        PreferencePayerRequest comprador =
                construirComprador(pago);

        var solicitudBuilder =
                PreferenceRequest.builder()
                        .items(
                                List.of(item)
                        )
                        .payer(comprador)
                        .externalReference(
                                pago.getIdPago()
                        );

        /*
         * Las URLs de retorno solamente se agregan
         * cuando existe una URL pública HTTPS.
         *
         * De esta manera el proyecto puede continuar
         * funcionando localmente sin ngrok.
         */
        if (tieneUrlPublicaHttps()) {

            PreferenceBackUrlsRequest backUrls =
                    PreferenceBackUrlsRequest
                            .builder()
                            .success(
                                    construirUrlPublica(
                                            "/inicio/creditos/retorno/exito"
                                    )
                            )
                            .pending(
                                    construirUrlPublica(
                                            "/inicio/creditos/retorno/pendiente"
                                    )
                            )
                            .failure(
                                    construirUrlPublica(
                                            "/inicio/creditos/retorno/fallo"
                                    )
                            )
                            .build();

            solicitudBuilder
                    .backUrls(backUrls)
                    .autoReturn("approved");
        }

        PreferenceRequest solicitud =
                solicitudBuilder.build();

        try {
            PreferenceClient cliente =
                    new PreferenceClient();

            /*
             * El token se envía únicamente desde
             * el backend.
             */
            MPRequestOptions opciones =
                    MPRequestOptions.builder()
                            .accessToken(accessToken)
                            .build();

            Preference preferencia =
                    cliente.create(
                            solicitud,
                            opciones
                    );

            validarPreferenciaCreada(
                    preferencia
            );

            String urlCheckout =
                    obtenerUrlCheckout(
                            preferencia
                    );

            pago.setIdPreferenciaExterna(
                    preferencia.getId()
            );

            pagoRepository.save(pago);

            return new PreferenciaPagoResponse(
                    pago.getIdPago(),
                    preferencia.getId(),
                    urlCheckout,
                    pago.getEstadoPago()
            );

        } catch (MPApiException exception) {

            registrarErrorApi(exception);

            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Mercado Pago rechazó la creación "
                            + "de la preferencia. Código: "
                            + exception.getStatusCode()
            );

        } catch (MPException exception) {

            LOGGER.error(
                    "No fue posible comunicarse con Mercado Pago",
                    exception
            );

            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "No fue posible comunicarse con Mercado Pago"
            );
        }
    }

    private PreferenceItemRequest construirItem(
            PaqueteCredito paquete
    ) {
        return PreferenceItemRequest.builder()
                .id(
                        paquete.getIdPaqueteCredito()
                )
                .title(
                        paquete.getNombre()
                )
                .description(
                        "Recarga de "
                                + paquete.getCantidadCreditos()
                                + " créditos en Infinity Fútbol"
                )
                .currencyId(MONEDA)
                .quantity(1)
                .unitPrice(
                        paquete.getPrecio()
                )
                .build();
    }

    private PreferencePayerRequest construirComprador(
            Pago pago
    ) {
        return PreferencePayerRequest.builder()
                .name(
                        pago.getAlumno()
                                .getNombres()
                )
                .surname(
                        pago.getAlumno()
                                .getApellidos()
                )
                .email(
                        pago.getAlumno()
                                .getUsuario()
                                .getCorreo()
                )
                .build();
    }

    private void validarAccessToken() {
        if (
                accessToken == null
                        || accessToken.isBlank()
        ) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "El Access Token de Mercado Pago "
                            + "no está configurado"
            );
        }
    }

    private void validarPreferenciaCreada(
            Preference preferencia
    ) {
        if (
                preferencia == null
                        || preferencia.getId() == null
                        || preferencia.getId().isBlank()
        ) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Mercado Pago no devolvió "
                            + "una preferencia válida"
            );
        }
    }

    private String obtenerUrlCheckout(
            Preference preferencia
    ) {
        /*
         * Checkout Pro se prueba con una cuenta
         * compradora y tarjetas de prueba, utilizando
         * la URL principal de la preferencia.
         */
        String urlCheckout =
                preferencia.getInitPoint();

        if (
                urlCheckout != null
                        && !urlCheckout.isBlank()
        ) {
            return urlCheckout;
        }

        /*
         * Se conserva sandboxInitPoint únicamente
         * como respaldo.
         */
        String urlSandbox =
                preferencia.getSandboxInitPoint();

        if (
                urlSandbox == null
                        || urlSandbox.isBlank()
        ) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Mercado Pago no devolvió "
                            + "la URL del checkout"
            );
        }

        return urlSandbox;
    }

    private void registrarErrorApi(
            MPApiException exception
    ) {
        String contenidoRespuesta = null;

        if (exception.getApiResponse() != null) {
            contenidoRespuesta =
                    exception
                            .getApiResponse()
                            .getContent();
        }

        LOGGER.error(
                "Mercado Pago respondió con estado {}. Respuesta: {}",
                exception.getStatusCode(),
                contenidoRespuesta
        );
    }

    public PreferenciaPagoResponse continuarPago(
            String idUsuario,
            String idPago
    ) {
        validarAccessToken();

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

        validarPagoContinuable(pago);

        boolean mercadoPagoYaRegistraOperacion =
                mercadoPagoConfirmacionService
                        .existePagoRegistrado(
                                pago.getIdPago()
                        );

        if (mercadoPagoYaRegistraOperacion) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Mercado Pago ya registra una operación para este pago. Utiliza Verificar pago."
            );
        }

        try {
            PreferenceClient cliente =
                    new PreferenceClient();

            MPRequestOptions opciones =
                    MPRequestOptions.builder()
                            .accessToken(
                                    accessToken
                            )
                            .build();

            Preference preferencia =
                    cliente.get(
                            pago.getIdPreferenciaExterna(),
                            opciones
                    );

            validarPreferenciaRecuperada(
                    pago,
                    preferencia
            );

            String urlCheckout =
                    obtenerUrlCheckout(
                            preferencia
                    );

            return new PreferenciaPagoResponse(
                    pago.getIdPago(),
                    preferencia.getId(),
                    urlCheckout,
                    pago.getEstadoPago()
            );

        } catch (MPApiException exception) {

            registrarErrorApi(exception);

            if (
                    exception.getStatusCode()
                            == 404
            ) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "La preferencia de Mercado Pago ya no existe. Debes iniciar una nueva compra."
                );
            }

            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Mercado Pago rechazó la consulta de la preferencia"
            );

        } catch (MPException exception) {

            LOGGER.error(
                    "No fue posible recuperar la preferencia de Mercado Pago",
                    exception
            );

            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "No fue posible comunicarse con Mercado Pago"
            );
        }
    }

    private void validarPagoContinuable(
            Pago pago
    ) {
        if (
                pago.getEstadoPago()
                        != EstadoPago.PENDIENTE
        ) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Solo se pueden continuar pagos pendientes"
            );
        }

        if (
                pago.getIdPreferenciaExterna()
                        == null

                        || pago.getIdPreferenciaExterna()
                        .isBlank()
        ) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El pago no tiene una preferencia de Mercado Pago asociada"
            );
        }
    }

    private void validarPreferenciaRecuperada(
            Pago pago,
            Preference preferencia
    ) {
        if (
                preferencia == null
                        || preferencia.getId() == null
                        || preferencia.getId().isBlank()
        ) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Mercado Pago no devolvió una preferencia válida"
            );
        }

        if (
                !pago.getIdPreferenciaExterna()
                        .equals(
                                preferencia.getId()
                        )
        ) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "La preferencia recuperada no coincide con el pago"
            );
        }

        if (
                preferencia.getExternalReference()
                        == null

                        || !pago.getIdPago()
                        .equals(
                                preferencia
                                        .getExternalReference()
                        )
        ) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "La referencia de la preferencia no coincide con el pago"
            );
        }

        boolean preferenciaExpirada =
                Boolean.TRUE.equals(
                        preferencia.getExpires()
                )

                        && preferencia
                        .getExpirationDateTo() != null

                        && preferencia
                        .getExpirationDateTo()
                        .isBefore(
                                OffsetDateTime.now()
                        );

        if (preferenciaExpirada) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "La preferencia de pago venció. Debes iniciar una nueva compra."
            );
        }
    }

    private boolean tieneUrlPublicaHttps() {

        if (
                appPublicUrl == null
                        || appPublicUrl.isBlank()
        ) {
            return false;
        }

        return appPublicUrl
                .trim()
                .toLowerCase()
                .startsWith("https://");
    }

    private String construirUrlPublica(
            String ruta
    ) {
        String urlBase =
                appPublicUrl.trim();

        /*
         * Evita formar una dirección con dos barras:
         *
         * https://dominio.com//inicio/creditos
         */
        while (urlBase.endsWith("/")) {
            urlBase =
                    urlBase.substring(
                            0,
                            urlBase.length() - 1
                    );
        }

        return urlBase + ruta;
    }
}