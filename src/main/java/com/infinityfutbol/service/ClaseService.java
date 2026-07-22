package com.infinityfutbol.service;

import com.infinityfutbol.dto.request.CrearClaseRequest;
import com.infinityfutbol.dto.response.CanchaOpcionResponse;
import com.infinityfutbol.dto.response.ClaseResponse;
import com.infinityfutbol.dto.response.EntrenadorOpcionResponse;
import com.infinityfutbol.entity.Cancha;
import com.infinityfutbol.entity.Clase;
import com.infinityfutbol.entity.Entrenador;
import com.infinityfutbol.entity.enums.EstadoCancha;
import com.infinityfutbol.entity.enums.EstadoClase;
import com.infinityfutbol.entity.enums.EstadoEntrenador;
import com.infinityfutbol.repository.CanchaRepository;
import com.infinityfutbol.repository.ClaseRepository;
import com.infinityfutbol.repository.EntrenadorRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.Set;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import com.infinityfutbol.dto.request.ActualizarProgramacionClaseRequest;
import java.time.LocalDateTime;

@Service
public class ClaseService {

    private final ClaseRepository claseRepository;
    private final CanchaRepository canchaRepository;
    private final EntrenadorRepository entrenadorRepository;
    private final NotificacionService notificacionService;
    private static final Set<String> TITULOS_PERMITIDOS =
            Set.of(
                    "Clase de arqueros",
                    "Entrenamiento",
                    "Futbol Total"
            );


    public ClaseService(ClaseRepository claseRepository, CanchaRepository canchaRepository, EntrenadorRepository entrenadorRepository, NotificacionService notificacionService) {
        this.claseRepository = claseRepository;
        this.canchaRepository = canchaRepository;
        this.entrenadorRepository = entrenadorRepository;
        this.notificacionService = notificacionService;
    }

    @Transactional(readOnly = true)
    public List<CanchaOpcionResponse> listarCanchasDisponibles() {
        return canchaRepository
                .findByEstadoAndSede_EstadoTrueOrderBySede_NombreAscNumeroCanchaAsc(
                        EstadoCancha.DISPONIBLE
                )
                .stream()
                .map(this::convertirCanchaOpcion)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<EntrenadorOpcionResponse> listarEntrenadoresActivos() {
        return entrenadorRepository
                .findByEstadoOrderByApellidosAscNombresAsc(
                        EstadoEntrenador.ACTIVO
                )
                .stream()
                .map(this::convertirEntrenadorOpcion)
                .toList();
    }

    @Transactional
    public ClaseResponse crearClase(
            CrearClaseRequest request
    ) {
        validarTitulo(request.titulo());

        validarHorario(request);

        Cancha cancha = buscarCanchaDisponible(
                request.idCancha()
        );

        Entrenador entrenador = buscarEntrenadorActivo(
                request.idEntrenador()
        );

        validarCruceCancha(
                request,
                cancha.getIdCancha()
        );

        validarCruceEntrenador(
                request,
                entrenador.getIdEntrenador()
        );

        Clase clase = new Clase();

        clase.setIdClase(
                generarIdClase()
        );

        clase.setTitulo(
                request.titulo().trim()
        );

        clase.setDescripcion(
                limpiarTextoOpcional(
                        request.descripcion()
                )
        );

        clase.setFechaClase(
                request.fechaClase()
        );

        clase.setHoraInicio(
                request.horaInicio()
        );

        clase.setHoraFin(
                request.horaFin()
        );

        clase.setCupoMaximo(
                request.cupoMaximo()
        );

        clase.setCupoDisponible(
                request.cupoMaximo()
        );

        clase.setEstado(
                EstadoClase.PROGRAMADA
        );

        clase.setCancha(cancha);
        clase.setEntrenador(entrenador);

        Clase claseGuardada =
                claseRepository.save(clase);

        return convertirClaseResponse(
                claseGuardada
        );
    }

    private void validarHorario(
            CrearClaseRequest request
    ) {
        if (!request.horaFin().isAfter(
                request.horaInicio()
        )) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La hora de finalización debe ser posterior a la hora de inicio"
            );
        }

