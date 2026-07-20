document.addEventListener(
    "DOMContentLoaded",
    iniciarPagina
);

let calendario;
let modalClase;
let claseSeleccionada = null;
let saldoActual = 0;

const nombreCliente =
    document.getElementById("nombreCliente");

const saldoCreditos =
    document.getElementById("saldoCreditos");

const reservasProximas =
    document.getElementById("reservasProximas");

const mensajePagina =
    document.getElementById("mensajePagina");

const btnActualizarCalendario =
    document.getElementById(
        "btnActualizarCalendario"
    );

const btnReservar =
    document.getElementById("btnReservar");

async function iniciarPagina() {
    modalClase = new bootstrap.Modal(
        document.getElementById("modalClase")
    );

    btnActualizarCalendario.addEventListener(
        "click",
        actualizarCalendario
    );

    btnReservar.addEventListener(
        "click",
        reservarClase
    );

    await cargarResumen();

    inicializarCalendario();
}

async function cargarResumen() {
    try {
        const response = await fetch(
            "/api/inicio/resumen",
            {
                credentials: "same-origin"
            }
        );

        if (!response.ok) {
            throw new Error(
                await obtenerMensajeError(response)
            );
        }

        const resumen = await response.json();

        nombreCliente.textContent =
            resumen.nombreCompleto;

        saldoActual =
            resumen.saldoCreditos ?? 0;

        saldoCreditos.textContent =
            saldoActual;

        reservasProximas.textContent =
            resumen.reservasProximas ?? 0;

    } catch (error) {
        mostrarMensajePagina(
            error.message,
            "danger"
        );
    }
}

function inicializarCalendario() {
    const elementoCalendario =
        document.getElementById("calendario");

    calendario = new FullCalendar.Calendar(
        elementoCalendario,
        {
            locale: "es",

            initialView:
                window.innerWidth < 768
                    ? "listWeek"
                    : "dayGridMonth",

            eventDisplay: "block",

            firstDay: 1,
            height: "auto",
            nowIndicator: true,
            navLinks: true,

            headerToolbar: {
                left: "prev,next today",
                center: "title",
                right: "dayGridMonth,timeGridWeek,listWeek"
            },

            buttonText: {
                today: "Hoy",
                month: "Mes",
                week: "Semana",
                list: "Lista"
            },

            eventTimeFormat: {
                hour: "2-digit",
                minute: "2-digit",
                hour12: false
            },

            events: cargarEventosCalendario,
            eventClick: mostrarDetalleClase,

            eventDidMount(info) {
                const situacion =
                    info.event.extendedProps.situacion;

                info.el.title =
                    `${info.event.title} - ${situacion}`;
            }
        }
    );

    calendario.render();
}

async function cargarEventosCalendario(
    informacion,
    successCallback,
    failureCallback
) {
    try {
        /*
         * El backend recibe LocalDate, por lo que enviamos
         * solamente yyyy-MM-dd.
         */
        const fechaInicio =
            informacion.startStr.substring(0, 10);

        const fechaFin =
            informacion.endStr.substring(0, 10);

        const parametros =
            new URLSearchParams({
                start: fechaInicio,
                end: fechaFin
            });

        const response = await fetch(
            `/api/inicio/clases?${parametros}`,
            {
                credentials: "same-origin"
            }
        );

        if (!response.ok) {
            throw new Error(
                await obtenerMensajeError(response)
            );
        }

        const clases = await response.json();

        const eventos =
            clases.map(clase => ({
                id: clase.id,
                title: clase.title,
                start: clase.start,
                end: clase.end,

                classNames: [
                    obtenerClaseSituacion(
                        clase.situacion
                    )
                ],

                extendedProps: {
                    descripcion:
                        clase.descripcion,

                    nombreSede:
                        clase.nombreSede,

                    distrito:
                        clase.distrito,

                    numeroCancha:
                        clase.numeroCancha,

                    tipoSuperficie:
                        clase.tipoSuperficie,

                    nombreEntrenador:
                        clase.nombreEntrenador,

                    cupoMaximo:
                        clase.cupoMaximo,

                    cupoDisponible:
                        clase.cupoDisponible,

                    costoCreditos:
                        clase.costoCreditos,

                    reservada:
                        clase.reservada,

                    disponible:
                        clase.disponible,

                    situacion:
                        clase.situacion
                }
            }));

        successCallback(eventos);

    } catch (error) {
        mostrarMensajePagina(
            error.message,
            "danger"
        );

        failureCallback(error);
    }
}

