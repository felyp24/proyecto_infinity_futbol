document.addEventListener(
    "DOMContentLoaded",
    iniciarPagina
);

let calendario;
let modalClase;
let claseSeleccionada = null;
let saldoActual = 0;
let paginaHistorialActual = 0;
let totalPaginasHistorial = 0;

const TAMANO_PAGINA_HISTORIAL = 6;

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

const listaReservasProximas =
    document.getElementById(
        "listaReservasProximas"
    );

const listaReservasPasadas =
    document.getElementById(
        "listaReservasPasadas"
    );

const btnActualizarHistorial =
    document.getElementById(
        "btnActualizarHistorial"
    );

const btnHistorialAnterior =
    document.getElementById(
        "btnHistorialAnterior"
    );

const btnHistorialSiguiente =
    document.getElementById(
        "btnHistorialSiguiente"
    );

const informacionPaginaHistorial =
    document.getElementById(
        "informacionPaginaHistorial"
    );

const contadorNotificaciones =
    document.getElementById(
        "contadorNotificaciones"
    );

const listaNotificaciones =
    document.getElementById(
        "listaNotificaciones"
    );

const btnActualizarNotificaciones =
    document.getElementById(
        "btnActualizarNotificaciones"
    );
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

    listaReservasProximas.addEventListener(
        "click",
        manejarAccionReserva
    );

    btnActualizarHistorial.addEventListener(
        "click",
        async () => {

            paginaHistorialActual = 0;

            await cargarReservasPasadas();
        }
    );

    btnHistorialAnterior.addEventListener(
        "click",
        async () => {

            if (paginaHistorialActual <= 0) {
                return;
            }

            paginaHistorialActual--;

            await cargarReservasPasadas();
        }
    );

    btnHistorialSiguiente.addEventListener(
        "click",
        async () => {

            if (
                totalPaginasHistorial === 0
                || paginaHistorialActual
                    >= totalPaginasHistorial - 1
            ) {
                return;
            }

            paginaHistorialActual++;

            await cargarReservasPasadas();
        }
    );

    listaNotificaciones.addEventListener(
        "click",
        manejarClickNotificacion
    );

    btnActualizarNotificaciones.addEventListener(
        "click",
        cargarNotificaciones
    );

    await Promise.all([
        cargarResumen(),
        cargarReservasProximas(),
        cargarReservasPasadas(),
        cargarNotificaciones()
    ]);

    inicializarCalendario();

    setInterval(
        cargarNotificaciones,
        60000
    );
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

        await Promise.all([
            cargarResumen(),
            cargarReservasProximas()
        ]);

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
        await Promise.all([
            cargarResumen(),
            cargarReservasProximas(),
            cargarReservasPasadas()
        ]);

        calendario.refetchEvents();

    } catch (error) {
        mostrarMensajePagina(
            error.message,
            "danger"
        );

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

    mensajePagina.scrollIntoView({
        behavior: "smooth",
        block: "center"
    });
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


async function cargarReservasProximas() {
    listaReservasProximas.innerHTML = `
        <div class="col-12">
            <div class="estado-reservas">
                Cargando reservas...
            </div>
        </div>
    `;

    try {
        const response = await fetch(
            "/api/inicio/reservas",
            {
                credentials: "same-origin"
            }
        );

        if (!response.ok) {
            throw new Error(
                await obtenerMensajeError(response)
            );
        }

        const reservas =
            await response.json();

        mostrarReservasProximas(reservas);

    } catch (error) {
        console.error(
            "Error al cargar reservas:",
            error
        );

        listaReservasProximas.innerHTML = `
            <div class="col-12">
                <div class="alert alert-danger mb-0">
                    ${escaparHtml(error.message)}
                </div>
            </div>
        `;
    }
}


function mostrarReservasProximas(reservas) {
    if (!reservas || reservas.length === 0) {
        listaReservasProximas.innerHTML = `
            <div class="col-12">
                <div class="estado-reservas">
                    Todavía no tienes reservas próximas.
                    Selecciona una clase disponible en el calendario.
                </div>
            </div>
        `;

        return;
    }

    listaReservasProximas.innerHTML =
        reservas
            .map(reserva => `
                <div class="col-lg-6">

                    <article class="tarjeta-reserva">

                        <div class="encabezado-reserva">

                            <h3 class="titulo-reserva">
                                ${escaparHtml(reserva.titulo)}
                            </h3>

                            <span class="estado-reserva">
                                Confirmada
                            </span>

                        </div>

                        <div class="fecha-reserva-clase">
                            ${formatearFechaISO(
                                reserva.fechaClase
                            )}
                            ·
                            ${formatearHoraTexto(
                                reserva.horaInicio
                            )}
                            -
                            ${formatearHoraTexto(
                                reserva.horaFin
                            )}
                        </div>

                        <div class="detalles-reserva">

                            <div class="detalle-reserva">

                                <span class="detalle-reserva-etiqueta">
                                    Entrenador
                                </span>

                                <span class="detalle-reserva-valor">
                                    ${escaparHtml(
                                        reserva.nombreEntrenador
                                    )}
                                </span>

                            </div>

                            <div class="detalle-reserva">

                                <span class="detalle-reserva-etiqueta">
                                    Sede
                                </span>

                                <span class="detalle-reserva-valor">
                                    ${escaparHtml(
                                        reserva.nombreSede
                                    )}
                                </span>

                            </div>

                            <div class="detalle-reserva">

                                <span class="detalle-reserva-etiqueta">
                                    Distrito
                                </span>

                                <span class="detalle-reserva-valor">
                                    ${escaparHtml(
                                        reserva.distrito
                                    )}
                                </span>

                            </div>

                            <div class="detalle-reserva">

                                <span class="detalle-reserva-etiqueta">
                                    Cancha
                                </span>

                                <span class="detalle-reserva-valor">
                                    Cancha ${reserva.numeroCancha}
                                </span>

                            </div>

                        </div>

                        <div class="pie-reserva">

                            <span class="creditos-reserva">
                                Costo:
                                ${reserva.creditosUsados}
                                ${
                                    reserva.creditosUsados === 1
                                        ? "crédito"
                                        : "créditos"
                                }
                            </span>

                            <button
                                type="button"
                                class="btn btn-outline-danger btn-sm btn-cancelar-reserva"
                                data-action="cancelar-reserva"
                                data-id-reserva="${escaparHtml(
                                    reserva.idReserva
                                )}"
                                data-titulo="${escaparHtml(
                                    reserva.titulo
                                )}"
                            >
                                Cancelar reserva
                            </button>

                        </div>

                    </article>

                </div>
            `)
            .join("");
}


function formatearFechaISO(fechaTexto) {
    if (!fechaTexto) {
        return "-";
    }

    const partes =
        fechaTexto.split("-");

    if (partes.length !== 3) {
        return fechaTexto;
    }

    const fecha = new Date(
        Number(partes[0]),
        Number(partes[1]) - 1,
        Number(partes[2])
    );

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


function formatearHoraTexto(hora) {
    if (!hora) {
        return "-";
    }

    return hora.substring(0, 5);
}


function escaparHtml(valor) {
    const elemento =
        document.createElement("div");

    elemento.textContent =
        valor ?? "";

    return elemento.innerHTML;
}

function manejarAccionReserva(event) {
    const botonCancelar =
        event.target.closest(
            '[data-action="cancelar-reserva"]'
        );

    if (!botonCancelar) {
        return;
    }

    const idReserva =
        botonCancelar.dataset.idReserva;

    const titulo =
        botonCancelar.dataset.titulo;

    cancelarReserva(
        idReserva,
        titulo,
        botonCancelar
    );
}

async function cancelarReserva(
    idReserva,
    titulo,
    boton
) {
    if (!idReserva) {
        mostrarMensajePagina(
            "No se pudo identificar la reserva.",
            "danger"
        );

        return;
    }

    const confirmado = window.confirm(
        `¿Deseas cancelar la reserva de "${titulo}"? `
        + "El crédito utilizado será devuelto a tu cuenta."
    );

    if (!confirmado) {
        return;
    }

    const textoOriginal =
        boton.textContent;

    boton.disabled = true;
    boton.textContent =
        "Cancelando...";

    try {
        const csrf =
            await obtenerCsrf();

        const response = await fetch(
            `/api/inicio/reservas/${idReserva}`,
            {
                method: "DELETE",

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

        const reservaCancelada =
            await response.json();

        saldoActual =
            reservaCancelada.saldoCreditos;

        saldoCreditos.textContent =
            saldoActual;

        mostrarMensajePagina(
            "La reserva fue cancelada correctamente. "
            + "El crédito fue devuelto a tu cuenta.",
            "success"
        );

        await Promise.all([
            cargarResumen(),
            cargarReservasProximas()
        ]);

        calendario.refetchEvents();

    } catch (error) {
        mostrarMensajePagina(
            error.message,
            "danger"
        );

        boton.disabled = false;
        boton.textContent =
            textoOriginal;
    }
}

async function cargarNotificaciones() {
    try {
        const response = await fetch(
            "/api/inicio/notificaciones",
            {
                credentials: "same-origin"
            }
        );

        if (!response.ok) {
            throw new Error(
                await obtenerMensajeError(response)
            );
        }

        const resultado =
            await response.json();

        actualizarContadorNotificaciones(
            resultado.noLeidas ?? 0
        );

        mostrarNotificaciones(
            resultado.notificaciones ?? []
        );

    } catch (error) {
        listaNotificaciones.innerHTML = `
            <div class="estado-notificaciones text-danger">
                ${escaparHtmlNotificacion(
                    error.message
                )}
            </div>
        `;
    }
}

function actualizarContadorNotificaciones(
    cantidad
) {
    contadorNotificaciones.textContent =
        cantidad > 99
            ? "99+"
            : String(cantidad);

    contadorNotificaciones.classList.toggle(
        "d-none",
        cantidad <= 0
    );
}

function mostrarNotificaciones(
    notificaciones
) {
    if (notificaciones.length === 0) {
        listaNotificaciones.innerHTML = `
            <div class="estado-notificaciones">
                No tienes notificaciones.
            </div>
        `;

        return;
    }

    listaNotificaciones.innerHTML =
        notificaciones
            .map(notificacion => {
                const noLeida =
                    notificacion.estado
                    === "ENVIADA";

                return `
                    <button
                            type="button"
                            class="item-notificacion
                                   ${
                                       noLeida
                                           ? "no-leida"
                                           : ""
                                   }"
                            data-id-notificacion="${
                                escaparHtmlNotificacion(
                                    notificacion.idNotificacion
                                )
                            }"
                            data-estado="${
                                escaparHtmlNotificacion(
                                    notificacion.estado
                                )
                            }"
                    >
                        <span class="titulo-notificacion">
                            ${
                                escaparHtmlNotificacion(
                                    notificacion.titulo
                                )
                            }
                        </span>

                        <span class="mensaje-notificacion">
                            ${
                                escaparHtmlNotificacion(
                                    notificacion.mensaje
                                )
                            }
                        </span>

                        <span class="fecha-notificacion">
                            ${
                                formatearFechaNotificacion(
                                    notificacion.fechaEnvio
                                )
                            }
                        </span>
                    </button>
                `;
            })
            .join("");
}

async function manejarClickNotificacion(
    event
) {
    const elemento =
        event.target.closest(
            ".item-notificacion"
        );

    if (!elemento) {
        return;
    }

    const idNotificacion =
        elemento.dataset.idNotificacion;

    const estado =
        elemento.dataset.estado;

    if (estado !== "ENVIADA") {
        return;
    }

    await marcarNotificacionComoLeida(
        idNotificacion
    );
}

async function marcarNotificacionComoLeida(
    idNotificacion
) {
    try {
        const csrf =
            await obtenerCsrfNotificaciones();

        const response = await fetch(
            `/api/inicio/notificaciones/${idNotificacion}/leer`,
            {
                method: "PATCH",
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

        await cargarNotificaciones();

    } catch (error) {
        mostrarMensajePagina(
            error.message,
            "danger"
        );
    }
}

async function obtenerCsrfNotificaciones() {
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

function formatearFechaNotificacion(
    valor
) {
    if (!valor) {
        return "";
    }

    const fecha = new Date(valor);

    return new Intl.DateTimeFormat(
        "es-PE",
        {
            dateStyle: "short",
            timeStyle: "short"
        }
    ).format(fecha);
}

function escaparHtmlNotificacion(
    valor
) {
    const texto =
        valor == null
            ? ""
            : String(valor);

    return texto
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}

async function cargarReservasPasadas() {

    listaReservasPasadas.innerHTML = `
        <div class="col-12">
            <div class="estado-reservas">
                Cargando historial...
            </div>
        </div>
    `;

    const parametros =
        new URLSearchParams({
            page: paginaHistorialActual,
            size: TAMANO_PAGINA_HISTORIAL
        });

    try {

        const response = await fetch(
            `/api/inicio/reservas/historial?${parametros.toString()}`,
            {
                credentials: "same-origin"
            }
        );

        if (!response.ok) {
            throw new Error(
                await obtenerMensajeError(
                    response
                )
            );
        }

        const pagina =
            await response.json();

        paginaHistorialActual =
            Number.isInteger(pagina.number)
                ? pagina.number
                : 0;

        totalPaginasHistorial =
            Number.isInteger(pagina.totalPages)
                ? pagina.totalPages
                : 0;

        const reservas =
            Array.isArray(pagina.content)
                ? pagina.content
                : [];

        mostrarReservasPasadas(
            reservas
        );

        actualizarPaginacionHistorial();

    } catch (error) {

        console.error(
            "Error al cargar historial de reservas:",
            error
        );

        listaReservasPasadas.innerHTML = `
            <div class="col-12">

                <div class="alert alert-danger mb-0">
                    ${escaparHtml(error.message)}
                </div>

            </div>
        `;
    }
}

function mostrarReservasPasadas(
    reservas
) {

    if (
        !Array.isArray(reservas)
        || reservas.length === 0
    ) {
        listaReservasPasadas.innerHTML = `
            <div class="col-12">

                <div class="estado-reservas">
                    Todavía no tienes reservas pasadas.
                </div>

            </div>
        `;

        return;
    }

    listaReservasPasadas.innerHTML =
        reservas
            .map(reserva => `
                <div class="col-lg-6">

                    <article class="
                        tarjeta-reserva
                        tarjeta-reserva-pasada
                    ">

                        <div class="encabezado-reserva">

                            <h3 class="titulo-reserva">
                                ${escaparHtml(
                                    reserva.titulo
                                )}
                            </h3>

                            <span class="
                                estado-reserva
                                ${obtenerClaseSituacionHistorial(
                                    reserva.situacion
                                )}
                            ">
                                ${formatearSituacionHistorial(
                                    reserva.situacion
                                )}
                            </span>

                        </div>

                        <div class="fecha-reserva-clase">

                            ${formatearFechaISO(
                                reserva.fechaClase
                            )}

                            ·

                            ${formatearHoraTexto(
                                reserva.horaInicio
                            )}

                            -

                            ${formatearHoraTexto(
                                reserva.horaFin
                            )}

                        </div>

                        <div class="detalles-reserva">

                            <div class="detalle-reserva">

                                <span class="detalle-reserva-etiqueta">
                                    Entrenador
                                </span>

                                <span class="detalle-reserva-valor">
                                    ${escaparHtml(
                                        reserva.nombreEntrenador
                                    )}
                                </span>

                            </div>

                            <div class="detalle-reserva">

                                <span class="detalle-reserva-etiqueta">
                                    Sede
                                </span>

                                <span class="detalle-reserva-valor">
                                    ${escaparHtml(
                                        reserva.nombreSede
                                    )}
                                </span>

                            </div>

                            <div class="detalle-reserva">

                                <span class="detalle-reserva-etiqueta">
                                    Distrito
                                </span>

                                <span class="detalle-reserva-valor">
                                    ${escaparHtml(
                                        reserva.distrito
                                    )}
                                </span>

                            </div>

                            <div class="detalle-reserva">

                                <span class="detalle-reserva-etiqueta">
                                    Cancha
                                </span>

                                <span class="detalle-reserva-valor">
                                    Cancha ${
                                        reserva.numeroCancha ?? "-"
                                    }
                                </span>

                            </div>

                        </div>

                        <div class="pie-reserva">

                            <span class="creditos-reserva">

                                Créditos utilizados:
                                ${reserva.creditosUsados ?? 0}

                            </span>

                            <span class="identificador-reserva">

                                ${escaparHtml(
                                    reserva.idReserva
                                )}

                            </span>

                        </div>

                    </article>

                </div>
            `)
            .join("");
}

function formatearSituacionHistorial(
    situacion
) {

    const nombres = {
        FINALIZADA:
            "Clase finalizada",

        RESERVA_CANCELADA:
            "Reserva cancelada",

        CLASE_CANCELADA:
            "Clase cancelada"
    };

    return nombres[situacion]
        ?? "Finalizada";
}

function obtenerClaseSituacionHistorial(
    situacion
) {

    if (situacion === "FINALIZADA") {
        return "estado-historial-finalizada";
    }

    return "estado-historial-cancelada";
}

function actualizarPaginacionHistorial() {

    if (totalPaginasHistorial === 0) {

        informacionPaginaHistorial
            .textContent =
            "Página 0 de 0";

    } else {

        informacionPaginaHistorial
            .textContent =
            `Página ${paginaHistorialActual + 1} `
            + `de ${totalPaginasHistorial}`;
    }

    btnHistorialAnterior.disabled =
        paginaHistorialActual <= 0;

    btnHistorialSiguiente.disabled =
        totalPaginasHistorial === 0
        || paginaHistorialActual
            >= totalPaginasHistorial - 1;
}