        boolean esHoy =
                request.fechaClase().equals(
                        LocalDate.now()
                );

        boolean horaYaPaso =
                !request.horaInicio().isAfter(
                        LocalTime.now()
                );

        if (esHoy && horaYaPaso) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La hora de inicio debe ser posterior a la hora actual"
            );
        }
    }

    private Cancha buscarCanchaDisponible(
            String idCancha
    ) {
        return canchaRepository
                .findByIdCanchaAndEstado(
                        idCancha,
                        EstadoCancha.DISPONIBLE
                )
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "La cancha seleccionada no existe o no está disponible"
                        )
                );
    }

    private Entrenador buscarEntrenadorActivo(
            String idEntrenador
    ) {
        return entrenadorRepository
                .findByIdEntrenadorAndEstado(
                        idEntrenador,
                        EstadoEntrenador.ACTIVO
                )
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "El entrenador seleccionado no existe o está inactivo"
                        )
                );
    }

    private void validarCruceCancha(
            CrearClaseRequest request,
            String idCancha
    ) {
        boolean existeCruce =
                claseRepository.existeCruceCancha(
                        request.fechaClase(),
                        request.horaInicio(),
                        request.horaFin(),
                        idCancha,
                        EstadoClase.PROGRAMADA
                );

        if (existeCruce) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "La cancha ya tiene una clase programada en ese horario"
            );
        }
    }

    private void validarCruceEntrenador(
            CrearClaseRequest request,
            String idEntrenador
    ) {
        boolean existeCruce =
                claseRepository.existeCruceEntrenador(
                        request.fechaClase(),
                        request.horaInicio(),
                        request.horaFin(),
                        idEntrenador,
                        EstadoClase.PROGRAMADA
                );

        if (existeCruce) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El entrenador ya tiene una clase programada en ese horario"
            );
        }
    }

    private CanchaOpcionResponse convertirCanchaOpcion(
            Cancha cancha
    ) {
        return new CanchaOpcionResponse(
                cancha.getIdCancha(),
                cancha.getNumeroCancha(),
                cancha.getTipoSuperficie(),

                cancha.getSede().getIdSede(),
                cancha.getSede().getNombre(),
                cancha.getSede().getDireccion(),

                cancha.getSede()
                        .getDistrito()
                        .getNombre()
        );
    }

    private EntrenadorOpcionResponse convertirEntrenadorOpcion(
            Entrenador entrenador
    ) {
        return new EntrenadorOpcionResponse(
                entrenador.getIdEntrenador(),
                entrenador.getNombres(),
                entrenador.getApellidos(),
                entrenador.getEspecialidad()
        );
    }

    private ClaseResponse convertirClaseResponse(
            Clase clase
    ) {
        String nombreEntrenador =
                clase.getEntrenador().getNombres()
                        + " "
                        + clase.getEntrenador().getApellidos();

        return new ClaseResponse(
                clase.getIdClase(),
                clase.getTitulo(),
                clase.getDescripcion(),

                clase.getFechaClase(),
                clase.getHoraInicio(),
                clase.getHoraFin(),

                clase.getCupoMaximo(),
                clase.getCupoDisponible(),
                clase.getEstado(),

                clase.getCancha().getIdCancha(),
                clase.getCancha().getNumeroCancha(),
                clase.getCancha()
                        .getSede()
                        .getNombre(),

                clase.getEntrenador()
                        .getIdEntrenador(),
                nombreEntrenador
        );
    }

    private String generarIdClase() {
        return "CLS-"
                + UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 16)
                .toUpperCase();
    }

    private String limpiarTextoOpcional(
            String texto
    ) {
        if (texto == null || texto.isBlank()) {
            return null;
        }

        return texto.trim();
    }

    @Transactional(readOnly = true)
    public List<ClaseResponse> listarClasesProgramadas() {
        return claseRepository
                .findByEstadoAndFechaClaseGreaterThanEqualOrderByFechaClaseAscHoraInicioAsc(
                        EstadoClase.PROGRAMADA,
                        LocalDate.now()
                )
                .stream()
                .map(this::convertirClaseResponse)
                .toList();
    }
    private void validarTitulo(
            String titulo
    ) {
        if (
                titulo == null
                        || !TITULOS_PERMITIDOS.contains(
                        titulo.trim()
                )
        ) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debe seleccionar un tipo de clase válido"
            );
        }
    }

    @Transactional
    public ClaseResponse actualizarProgramacion(
            String idClase,
            ActualizarProgramacionClaseRequest request
    ) {
        Clase clase = claseRepository
                .buscarPorIdConBloqueo(idClase)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "La clase solicitada no existe"
                        )
                );

        validarClaseEditable(clase);

        validarNuevoHorario(
                request.fechaClase(),
                request.horaInicio(),
                request.horaFin()
        );

        Cancha nuevaCancha =
                buscarCanchaDisponible(
                        request.idCancha()
                );

        validarCruceCanchaAlEditar(
                clase,
                request,
                nuevaCancha
        );

        validarCruceEntrenadorAlEditar(
                clase,
                request
        );

        clase.setFechaClase(
                request.fechaClase()
        );

        clase.setHoraInicio(
                request.horaInicio()
        );

        clase.setHoraFin(
                request.horaFin()
        );

        clase.setCancha(
                nuevaCancha
        );

        Clase claseActualizada =
                claseRepository.save(clase);

        notificacionService.reprogramarRecordatoriosClase(
                claseActualizada
        );

        return convertirClaseResponse(
                claseActualizada
        );
    }

    private void validarClaseEditable(
            Clase clase
    ) {
        if (clase.getEstado() != EstadoClase.PROGRAMADA) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Solo se pueden editar clases programadas"
            );
        }

        LocalDateTime inicioActual =
                LocalDateTime.of(
                        clase.getFechaClase(),
                        clase.getHoraInicio()
                );

        if (!inicioActual.isAfter(LocalDateTime.now())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No se puede editar una clase que ya comenzó"
            );
        }
    }

    private void validarNuevoHorario(
            LocalDate fechaClase,
            LocalTime horaInicio,
            LocalTime horaFin
    ) {
        if (!horaFin.isAfter(horaInicio)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La hora de finalización debe ser posterior a la hora de inicio"
            );
        }

        LocalDateTime nuevoInicio =
                LocalDateTime.of(
                        fechaClase,
                        horaInicio
                );

        if (!nuevoInicio.isAfter(LocalDateTime.now())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El nuevo horario debe ser posterior a la fecha y hora actual"
            );
        }
    }

    private void validarCruceCanchaAlEditar(
            Clase clase,
            ActualizarProgramacionClaseRequest request,
            Cancha cancha
    ) {
        boolean existeCruce =
                claseRepository
                        .existeCruceCanchaAlEditar(
                                clase.getIdClase(),
                                request.fechaClase(),
                                request.horaInicio(),
                                request.horaFin(),
                                cancha.getIdCancha(),
                                EstadoClase.PROGRAMADA
                        );

        if (existeCruce) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "La cancha ya tiene otra clase programada en ese horario"
            );
        }
    }

    private void validarCruceEntrenadorAlEditar(
            Clase clase,
            ActualizarProgramacionClaseRequest request
    ) {
        boolean existeCruce =
                claseRepository
                        .existeCruceEntrenadorAlEditar(
                                clase.getIdClase(),
                                request.fechaClase(),
                                request.horaInicio(),
                                request.horaFin(),
                                clase.getEntrenador()
                                        .getIdEntrenador(),
                                EstadoClase.PROGRAMADA
                        );

        if (existeCruce) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El entrenador ya tiene otra clase programada en ese horario"
            );
        }
    }
}