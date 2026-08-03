package com.infinityfutbol.service;

import com.infinityfutbol.dto.response.ComprobanteAdminResponse;
import com.infinityfutbol.entity.Alumno;
import com.infinityfutbol.entity.Comprobante;
import com.infinityfutbol.entity.Pago;
import com.infinityfutbol.entity.Usuario;
import com.infinityfutbol.entity.enums.EstadoComprobante;
import com.infinityfutbol.repository.ComprobanteRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional(readOnly = true)
public class ComprobanteAdminService {

    private final ComprobanteRepository
            comprobanteRepository;

    public ComprobanteAdminService(
            ComprobanteRepository
                    comprobanteRepository
    ) {
        this.comprobanteRepository =
                comprobanteRepository;
    }

    public Page<ComprobanteAdminResponse>
    listarComprobantesEmitidos(
            String texto,
            Pageable pageable
    ) {
        String criterio =
                texto == null
                        ? ""
                        : texto.trim();

        return comprobanteRepository
                .buscarComprobantesEmitidos(
                        EstadoComprobante.EMITIDO,
                        criterio,
                        pageable
                )
                .map(
                        this::convertirResponse
                );
    }

    public ComprobanteAdminResponse
    obtenerComprobante(
            String idComprobante
    ) {
        Comprobante comprobante =
                comprobanteRepository
                        .findByIdComprobanteAndEstado(
                                idComprobante,
                                EstadoComprobante.EMITIDO
                        )
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "No existe el comprobante solicitado"
                                )
                        );

        return convertirResponse(
                comprobante
        );
    }

    private ComprobanteAdminResponse
    convertirResponse(
            Comprobante comprobante
    ) {
        Pago pago =
                comprobante.getPago();

        Alumno alumno =
                pago.getAlumno();

        Usuario usuario =
                alumno.getUsuario();

        String nombreCompleto =
                (
                        alumno.getNombres()
                                + " "
                                + alumno.getApellidos()
                ).trim();

        String numeroCompleto =
                comprobante.getSerie()
                        + "-"
                        + comprobante.getNumero();

        String tipoDocumento =
                alumno.getTipoDocumento() == null
                        ? null
                        : alumno.getTipoDocumento()
                        .name();

        return new ComprobanteAdminResponse(
                comprobante.getIdComprobante(),

                comprobante.getTipoComprobante(),
                comprobante.getSerie(),
                comprobante.getNumero(),
                numeroCompleto,

                comprobante.getFechaEmision(),
                comprobante.getEstado(),

                pago.getIdPago(),
                pago.getIdPagoExterno(),

                alumno.getIdAlumno(),
                nombreCompleto,
                usuario.getUsername(),
                usuario.getCorreo(),

                tipoDocumento,
                alumno.getNumeroDocumento(),

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