package com.infinityfutbol.service;

import com.infinityfutbol.dto.request.CambiarEstadoCuponRequest;
import com.infinityfutbol.dto.request.CrearCuponRequest;
import com.infinityfutbol.dto.response.CuponAdminResponse;
import com.infinityfutbol.dto.response.CuponValidacionResponse;
import com.infinityfutbol.entity.CuponDescuento;
import com.infinityfutbol.entity.enums.EstadoCupon;
import com.infinityfutbol.repository.CuponDescuentoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class CuponDescuentoService {

    private static final ZoneId ZONA_PERU =
            ZoneId.of("America/Lima");

    private final CuponDescuentoRepository
            cuponDescuentoRepository;

    public CuponDescuentoService(
            CuponDescuentoRepository
                    cuponDescuentoRepository
    ) {
        this.cuponDescuentoRepository =
                cuponDescuentoRepository;
    }

    @Transactional
    public CuponAdminResponse crearCupon(
            CrearCuponRequest request
    ) {
        validarFechas(
                request.fechaInicio(),
                request.fechaExpiracion()
        );

        String codigo =
                normalizarCodigo(
                        request.codigo()
                );

        if (codigo.isBlank()) {
            codigo = generarCodigoDisponible();
        } else {
            validarFormatoCodigo(codigo);
            validarCodigoDisponible(codigo);
        }

        CuponDescuento cupon =
                new CuponDescuento();

        cupon.setIdCupon(
                generarIdCupon()
        );

        cupon.setCodigo(codigo);

        cupon.setPorcentajeDescuento(
                request.porcentajeDescuento()
                        .setScale(
                                2,
                                RoundingMode.HALF_UP
                        )
        );

        cupon.setFechaInicio(
                request.fechaInicio()
        );

        cupon.setFechaExpiracion(
                request.fechaExpiracion()
        );

        cupon.setEstado(
                EstadoCupon.ACTIVO
        );

        CuponDescuento guardado =
                cuponDescuentoRepository.save(
                        cupon
                );

        return convertirAdminResponse(
                guardado
        );
    }

    public List<CuponAdminResponse> listarCupones(
            String texto
    ) {
        String criterio =
                texto == null
                        ? ""
                        : texto.trim();

        return cuponDescuentoRepository
                .buscarCupones(criterio)
                .stream()
                .map(this::convertirAdminResponse)
                .toList();
    }

    @Transactional
    public CuponAdminResponse cambiarEstado(
            String idCupon,
            CambiarEstadoCuponRequest request
    ) {
        CuponDescuento cupon =
                buscarPorId(idCupon);

        if (
                request.estado()
                        == EstadoCupon.ACTIVO

                        && cupon.getFechaExpiracion()
                        .isBefore(
                                LocalDate.now(ZONA_PERU)
                        )
        ) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No se puede activar un cupón vencido"
            );
        }

        cupon.setEstado(
                request.estado()
        );

        return convertirAdminResponse(
                cupon
        );
    }

    public CuponValidacionResponse validarCuponCliente(
            String codigo
    ) {
        CuponDescuento cupon =
                buscarCuponVigente(codigo);

        return new CuponValidacionResponse(
                cupon.getCodigo(),
                cupon.getPorcentajeDescuento(),
                cupon.getFechaExpiracion(),

                "Cupón válido: "
                        + cupon.getPorcentajeDescuento()
                        .stripTrailingZeros()
                        .toPlainString()
                        + "% de descuento"
        );
    }

    public CuponCalculo calcularDescuento(
            String codigo,
            BigDecimal monto
    ) {
        BigDecimal montoBruto =
                normalizarMonto(monto);

        String codigoNormalizado =
                normalizarCodigo(codigo);

        if (codigoNormalizado.isBlank()) {
            return new CuponCalculo(
                    null,
                    montoBruto,
                    BigDecimal.ZERO
                            .setScale(2),
                    montoBruto
            );
        }

        CuponDescuento cupon =
                buscarCuponVigente(
                        codigoNormalizado
                );

        BigDecimal montoDescuento =
                montoBruto
                        .multiply(
                                cupon.getPorcentajeDescuento()
                        )
                        .divide(
                                BigDecimal.valueOf(100),
                                2,
                                RoundingMode.HALF_UP
                        );

        BigDecimal montoTotal =
                montoBruto
                        .subtract(
                                montoDescuento
                        )
                        .setScale(
                                2,
                                RoundingMode.HALF_UP
                        );

        if (
                montoTotal.compareTo(
                        BigDecimal.ZERO
                ) <= 0
        ) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El cupón genera un monto de pago inválido"
            );
        }

        return new CuponCalculo(
                cupon,
                montoBruto,
                montoDescuento,
                montoTotal
        );
    }

    private CuponDescuento buscarCuponVigente(
            String codigo
    ) {
        String codigoNormalizado =
                normalizarCodigo(codigo);

        if (codigoNormalizado.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debe ingresar un código de descuento"
            );
        }

        CuponDescuento cupon =
                cuponDescuentoRepository
                        .findByCodigoIgnoreCase(
                                codigoNormalizado
                        )
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "El código de descuento no existe"
                                )
                        );

        LocalDate hoy =
                LocalDate.now(ZONA_PERU);

        if (
                cupon.getEstado()
                        != EstadoCupon.ACTIVO
        ) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El cupón se encuentra inactivo"
            );
        }

        if (
                hoy.isBefore(
                        cupon.getFechaInicio()
                )
        ) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El cupón todavía no se encuentra vigente"
            );
        }

        if (
                hoy.isAfter(
                        cupon.getFechaExpiracion()
                )
        ) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El cupón ha vencido"
            );
        }

        return cupon;
    }

    private CuponDescuento buscarPorId(
            String idCupon
    ) {
        return cuponDescuentoRepository
                .findById(idCupon)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "No existe el cupón solicitado"
                        )
                );
    }

    private CuponAdminResponse convertirAdminResponse(
            CuponDescuento cupon
    ) {
        String situacion =
                determinarSituacion(cupon);

        return new CuponAdminResponse(
                cupon.getIdCupon(),
                cupon.getCodigo(),

                cupon.getPorcentajeDescuento(),

                cupon.getFechaInicio(),
                cupon.getFechaExpiracion(),

                cupon.getEstado(),

                "VIGENTE".equals(situacion),
                situacion
        );
    }

    private String determinarSituacion(
            CuponDescuento cupon
    ) {
        LocalDate hoy =
                LocalDate.now(ZONA_PERU);

        if (
                cupon.getEstado()
                        == EstadoCupon.INACTIVO
        ) {
            return "INACTIVO";
        }

        if (
                hoy.isBefore(
                        cupon.getFechaInicio()
                )
        ) {
            return "PROXIMAMENTE";
        }

        if (
                hoy.isAfter(
                        cupon.getFechaExpiracion()
                )
        ) {
            return "VENCIDO";
        }

        return "VIGENTE";
    }

    private void validarFechas(
            LocalDate fechaInicio,
            LocalDate fechaExpiracion
    ) {
        if (
                fechaInicio.isAfter(
                        fechaExpiracion
                )
        ) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La fecha inicial no puede ser posterior a la fecha de expiración"
            );
        }

        if (
                fechaExpiracion.isBefore(
                        LocalDate.now(ZONA_PERU)
                )
        ) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La fecha de expiración no puede estar en el pasado"
            );
        }
    }

    private void validarCodigoDisponible(
            String codigo
    ) {
        if (
                cuponDescuentoRepository
                        .existsByCodigoIgnoreCase(
                                codigo
                        )
        ) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El código del cupón ya existe"
            );
        }
    }

    private void validarFormatoCodigo(
            String codigo
    ) {
        if (
                !codigo.matches(
                        "^[A-Z0-9-]{4,50}$"
                )
        ) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El código solo puede contener letras, números y guiones"
            );
        }
    }

    private String generarCodigoDisponible() {
        for (int intento = 0; intento < 10; intento++) {

            String codigo =
                    "INF-"
                            + UUID.randomUUID()
                            .toString()
                            .replace("-", "")
                            .substring(0, 8)
                            .toUpperCase(
                                    Locale.ROOT
                            );

            if (
                    !cuponDescuentoRepository
                            .existsByCodigoIgnoreCase(
                                    codigo
                            )
            ) {
                return codigo;
            }
        }

        throw new IllegalStateException(
                "No se pudo generar un código único"
        );
    }

    private String generarIdCupon() {
        return "CUP-"
                + UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 16)
                .toUpperCase(
                        Locale.ROOT
                );
    }

    private String normalizarCodigo(
            String codigo
    ) {
        if (
                codigo == null
                        || codigo.isBlank()
        ) {
            return "";
        }

        return codigo
                .trim()
                .toUpperCase(
                        Locale.ROOT
                );
    }

    private BigDecimal normalizarMonto(
            BigDecimal monto
    ) {
        if (
                monto == null
                        || monto.compareTo(
                        BigDecimal.ZERO
                ) <= 0
        ) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El paquete no tiene un precio válido"
            );
        }

        return monto.setScale(
                2,
                RoundingMode.HALF_UP
        );
    }
}