function obtenerClaseSituacion(situacion) {
    switch (situacion) {

        case "RESERVADA":
            return "evento-reservada";

        case "SIN_CUPOS":
            return "evento-sin-cupos";

        case "INICIADA":
            return "evento-iniciada";

        default:
            return "evento-disponible";
    }
}

function mostrarDetalleClase(informacion) {
    const evento = informacion.event;
    const datos = evento.extendedProps;

    claseSeleccionada = {
        idClase: evento.id,
        titulo: evento.title,

        cupoMaximo:
            datos.cupoMaximo,

        costoCreditos:
            datos.costoCreditos,

        disponible:
            datos.disponible,

        reservada:
            datos.reservada
    };

    document
        .getElementById("modalTitulo")
        .textContent = evento.title;

    document
        .getElementById("modalDescripcion")
        .textContent =
            datos.descripcion
            || "La clase no tiene descripción.";

    document
        .getElementById("modalFecha")
        .textContent =
            formatearFecha(evento.start);

    document
        .getElementById("modalHorario")
        .textContent =
            `${formatearHora(evento.start)}`
            + " - "
            + `${formatearHora(evento.end)}`;

    document
        .getElementById("modalEntrenador")
        .textContent =
            datos.nombreEntrenador;

    document
        .getElementById("modalSede")
        .textContent =
            `${datos.nombreSede} - ${datos.distrito}`;

    document
        .getElementById("modalCancha")
        .textContent =
            `Cancha ${datos.numeroCancha}`
            + (
                datos.tipoSuperficie
                    ? ` - ${datos.tipoSuperficie}`
                    : ""
            );

    document
        .getElementById("modalCupos")
        .textContent =
            `${datos.cupoDisponible}`
            + " / "
            + `${datos.cupoMaximo}`;

    document
        .getElementById("modalCosto")
        .textContent =
            datos.costoCreditos === 1
                ? "1 crédito"
                : `${datos.costoCreditos} créditos`;

    ocultarMensajeModal();

    configurarEstadoModal(datos);

    modalClase.show();
}

function configurarEstadoModal(datos) {
    const etiqueta =
        document.getElementById("modalSituacion");

    etiqueta.className = "badge mb-2";

    if (datos.reservada) {
        etiqueta.textContent = "Ya reservada";
        etiqueta.classList.add("text-bg-primary");

        btnReservar.disabled = true;
        btnReservar.textContent =
            "Reserva confirmada";

        return;
    }

    if (datos.situacion === "SIN_CUPOS") {
        etiqueta.textContent = "Sin cupos";
        etiqueta.classList.add("text-bg-secondary");

        btnReservar.disabled = true;
        btnReservar.textContent =
            "Sin cupos disponibles";

        return;
    }

    if (datos.situacion === "INICIADA") {
        etiqueta.textContent = "Clase iniciada";
        etiqueta.classList.add("text-bg-warning");

        btnReservar.disabled = true;
        btnReservar.textContent =
            "La clase ya comenzó";

        return;
    }

    etiqueta.textContent = "Disponible";
    etiqueta.classList.add("text-bg-success");

    const costo =
        datos.costoCreditos ?? 1;

    if (saldoActual < costo) {
        btnReservar.disabled = true;
        btnReservar.textContent =
            "Créditos insuficientes";

        mostrarMensajeModal(
            "No tienes créditos suficientes para reservar esta clase.",
            "warning"
        );

        return;
    }

    btnReservar.disabled = false;
    btnReservar.textContent =
        "Confirmar reserva";
}

