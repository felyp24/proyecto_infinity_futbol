document.addEventListener(
    "DOMContentLoaded",
    iniciarPagina
);

const fechaClases =
    document.getElementById(
        "fechaClases"
    );

const selectClase =
    document.getElementById(
        "selectClase"
    );

const botonActualizar =
    document.getElementById(
        "botonActualizar"
    );

const mensajePagina =
    document.getElementById(
        "mensajePagina"
    );

const contenidoAsistencia =
    document.getElementById(
        "contenidoAsistencia"
    );

const tituloClaseSeleccionada =
    document.getElementById(
        "tituloClaseSeleccionada"
    );

const detalleClaseSeleccionada =
    document.getElementById(
        "detalleClaseSeleccionada"
    );

const contadorMarcacion =
    document.getElementById(
        "contadorMarcacion"
    );

const tablaAlumnos =
    document.getElementById(
        "tablaAlumnos"
    );

const botonTodosPresentes =
    document.getElementById(
        "botonTodosPresentes"
    );

const botonTodosAusentes =
    document.getElementById(
        "botonTodosAusentes"
    );

const botonGuardarAsistencias =
    document.getElementById(
        "botonGuardarAsistencias"
    );

let clasesCargadas = [];
let alumnosCargados = [];

async function iniciarPagina() {

    const hoy =
        obtenerFechaLocalActual();

    fechaClases.value =
        hoy;

    fechaClases.max =
        hoy;

    fechaClases.addEventListener(
        "change",
        async () => {

            await cargarClases();
        }
    );

    selectClase.addEventListener(
        "change",
        async () => {

            await cargarAlumnosClase();
        }
    );

    botonActualizar.addEventListener(
        "click",
        async () => {

            await cargarClases(
                selectClase.value
            );
        }
    );

    botonTodosPresentes.addEventListener(
        "click",
        () => marcarTodos(
            "PRESENTE"
        )
    );

    botonTodosAusentes.addEventListener(
        "click",
        () => marcarTodos(
            "AUSENTE"
        )
    );

    botonGuardarAsistencias.addEventListener(
        "click",
        guardarAsistencias
    );

    await cargarClases();
}

