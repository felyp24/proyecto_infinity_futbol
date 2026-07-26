package com.infinityfutbol.controller.web;

import com.infinityfutbol.dto.response.ConfirmacionPagoResponse;
import com.infinityfutbol.entity.enums.EstadoPago;
import com.infinityfutbol.security.CustomUserDetails;
import com.infinityfutbol.service.MercadoPagoConfirmacionService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/inicio/creditos")
public class CreditoWebController {

    private final MercadoPagoConfirmacionService
            mercadoPagoConfirmacionService;

    public CreditoWebController(
            MercadoPagoConfirmacionService
                    mercadoPagoConfirmacionService
    ) {
        this.mercadoPagoConfirmacionService =
                mercadoPagoConfirmacionService;
    }

    @GetMapping
    public String mostrarRecargaCreditos() {
        return "inicio/creditos";
    }

    @GetMapping("/retorno/exito")
    public String procesarRetornoExitoso(
            @AuthenticationPrincipal
            CustomUserDetails usuarioAutenticado,

            @RequestParam(
                    name = "external_reference",
                    required = false
            )
            String idPago,

            RedirectAttributes redirectAttributes
    ) {
        if (
                idPago == null
                        || idPago.isBlank()
        ) {
            redirectAttributes.addAttribute(
                    "resultado",
                    "error"
            );

            return "redirect:/inicio/creditos";
        }

        try {
            ConfirmacionPagoResponse resultado =
                    mercadoPagoConfirmacionService
                            .confirmarPago(
                                    usuarioAutenticado
                                            .getIdUsuario(),
                                    idPago
                            );

            if (
                    resultado.estadoPago()
                            == EstadoPago.APROBADO
            ) {
                redirectAttributes.addAttribute(
                        "resultado",
                        "aprobado"
                );
            } else {
                redirectAttributes.addAttribute(
                        "resultado",
                        "pendiente"
                );
            }

        } catch (ResponseStatusException exception) {

            redirectAttributes.addAttribute(
                    "resultado",
                    "error"
            );
        }

        return "redirect:/inicio/creditos";
    }

    @GetMapping("/retorno/pendiente")
    public String procesarRetornoPendiente(
            RedirectAttributes redirectAttributes
    ) {
        redirectAttributes.addAttribute(
                "resultado",
                "pendiente"
        );

        return "redirect:/inicio/creditos";
    }

    @GetMapping("/retorno/fallo")
    public String procesarRetornoFallido(
            RedirectAttributes redirectAttributes
    ) {
        redirectAttributes.addAttribute(
                "resultado",
                "fallido"
        );

        return "redirect:/inicio/creditos";
    }
}