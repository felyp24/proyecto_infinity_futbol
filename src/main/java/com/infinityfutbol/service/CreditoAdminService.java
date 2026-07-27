package com.infinityfutbol.service;

import com.infinityfutbol.dto.request.ActualizarSaldoCreditoRequest;
import com.infinityfutbol.dto.response.CreditoAdminResponse;
import com.infinityfutbol.entity.Alumno;
import com.infinityfutbol.entity.CuentaCredito;
import com.infinityfutbol.entity.MovimientoCredito;
import com.infinityfutbol.entity.Usuario;
import com.infinityfutbol.entity.enums.TipoMovimientoCredito;
import com.infinityfutbol.repository.CuentaCreditoRepository;
import com.infinityfutbol.repository.MovimientoCreditoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.infinityfutbol.dto.response.HistorialAjusteCreditoResponse;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class CreditoAdminService {

    private final CuentaCreditoRepository
            cuentaCreditoRepository;

    private final MovimientoCreditoRepository
            movimientoCreditoRepository;

    public CreditoAdminService(
            CuentaCreditoRepository
                    cuentaCreditoRepository,

            MovimientoCreditoRepository
                    movimientoCreditoRepository
    ) {
        this.cuentaCreditoRepository =
                cuentaCreditoRepository;

        this.movimientoCreditoRepository =
                movimientoCreditoRepository;
    }

    @Transactional(readOnly = true)
    public Page<CreditoAdminResponse> listarCuentas(
            String texto,
            Pageable pageable
    ) {
        String criterio =
                texto == null
                        ? ""
                        : texto.trim();

        return cuentaCreditoRepository
                .buscarParaAdministrador(
                        criterio,
                        pageable
                )
                .map(this::convertirResponse);
    }

    @Transactional
    public CreditoAdminResponse actualizarSaldo(
            String idAlumno,
            ActualizarSaldoCreditoRequest request,
            String idAdministrador,
            String usernameAdministrador
    ) {
        CuentaCredito cuentaCredito =
                cuentaCreditoRepository
                        .buscarPorAlumnoConBloqueo(
                                idAlumno
                        )
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "El alumno no tiene una cuenta de créditos"
                                )
                        );

        int saldoAnterior =
                cuentaCredito.getSaldoActual() == null
                        ? 0
                        : cuentaCredito.getSaldoActual();

        int nuevoSaldo =
                request.nuevoSaldo();

        if (saldoAnterior == nuevoSaldo) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El nuevo saldo debe ser diferente al saldo actual"
            );
        }

        int diferencia =
                nuevoSaldo - saldoAnterior;

        cuentaCredito.setSaldoActual(
                nuevoSaldo
        );

        cuentaCredito.setFechaActualizacion(
                LocalDateTime.now()
        );

        MovimientoCredito movimiento =
                crearMovimientoAjuste(
                        cuentaCredito,
                        saldoAnterior,
                        nuevoSaldo,
                        diferencia,
                        request.motivo(),
                        idAdministrador,
                        usernameAdministrador
                );

        cuentaCreditoRepository.save(
                cuentaCredito
        );

        movimientoCreditoRepository.save(
                movimiento
        );

        return convertirResponse(
                cuentaCredito
        );
    }

    private MovimientoCredito crearMovimientoAjuste(
            CuentaCredito cuentaCredito,
            int saldoAnterior,
            int nuevoSaldo,
            int diferencia,
            String motivo,
            String idAdministrador,
            String usernameAdministrador
    ) {
        MovimientoCredito movimiento =
                new MovimientoCredito();

        movimiento.setIdMovimientoCredito(
                generarIdMovimiento()
        );

        movimiento.setCuentaCredito(
                cuentaCredito
        );

        movimiento.setIdPago(null);
        movimiento.setReserva(null);

        movimiento.setTipoMovimiento(
                TipoMovimientoCredito.AJUSTE_ADMIN
        );

        /*
         * Ejemplos:
         *
         * 5 -> 8  = +3
         * 8 -> 4  = -4
         */
        movimiento.setCantidad(
                diferencia
        );

        movimiento.setFechaExpiracion(null);

        String descripcion =
                "Ajuste administrativo de créditos. "
                        + "Saldo anterior: "
                        + saldoAnterior
                        + ", nuevo saldo: "
                        + nuevoSaldo
                        + ". Motivo: "
                        + requestTexto(motivo)
                        + ". Responsable: "
                        + requestTexto(
                        usernameAdministrador
                )
                        + " ("
                        + requestTexto(
                        idAdministrador
                )
                        + ").";

        movimiento.setDescripcion(
                limitarTexto(
                        descripcion,
                        255
                )
        );

        return movimiento;
    }

    private CreditoAdminResponse convertirResponse(
            CuentaCredito cuentaCredito
    ) {
        Alumno alumno =
                cuentaCredito.getAlumno();

        Usuario usuario =
                alumno.getUsuario();

        String nombreCompleto =
                (
                        alumno.getNombres()
                                + " "
                                + alumno.getApellidos()
                ).trim();

        return new CreditoAdminResponse(
                cuentaCredito
                        .getIdCuentaCredito(),

                alumno.getIdAlumno(),

                usuario.getIdUsuario(),

                alumno.getNombres(),
                alumno.getApellidos(),
                nombreCompleto,

                usuario.getUsername(),
                usuario.getCorreo(),

                alumno.getTipoDocumento(),
                alumno.getNumeroDocumento(),

                alumno.getEstado(),
                usuario.getEstado(),

                cuentaCredito.getSaldoActual(),
                cuentaCredito
                        .getFechaActualizacion()
        );
    }

    private String generarIdMovimiento() {
        return "MOV-"
                + UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 20)
                .toUpperCase();
    }

    private String limitarTexto(
            String texto,
            int longitudMaxima
    ) {
        if (texto.length() <= longitudMaxima) {
            return texto;
        }

        return texto.substring(
                0,
                longitudMaxima
        );
    }

    private String requestTexto(
            String texto
    ) {
        if (texto == null || texto.isBlank()) {
            return "No especificado";
        }

        return texto.trim();
    }

    @Transactional(readOnly = true)
    public Page<HistorialAjusteCreditoResponse>
    listarHistorialAjustes(
            String texto,
            Pageable pageable
    ) {
        String criterio =
                texto == null
                        ? ""
                        : texto.trim();

        return movimientoCreditoRepository
                .buscarHistorialAjustes(
                        TipoMovimientoCredito.AJUSTE_ADMIN,
                        criterio,
                        pageable
                )
                .map(
                        this::convertirHistorialResponse
                );
    }

    private HistorialAjusteCreditoResponse
    convertirHistorialResponse(
            MovimientoCredito movimiento
    ) {
        CuentaCredito cuentaCredito =
                movimiento.getCuentaCredito();

        Alumno alumno =
                cuentaCredito.getAlumno();

        Usuario usuario =
                alumno.getUsuario();

        int cambio =
                movimiento.getCantidad() == null
                        ? 0
                        : movimiento.getCantidad();

        String tipoCambio;

        if (cambio > 0) {
            tipoCambio = "AUMENTO";
        } else if (cambio < 0) {
            tipoCambio = "REDUCCION";
        } else {
            tipoCambio = "SIN_CAMBIO";
        }

        String nombreCompleto =
                (
                        alumno.getNombres()
                                + " "
                                + alumno.getApellidos()
                ).trim();

        return new HistorialAjusteCreditoResponse(
                movimiento
                        .getIdMovimientoCredito(),

                alumno.getIdAlumno(),
                nombreCompleto,
                usuario.getUsername(),
                alumno.getNumeroDocumento(),

                cambio,
                tipoCambio,

                movimiento.getFechaMovimiento(),
                movimiento.getDescripcion()
        );
    }
}