async function cargarClases(
    idClaseConservar = ""
) {

    ocultarContenido();

    mostrarMensaje(
        "Cargando clases...",
        false
    );

    selectClase.innerHTML = `
        <option value="">
            Seleccione una clase
        </option>
    `;

    if (!fechaClases.value) {
        mostrarMensaje(
            "Debe seleccionar una fecha.",
            true
        );

        return;
    }

    try {

        const parametros =
            new URLSearchParams({
                fecha:
                    fechaClases.value
            });

        const response = await fetch(
            `/api/coordinador/asistencias/clases?${
                parametros.toString()
            }`,
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

        clasesCargadas =
            await response.json();

        mostrarClases(
            clasesCargadas
        );

        if (
            idClaseConservar
            && clasesCargadas.some(
                clase =>
                    clase.idClase
                    === idClaseConservar
            )
        ) {
            selectClase.value =
                idClaseConservar;

            await cargarAlumnosClase();

            return;
        }

        if (clasesCargadas.length === 0) {

            mostrarMensaje(
                "No existen clases disponibles para la fecha seleccionada.",
                false
            );

            return;
        }

        mostrarMensaje(
            "Selecciona una clase para registrar la asistencia.",
            false
        );

    } catch (error) {

        mostrarMensaje(
            error.message,
            true
        );
    }
}

function mostrarClases(
    clases
) {

    selectClase.innerHTML = `
        <option value="">
            Seleccione una clase
        </option>
    `;

    clases.forEach(
        clase => {

            const opcion =
                document.createElement(
                    "option"
                );

            opcion.value =
                clase.idClase;

            opcion.textContent =
                `${formatearHora(
                    clase.horaInicio
                )} - `
                + `${clase.titulo} - `
                + `${clase.nombreSede} - `
                + `${clase.reservasConfirmadas} reservados`;

            selectClase.appendChild(
                opcion
            );
        }
    );
}

async function cargarAlumnosClase() {

    const idClase =
        selectClase.value;

    if (!idClase) {

        ocultarContenido();

        mostrarMensaje(
            "Selecciona una clase.",
            false
        );

        return;
    }

    mostrarMensaje(
        "Cargando alumnos reservados...",
        false
    );

    try {

        const response = await fetch(
            `/api/coordinador/asistencias/clases/${
                encodeURIComponent(
                    idClase
                )
            }/alumnos`,
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

        alumnosCargados =
            await response.json();

        mostrarInformacionClase(
            idClase
        );

        mostrarAlumnos(
            alumnosCargados
        );

        actualizarContador();

        contenidoAsistencia.classList.remove(
            "oculto"
        );

        ocultarMensaje();

    } catch (error) {

        ocultarContenido();

        mostrarMensaje(
            error.message,
            true
        );
    }
}

function mostrarInformacionClase(
    idClase
) {

    const clase =
        clasesCargadas.find(
            item =>
                item.idClase
                === idClase
        );

    if (!clase) {
        return;
    }

    tituloClaseSeleccionada.textContent =
        clase.titulo;

    detalleClaseSeleccionada.textContent =
        `${formatearFecha(
            clase.fechaClase
        )} | `
        + `${formatearHora(
            clase.horaInicio
        )} - `
        + `${formatearHora(
            clase.horaFin
        )} | `
        + `${clase.nombreSede}, `
        + `cancha ${clase.numeroCancha} | `
        + `${clase.nombreEntrenador}`;
}

function mostrarAlumnos(
    alumnos
) {

    if (
        !Array.isArray(alumnos)
        || alumnos.length === 0
    ) {
        tablaAlumnos.innerHTML = `
            <tr>
                <td
                        colspan="6"
                        class="sin-registros"
                >
                    Esta clase no tiene alumnos
                    con reserva confirmada.
                </td>
            </tr>
        `;

        botonGuardarAsistencias.disabled =
            true;

        botonTodosPresentes.disabled =
            true;

        botonTodosAusentes.disabled =
            true;

        return;
    }

    botonGuardarAsistencias.disabled =
        false;

    botonTodosPresentes.disabled =
        false;

    botonTodosAusentes.disabled =
        false;

    tablaAlumnos.innerHTML =
        alumnos
            .map(alumno => `
                <tr
                        data-id-reserva="${
                            escaparHtml(
                                alumno.idReserva
                            )
                        }"
                >

                    <td>

                        <span class="dato-principal">
                            ${escaparHtml(
                                alumno.nombreCompleto
                            )}
                        </span>

                        <span class="dato-secundario">
                            ${escaparHtml(
                                alumno.username
                            )}
                        </span>

                    </td>

                    <td>

                        <span class="dato-principal">
                            ${formatearTipoDocumento(
                                alumno.tipoDocumento
                            )}
                        </span>

                        <span class="dato-secundario">
                            ${escaparHtml(
                                alumno.numeroDocumento
                            )}
                        </span>

                    </td>

                    <td>
                        ${escaparHtml(
                            alumno.idReserva
                        )}
                    </td>

                    <td>

                        <select
                                class="select-estado"
                                aria-label="Estado de asistencia"
                        >

                            <option value="">
                                Seleccionar
                            </option>

                            <option
                                    value="PRESENTE"
                                    ${
                                        alumno.estadoAsistencia
                                        === "PRESENTE"
                                            ? "selected"
                                            : ""
                                    }
                            >
                                Presente
                            </option>

                            <option
                                    value="TARDANZA"
                                    ${
                                        alumno.estadoAsistencia
                                        === "TARDANZA"
                                            ? "selected"
                                            : ""
                                    }
                            >
                                Tardanza
                            </option>

                            <option
                                    value="AUSENTE"
                                    ${
                                        alumno.estadoAsistencia
                                        === "AUSENTE"
                                            ? "selected"
                                            : ""
                                    }
                            >
                                Ausente
                            </option>

                            <option
                                    value="JUSTIFICADA"
                                    ${
                                        alumno.estadoAsistencia
                                        === "JUSTIFICADA"
                                            ? "selected"
                                            : ""
                                    }
                            >
                                Justificada
                            </option>

                        </select>

                    </td>

                    <td>
                        ${formatearFechaHora(
                            alumno.horaMarcacion
                        )}
                    </td>

                    <td>

                        <input
                                type="text"
                                maxlength="255"
                                class="input-observacion"
                                value="${
                                    escaparAtributo(
                                        alumno.observacion
                                        ?? ""
                                    )
                                }"
                                placeholder="Observación opcional"
                        >

                    </td>

                </tr>
            `)
            .join("");

    tablaAlumnos
        .querySelectorAll(
            ".select-estado"
        )
        .forEach(
            select => {

                select.addEventListener(
                    "change",
                    actualizarContador
                );
            }
        );
}

function marcarTodos(
    estado
) {

    tablaAlumnos
        .querySelectorAll(
            ".select-estado"
        )
        .forEach(
            select => {

                select.value =
                    estado;
            }
        );

    actualizarContador();
}

function actualizarContador() {

    const selects =
        Array.from(
            tablaAlumnos
                .querySelectorAll(
                    ".select-estado"
                )
        );

    const registrados =
        selects.filter(
            select =>
                Boolean(
                    select.value
                )
        ).length;

    contadorMarcacion.textContent =
        `${registrados} de ${selects.length} registrados`;
}

async function guardarAsistencias() {

    const idClase =
        selectClase.value;

    if (!idClase) {

        mostrarMensaje(
            "Debe seleccionar una clase.",
            true
        );

        return;
    }

    const filas =
        Array.from(
            tablaAlumnos
                .querySelectorAll(
                    "tr[data-id-reserva]"
                )
        );

    if (filas.length === 0) {

        mostrarMensaje(
            "La clase no tiene alumnos reservados.",
            true
        );

        return;
    }

    const asistencias =
        [];

    for (const fila of filas) {

        const estado =
            fila.querySelector(
                ".select-estado"
            ).value;

        if (!estado) {

            mostrarMensaje(
                "Debes seleccionar un estado para todos los alumnos.",
                true
            );

            fila.scrollIntoView({
                behavior: "smooth",
                block: "center"
            });

            return;
        }

        asistencias.push({
            idReserva:
                fila.dataset.idReserva,

            estadoAsistencia:
                estado,

            observacion:
                fila.querySelector(
                    ".input-observacion"
                ).value.trim()
        });
    }

    botonGuardarAsistencias.disabled =
        true;

    botonGuardarAsistencias.textContent =
        "Guardando...";

    mostrarMensaje(
        "Guardando asistencias...",
        false
    );

    try {

        const csrf =
            await obtenerCsrf();

        const response = await fetch(
            `/api/coordinador/asistencias/clases/${
                encodeURIComponent(
                    idClase
                )
            }`,
            {
                method: "PUT",
                credentials: "same-origin",

                headers: {
                    "Content-Type":
                        "application/json",

                    "Accept":
                        "application/json",

                    [csrf.headerName]:
                        csrf.token
                },

                body: JSON.stringify({
                    asistencias:
                        asistencias
                })
            }
        );

        if (!response.ok) {

            throw new Error(
                await obtenerMensajeError(
                    response
                )
            );
        }

        const resultado =
            await response.json();

        mostrarMensaje(
            resultado.mensaje,
            false,
            true
        );

        await cargarClases(
            idClase
        );

    } catch (error) {

        mostrarMensaje(
            error.message,
            true
        );

    } finally {

        botonGuardarAsistencias.disabled =
            false;

        botonGuardarAsistencias.textContent =
            "Guardar asistencias";
    }
}

async function obtenerCsrf() {

    const response = await fetch(
        "/api/csrf",
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
            "No se pudo obtener el token de seguridad."
        );
    }

    return response.json();
}

async function obtenerMensajeError(
    response
) {

    const contenido =
        await response.text();

    if (!contenido) {

        return `No se pudo completar la operación. Código HTTP: ${response.status}`;
    }

    try {

        const error =
            JSON.parse(
                contenido
            );

        return error.detail
            ?? error.message
            ?? "No se pudo completar la operación.";

    } catch (error) {

        return contenido;
    }
}

function mostrarMensaje(
    texto,
    esError,
    esExito = false
) {

    mensajePagina.textContent =
        texto;

    mensajePagina.classList.remove(
        "oculto",
        "alert-info",
        "alert-danger",
        "alert-success"
    );

    if (esError) {

        mensajePagina.classList.add(
            "alert-danger"
        );

        return;
    }

    if (esExito) {

        mensajePagina.classList.add(
            "alert-success"
        );

        return;
    }

    mensajePagina.classList.add(
        "alert-info"
    );
}

function ocultarMensaje() {

    mensajePagina.classList.add(
        "oculto"
    );
}

function ocultarContenido() {

    contenidoAsistencia.classList.add(
        "oculto"
    );

    alumnosCargados = [];

    tablaAlumnos.innerHTML =
        "";
}

function obtenerFechaLocalActual() {

    const fecha =
        new Date();

    const anio =
        fecha.getFullYear();

    const mes =
        String(
            fecha.getMonth() + 1
        ).padStart(
            2,
            "0"
        );

    const dia =
        String(
            fecha.getDate()
        ).padStart(
            2,
            "0"
        );

    return `${anio}-${mes}-${dia}`;
}

function formatearFecha(
    valor
) {

    if (!valor) {
        return "-";
    }

    const fecha =
        new Date(
            `${valor}T00:00:00`
        );

    return new Intl.DateTimeFormat(
        "es-PE",
        {
            dateStyle: "long"
        }
    ).format(fecha);
}

function formatearHora(
    valor
) {

    if (!valor) {
        return "-";
    }

    return String(valor)
        .substring(
            0,
            5
        );
}

function formatearFechaHora(
    valor
) {

    if (!valor) {
        return "-";
    }

    const fecha =
        new Date(valor);

    if (
        Number.isNaN(
            fecha.getTime()
        )
    ) {
        return "-";
    }

    return new Intl.DateTimeFormat(
        "es-PE",
        {
            dateStyle: "short",
            timeStyle: "short"
        }
    ).format(fecha);
}

function formatearTipoDocumento(
    tipo
) {

    const tipos = {
        DNI:
            "DNI",

        CARNET_EXTRANJERIA:
            "Carnet de extranjería"
    };

    return tipos[tipo]
        ?? tipo
        ?? "-";
}

function escaparHtml(
    valor
) {

    if (
        valor === null
        || valor === undefined
    ) {
        return "";
    }

    return String(valor)
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}

function escaparAtributo(
    valor
) {

    return escaparHtml(
        valor
    );
}