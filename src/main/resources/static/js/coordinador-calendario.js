document.addEventListener(
    "DOMContentLoaded",
    iniciarCalendarioClases
);

let modalDetalleClase = null;

async function iniciarCalendarioClases() {

    const elementoCalendario =
        document.getElementById(
            "calendarioClases"
        );

    modalDetalleClase =
        new bootstrap.Modal(
            document.getElementById(
                "modalDetalleClase"
            )
        );

    const calendario =
        new FullCalendar.Calendar(
            elementoCalendario,
            {
                locale: "es",

                initialView:
                    "dayGridMonth",

                firstDay: 1,

                height: "auto",

                navLinks: true,

                nowIndicator: true,

                displayEventTime: true,

                eventTimeFormat: {
                    hour: "2-digit",
                    minute: "2-digit",
                    hour12: false
                },

                headerToolbar: {
                    left:
                        "prev,next today",

                    center:
                        "title",

                    right:
                        "dayGridMonth,timeGridWeek,listMonth"
                },

                buttonText: {
                    today: "Hoy",
                    month: "Mes",
                    week: "Semana",
                    list: "Lista"
                },

                events:
                    cargarClasesCalendario,

                eventClick:
                    mostrarDetalleClase,

                eventDidMount:
                    agregarTituloEvento
            }
        );

    calendario.render();
}

async function cargarClasesCalendario(
    informacion,
    successCallback,
    failureCallback
) {

    ocultarMensajeCalendario();

    try {

        /*
         * Este endpoint ya permite el acceso
         * de COORDINADOR y ADMINISTRADOR.
         */
        const response = await fetch(
            "/api/coordinador/clases",
            {
                method: "GET",
                credentials: "same-origin",
                cache: "no-store",

                headers: {
                    "Accept":
                        "application/json"
                }
            }
        );

        if (!response.ok) {

            throw new Error(
                await obtenerMensajeError(
                    response
                )
            );
        }

        const clases =
            await response.json();

        const eventos =
            Array.isArray(clases)
                ? clases.map(
                    convertirClaseEvento
                )
                : [];

        successCallback(
            eventos
        );

    } catch (error) {

        console.error(
            "Error al cargar el calendario:",
            error
        );

        mostrarMensajeCalendario(
            error.message
        );

        failureCallback(
            error
        );
    }
}

function convertirClaseEvento(
    clase
) {

    const horaInicio =
        normalizarHora(
            clase.horaInicio
        );

    const horaFin =
        normalizarHora(
            clase.horaFin
        );

    return {
        id:
            clase.idClase,

        title:
            clase.titulo,

        start:
            `${clase.fechaClase}T${horaInicio}`,

        end:
            `${clase.fechaClase}T${horaFin}`,

        classNames: [
            obtenerClaseDisponibilidad(
                clase
            )
        ],

        extendedProps: {
            descripcion:
                clase.descripcion,

            fechaClase:
                clase.fechaClase,

            horaInicio:
                clase.horaInicio,

            horaFin:
                clase.horaFin,

            cupoMaximo:
                clase.cupoMaximo,

            cupoDisponible:
                clase.cupoDisponible,

            estado:
                clase.estado,

            numeroCancha:
                clase.numeroCancha,

            nombreSede:
                clase.nombreSede,

            nombreEntrenador:
                clase.nombreEntrenador
        }
    };
}

function obtenerClaseDisponibilidad(
    clase
) {

    const disponibles =
        Number(
            clase.cupoDisponible
        ) || 0;

    const maximo =
        Number(
            clase.cupoMaximo
        ) || 0;

    if (disponibles <= 0) {
        return "evento-sin-cupos";
    }

    /*
     * Se considera disponibilidad limitada
     * cuando queda un 25 % o menos.
     */
    if (
        maximo > 0
        && disponibles <= Math.ceil(
            maximo * 0.25
        )
    ) {
        return "evento-limitado";
    }

    return "evento-disponible";
}