async function reservarClase() {
    if (
        !claseSeleccionada
        || !claseSeleccionada.disponible
    ) {
        return;
    }

    const confirmado = window.confirm(
        `¿Deseas reservar "${claseSeleccionada.titulo}" `
        + `utilizando ${claseSeleccionada.costoCreditos} crédito?`
    );

    if (!confirmado) {
        return;
    }

    bloquearBotonReserva(true);

    try {
        const csrf = await obtenerCsrf();

        const response = await fetch(
            `/api/inicio/reservas/${claseSeleccionada.idClase}`,
            {
                method: "POST",

                credentials: "same-origin",

                headers: {
                    [csrf.headerName]:
                        csrf.token
                }
            }
        );

        if (!response.ok) {
            throw new Error(
                await obtenerMensajeError(response)
            );
        }

        const reserva = await response.json();

        saldoActual =
            reserva.saldoCreditos;

        saldoCreditos.textContent =
            saldoActual;

        document
            .getElementById("modalCupos")
            .textContent =
                `${reserva.cuposDisponibles}`
                + " / "
                + `${claseSeleccionada.cupoMaximo}`;

        claseSeleccionada.disponible = false;
        claseSeleccionada.reservada = true;

        btnReservar.disabled = true;
        btnReservar.textContent =
            "Reserva confirmada";

        const etiqueta =
            document.getElementById(
                "modalSituacion"
            );

        etiqueta.className =
            "badge mb-2 text-bg-primary";

        etiqueta.textContent =
            "Ya reservada";

        mostrarMensajeModal(
            "La reserva fue confirmada correctamente. Se descontó un crédito de tu cuenta.",
            "success"
        );

        await cargarResumen();

        calendario.refetchEvents();

    } catch (error) {
        mostrarMensajeModal(
            error.message,
            "danger"
        );

        bloquearBotonReserva(false);
    }
}

async function obtenerCsrf() {
    const response = await fetch(
        "/api/csrf",
        {
            credentials: "same-origin"
        }
    );

    if (!response.ok) {
        throw new Error(
            "No se pudo obtener el token de seguridad."
        );
    }

    return response.json();
}

async function actualizarCalendario() {
    btnActualizarCalendario.disabled = true;

    btnActualizarCalendario.textContent =
        "Actualizando...";

    try {
        await cargarResumen();

        calendario.refetchEvents();

    } finally {
        btnActualizarCalendario.disabled = false;

        btnActualizarCalendario.textContent =
            "Actualizar";
    }
}

function bloquearBotonReserva(bloquear) {
    btnReservar.disabled = bloquear;

    btnReservar.textContent =
        bloquear
            ? "Reservando..."
            : "Confirmar reserva";
}

function mostrarMensajePagina(texto, tipo) {
    mensajePagina.textContent = texto;

    mensajePagina.className =
        `alert alert-${tipo}`;
}

function mostrarMensajeModal(texto, tipo) {
    const mensaje =
        document.getElementById("mensajeModal");

    mensaje.textContent = texto;

    mensaje.className =
        `alert alert-${tipo}`;
}

function ocultarMensajeModal() {
    const mensaje =
        document.getElementById("mensajeModal");

    mensaje.textContent = "";

    mensaje.className =
        "alert d-none";
}

function formatearFecha(fecha) {
    if (!fecha) {
        return "-";
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

function formatearHora(fecha) {
    if (!fecha) {
        return "-";
    }

    return new Intl.DateTimeFormat(
        "es-PE",
        {
            hour: "2-digit",
            minute: "2-digit",
            hour12: false
        }
    ).format(fecha);
}

async function obtenerMensajeError(response) {
    const contenido =
        await response.text();

    if (!contenido) {
        return "No se pudo completar la operación.";
    }

    try {
        const error = JSON.parse(contenido);

        return error.detail
            || error.message
            || error.error
            || "No se pudo completar la operación.";

    } catch {
        return contenido;
    }
}