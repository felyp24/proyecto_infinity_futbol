package com.infinityfutbol.service;

import com.infinityfutbol.dto.request.GuardarUtileriaRequest;
import com.infinityfutbol.dto.response.InventarioUtileriaResponse;
import com.infinityfutbol.dto.response.SedeOpcionResponse;
import com.infinityfutbol.dto.response.UtileriaResponse;
import com.infinityfutbol.entity.Sede;
import com.infinityfutbol.entity.Usuario;
import com.infinityfutbol.entity.Utileria;
import com.infinityfutbol.entity.enums.EstadoUtileria;
import com.infinityfutbol.repository.SedeRepository;
import com.infinityfutbol.repository.UsuarioRepository;
import com.infinityfutbol.repository.UtileriaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class UtileriaService {

    private static final String SUFICIENTE =
            "SUFICIENTE";

    private static final String BAJO_STOCK =
            "BAJO_STOCK";

    private static final String AGOTADO =
            "AGOTADO";

    private static final String INACTIVO =
            "INACTIVO";

    private final UtileriaRepository
            utileriaRepository;

    private final SedeRepository
            sedeRepository;

    private final UsuarioRepository
            usuarioRepository;

    public UtileriaService(
            UtileriaRepository utileriaRepository,
            SedeRepository sedeRepository,
            UsuarioRepository usuarioRepository
    ) {
        this.utileriaRepository =
                utileriaRepository;

        this.sedeRepository =
                sedeRepository;

        this.usuarioRepository =
                usuarioRepository;
    }

    public List<SedeOpcionResponse>
    listarSedesActivas() {

        return sedeRepository
                .findByEstadoTrueOrderByNombreAsc()
                .stream()
                .map(sede ->
                        new SedeOpcionResponse(
                                sede.getIdSede(),
                                sede.getNombre(),
                                sede.getDireccion(),

                                sede.getDistrito()
                                        .getNombre()
                        )
                )
                .toList();
    }

    public InventarioUtileriaResponse listarUtileria(
            String idSede,
            String texto,
            String situacion,
            boolean incluirInactivos
    ) {
        String sedeNormalizada =
                normalizarFiltro(idSede);

        String textoNormalizado =
                normalizarFiltro(texto);

        String situacionNormalizada =
                normalizarFiltro(situacion)
                        .toUpperCase(Locale.ROOT);

        EstadoUtileria estadoFiltro =
                incluirInactivos
                        ? null
                        : EstadoUtileria.ACTIVO;

        List<UtileriaResponse> base =
                utileriaRepository
                        .buscarUtileria(
                                sedeNormalizada,
                                textoNormalizado,
                                estadoFiltro
                        )
                        .stream()
                        .map(this::convertirResponse)
                        .toList();

        long itemsActivos =
                base.stream()
                        .filter(item ->
                                item.estado()
                                        == EstadoUtileria.ACTIVO
                        )
                        .count();

        long itemsBajoStock =
                base.stream()
                        .filter(item ->
                                BAJO_STOCK.equals(
                                        item.situacion()
                                )
                        )
                        .count();

        long itemsAgotados =
                base.stream()
                        .filter(item ->
                                AGOTADO.equals(
                                        item.situacion()
                                )
                        )
                        .count();

        int unidadesFaltantes =
                base.stream()
                        .filter(item ->
                                item.estado()
                                        == EstadoUtileria.ACTIVO
                        )
                        .mapToInt(item ->
                                item.cantidadFaltante()
                                        == null
                                        ? 0
                                        : item.cantidadFaltante()
                        )
                        .sum();

        List<UtileriaResponse> resultado =
                situacionNormalizada.isBlank()
                        ? base
                        : base.stream()
                        .filter(item ->
                                situacionNormalizada.equals(
                                        item.situacion()
                                )
                        )
                        .toList();

        return new InventarioUtileriaResponse(
                itemsActivos,
                itemsBajoStock,
                itemsAgotados,
                unidadesFaltantes,
                resultado
        );
    }

    @Transactional
    public UtileriaResponse crear(
            GuardarUtileriaRequest request,
            String idUsuario
    ) {
        Sede sede =
                buscarSedeActiva(
                        request.idSede()
                );

        Usuario usuario =
                buscarUsuario(idUsuario);

        String nombre =
                limpiarTexto(
                        request.nombre()
                );

        validarDuplicado(
                sede.getIdSede(),
                nombre
        );

        Utileria utileria =
                new Utileria();

        utileria.setIdUtileria(
                generarId()
        );

        utileria.setSede(sede);
        utileria.setNombre(nombre);

        utileria.setCategoria(
                normalizarMayusculas(
                        request.categoria()
                )
        );

        utileria.setUnidadMedida(
                normalizarMayusculas(
                        request.unidadMedida()
                )
        );

        utileria.setCantidadActual(
                request.cantidadActual()
        );

        utileria.setStockMinimo(
                request.stockMinimo()
        );

        utileria.setObservacion(
                limpiarTextoOpcional(
                        request.observacion()
                )
        );

        utileria.setEstado(
                EstadoUtileria.ACTIVO
        );

        utileria.setUsuarioRegistro(
                usuario
        );

        utileria.setUsuarioActualizacion(
                usuario
        );

        return convertirResponse(
                utileriaRepository.save(
                        utileria
                )
        );
    }

    @Transactional
    public UtileriaResponse actualizar(
            String idUtileria,
            GuardarUtileriaRequest request,
            String idUsuario
    ) {
        Utileria utileria =
                buscarUtileria(idUtileria);

        Sede sede =
                buscarSedeActiva(
                        request.idSede()
                );

        Usuario usuario =
                buscarUsuario(idUsuario);

        String nombre =
                limpiarTexto(
                        request.nombre()
                );

        boolean duplicado =
                utileriaRepository
                        .existsBySede_IdSedeAndNombreIgnoreCaseAndIdUtileriaNot(
                                sede.getIdSede(),
                                nombre,
                                idUtileria
                        );

        if (duplicado) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ya existe un implemento con ese nombre en la sede"
            );
        }

        utileria.setSede(sede);
        utileria.setNombre(nombre);

        utileria.setCategoria(
                normalizarMayusculas(
                        request.categoria()
                )
        );

        utileria.setUnidadMedida(
                normalizarMayusculas(
                        request.unidadMedida()
                )
        );

        utileria.setCantidadActual(
                request.cantidadActual()
        );

        utileria.setStockMinimo(
                request.stockMinimo()
        );

        utileria.setObservacion(
                limpiarTextoOpcional(
                        request.observacion()
                )
        );

        utileria.setUsuarioActualizacion(
                usuario
        );

        return convertirResponse(utileria);
    }

    @Transactional
    public UtileriaResponse eliminarLogicamente(
            String idUtileria,
            String idUsuario
    ) {
        Utileria utileria =
                buscarUtileria(idUtileria);

        Usuario usuario =
                buscarUsuario(idUsuario);

        utileria.setEstado(
                EstadoUtileria.INACTIVO
        );

        utileria.setUsuarioActualizacion(
                usuario
        );

        return convertirResponse(utileria);
    }

    @Transactional
    public UtileriaResponse restaurar(
            String idUtileria,
            String idUsuario
    ) {
        Utileria utileria =
                buscarUtileria(idUtileria);

        Usuario usuario =
                buscarUsuario(idUsuario);

        utileria.setEstado(
                EstadoUtileria.ACTIVO
        );

        utileria.setUsuarioActualizacion(
                usuario
        );

        return convertirResponse(utileria);
    }

    private Utileria buscarUtileria(
            String idUtileria
    ) {
        return utileriaRepository
                .findById(idUtileria)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "No existe el registro de utilería"
                        )
                );
    }

    private Sede buscarSedeActiva(
            String idSede
    ) {
        Sede sede =
                sedeRepository
                        .findById(idSede)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "No existe la sede seleccionada"
                                )
                        );

        if (!Boolean.TRUE.equals(
                sede.getEstado()
        )) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "La sede seleccionada se encuentra inactiva"
            );
        }

        return sede;
    }

    private Usuario buscarUsuario(
            String idUsuario
    ) {
        return usuarioRepository
                .findById(idUsuario)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "No existe el usuario autenticado"
                        )
                );
    }

    private void validarDuplicado(
            String idSede,
            String nombre
    ) {
        boolean existe =
                utileriaRepository
                        .existsBySede_IdSedeAndNombreIgnoreCase(
                                idSede,
                                nombre
                        );

        if (existe) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ya existe un implemento con ese nombre en la sede"
            );
        }
    }

    private UtileriaResponse convertirResponse(
            Utileria utileria
    ) {
        int cantidad =
                utileria.getCantidadActual() == null
                        ? 0
                        : utileria.getCantidadActual();

        int minimo =
                utileria.getStockMinimo() == null
                        ? 0
                        : utileria.getStockMinimo();

        int faltante =
                Math.max(
                        minimo - cantidad,
                        0
                );

        return new UtileriaResponse(
                utileria.getIdUtileria(),

                utileria.getSede()
                        .getIdSede(),

                utileria.getSede()
                        .getNombre(),

                utileria.getSede()
                        .getDistrito()
                        .getNombre(),

                utileria.getNombre(),
                utileria.getCategoria(),
                utileria.getUnidadMedida(),

                cantidad,
                minimo,
                faltante,

                utileria.getEstado(),
                determinarSituacion(
                        utileria,
                        cantidad,
                        minimo
                ),

                utileria.getObservacion(),

                utileria.getUsuarioRegistro()
                        .getUsername(),

                utileria.getUsuarioActualizacion()
                        .getUsername(),

                utileria.getFechaRegistro(),
                utileria.getFechaActualizacion()
        );
    }

    private String determinarSituacion(
            Utileria utileria,
            int cantidad,
            int minimo
    ) {
        if (
                utileria.getEstado()
                        == EstadoUtileria.INACTIVO
        ) {
            return INACTIVO;
        }

        if (cantidad == 0) {
            return AGOTADO;
        }

        if (cantidad < minimo) {
            return BAJO_STOCK;
        }

        return SUFICIENTE;
    }

    private String generarId() {
        return "UTI-"
                + UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 16)
                .toUpperCase(Locale.ROOT);
    }

    private String normalizarFiltro(
            String valor
    ) {
        return valor == null
                ? ""
                : valor.trim();
    }

    private String limpiarTexto(
            String valor
    ) {
        return valor.trim();
    }

    private String limpiarTextoOpcional(
            String valor
    ) {
        if (
                valor == null
                        || valor.isBlank()
        ) {
            return null;
        }

        return valor.trim();
    }

    private String normalizarMayusculas(
            String valor
    ) {
        return valor
                .trim()
                .toUpperCase(Locale.ROOT);
    }
}