function mostrarDetalleClase(
    informacion
) {

    const evento =
        informacion.event;

    const datos =
        evento.extendedProps;

    document
        .getElementById(
            "tituloDetalleClase"
        )
        .textContent =
        evento.title
        ?? "Detalles de la clase";

    document
        .getElementById(
            "descripcionDetalleClase"
        )
        .textContent =
        datos.descripcion
        || "Sin descripción.";

    document
        .getElementById(
            "fechaDetalleClase"
        )
        .textContent =
        formatearFecha(
            datos.fechaClase
        );

    document
        .getElementById(
            "horarioDetalleClase"
        )
        .textContent =
        `${formatearHora(
            datos.horaInicio
        )} - ${formatearHora(
            datos.horaFin
        )}`;

    document
        .getElementById(
            "entrenadorDetalleClase"
        )
        .textContent =
        datos.nombreEntrenador
        ?? "-";

    document
        .getElementById(
            "sedeDetalleClase"
        )
        .textContent =
        datos.nombreSede
        ?? "-";

    document
        .getElementById(
            "canchaDetalleClase"
        )
        .textContent =
        datos.numeroCancha
            ? `Cancha ${datos.numeroCancha}`
            : "-";

    document
        .getElementById(
            "cuposDetalleClase"
        )
        .textContent =
        `${datos.cupoDisponible ?? 0} `
        + `de ${datos.cupoMaximo ?? 0}`;

    const estado =
        document.getElementById(
            "estadoDetalleClase"
        );

    estado.textContent =
        formatearEstado(
            datos.estado
        );

    estado.className =
        "badge mb-2 "
        + obtenerClaseBadge(
            datos.cupoDisponible
        );

    modalDetalleClase.show();
}

function agregarTituloEvento(
    informacion
) {

    const datos =
        informacion.event
            .extendedProps;

    informacion.el.title =
        `${informacion.event.title} | `
        + `${datos.nombreSede ?? "-"} | `
        + `${datos.cupoDisponible ?? 0} cupos`;
}

function obtenerClaseBadge(
    cuposDisponibles
) {

    return Number(
        cuposDisponibles
    ) > 0
        ? "text-bg-success"
        : "text-bg-danger";
}

function formatearEstado(
    estado
) {

    const estados = {
        PROGRAMADA:
            "Programada",

        CANCELADA:
            "Cancelada",

        FINALIZADA:
            "Finalizada"
    };

    return estados[estado]
        ?? estado
        ?? "-";
}

function normalizarHora(
    hora
) {

    if (!hora) {
        return "00:00:00";
    }

    const partes =
        String(hora)
            .split(":");

    const horas =
        partes[0]
        ?? "00";

    const minutos =
        partes[1]
        ?? "00";

    const segundos =
        partes[2]
        ?? "00";

    return `${horas}:${minutos}:${segundos}`;
}

function formatearHora(
    hora
) {

    if (!hora) {
        return "-";
    }

    return String(hora)
        .substring(
            0,
            5
        );
}

function formatearFecha(
    fechaISO
) {

    if (!fechaISO) {
        return "-";
    }

    const fecha =
        new Date(
            `${fechaISO}T00:00:00`
        );

    if (
        Number.isNaN(
            fecha.getTime()
        )
    ) {
        return fechaISO;
    }

    return new Intl.DateTimeFormat(
        "es-PE",
        {
            weekday: "long",
            day: "2-digit",
            month: "long",
            year: "numeric"
        }
    ).format(fecha);
}

async function obtenerMensajeError(
    response
) {

    const contenido =
        await response.text();

    if (!contenido) {

        return "No se pudieron cargar las clases.";
    }

    try {

        const error =
            JSON.parse(
                contenido
            );

        return error.detail
            ?? error.message
            ?? "No se pudieron cargar las clases.";

    } catch (error) {

        return "No se pudieron cargar las clases.";
    }
}

function mostrarMensajeCalendario(
    texto
) {

    const mensaje =
        document.getElementById(
            "mensajeCalendario"
        );

    mensaje.textContent =
        texto;

    mensaje.classList.remove(
        "d-none"
    );
}

function ocultarMensajeCalendario() {

    const mensaje =
        document.getElementById(
            "mensajeCalendario"
        );

    mensaje.textContent =
        "";

    mensaje.classList.add(
        "d-none"
    );
}