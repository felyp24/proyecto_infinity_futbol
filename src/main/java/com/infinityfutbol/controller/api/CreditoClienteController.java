package com.infinityfutbol.controller.api;

import com.infinityfutbol.dto.request.CrearPreferenciaRequest;
import com.infinityfutbol.dto.response.ConfirmacionPagoResponse;
import com.infinityfutbol.dto.response.PagoClienteResponse;
import com.infinityfutbol.dto.response.PaqueteCreditoResponse;
import com.infinityfutbol.dto.response.PreferenciaPagoResponse;
import com.infinityfutbol.security.CustomUserDetails;
import com.infinityfutbol.service.CreditoService;
import com.infinityfutbol.service.MercadoPagoCheckoutService;
import com.infinityfutbol.service.MercadoPagoConfirmacionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.infinityfutbol.dto.response.BoletaResponse;
import com.infinityfutbol.service.ComprobanteService;

import java.util.List;

@RestController
@RequestMapping("/api/inicio/creditos")
@PreAuthorize("hasRole('USUARIO')")
public class CreditoClienteController {

    private final CreditoService creditoService;

    private final MercadoPagoCheckoutService
            mercadoPagoCheckoutService;

    private final MercadoPagoConfirmacionService
            mercadoPagoConfirmacionService;

    private final ComprobanteService
            comprobanteService;

    public CreditoClienteController(
            CreditoService creditoService,

            MercadoPagoCheckoutService
                    mercadoPagoCheckoutService,

            MercadoPagoConfirmacionService
                    mercadoPagoConfirmacionService,
            ComprobanteService comprobanteService
    ) {
        this.creditoService =
                creditoService;

        this.mercadoPagoCheckoutService =
                mercadoPagoCheckoutService;

        this.mercadoPagoConfirmacionService =
                mercadoPagoConfirmacionService;
        this.comprobanteService =
                comprobanteService;
    }

    @GetMapping("/paquetes")
    public List<PaqueteCreditoResponse>
    listarPaquetes() {

        return creditoService
                .listarPaquetesActivos();
    }

    @GetMapping("/pagos")
    public List<PagoClienteResponse> listarPagos(
            @AuthenticationPrincipal
            CustomUserDetails usuarioAutenticado
    ) {
        return creditoService
                .listarPagosCliente(
                        usuarioAutenticado
                                .getIdUsuario()
                );
    }

    @PostMapping("/preferencias")
    @ResponseStatus(HttpStatus.CREATED)
    public PreferenciaPagoResponse crearPreferencia(
            @AuthenticationPrincipal
            CustomUserDetails usuarioAutenticado,

            @Valid
            @RequestBody
            CrearPreferenciaRequest request
    ) {
        return mercadoPagoCheckoutService
                .crearPreferencia(
                        usuarioAutenticado
                                .getIdUsuario(),

                        request
                                .idPaqueteCredito()
                );
    }

    @PostMapping("/pagos/{idPago}/confirmar")
    public ConfirmacionPagoResponse confirmarPago(
            @AuthenticationPrincipal
            CustomUserDetails usuarioAutenticado,

            @PathVariable
            String idPago
    ) {
        return mercadoPagoConfirmacionService
                .confirmarPago(
                        usuarioAutenticado
                                .getIdUsuario(),
                        idPago
                );
    }

    @PostMapping("/pagos/{idPago}/continuar")
    public PreferenciaPagoResponse continuarPago(
            @AuthenticationPrincipal
            CustomUserDetails usuarioAutenticado,

            @PathVariable
            String idPago
    ) {
        return mercadoPagoCheckoutService
                .continuarPago(
                        usuarioAutenticado
                                .getIdUsuario(),
                        idPago
                );
    }

    @GetMapping("/pagos/{idPago}/boleta")
    public BoletaResponse obtenerBoleta(
            @AuthenticationPrincipal
            CustomUserDetails usuarioAutenticado,

            @PathVariable
            String idPago
    ) {
        return comprobanteService
                .obtenerBoletaCliente(
                        usuarioAutenticado
                                .getIdUsuario(),
                        idPago
                );
    }
}