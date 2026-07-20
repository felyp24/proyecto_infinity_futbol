document.addEventListener(
    "DOMContentLoaded",
    iniciarPagina
);

const formCrearClase =
    document.getElementById("formCrearClase");

const btnCrearClase =
    document.getElementById("btnCrearClase");

const btnActualizar =
    document.getElementById("btnActualizar");

const selectCancha =
    document.getElementById("idCancha");

const selectEntrenador =
    document.getElementById("idEntrenador");

const tablaClases =
    document.getElementById("tablaClases");

const mensaje =
    document.getElementById("mensaje");

async function iniciarPagina() {
    establecerFechaMinima();

    formCrearClase.addEventListener(
        "submit",
        registrarClase
    );

    btnActualizar.addEventListener(
        "click",
        cargarClases
    );

    await Promise.all([
        cargarCanchas(),
        cargarEntrenadores(),
        cargarClases()
    ]);
}

function establecerFechaMinima() {
    const fechaActual =
        new Date()
            .toISOString()
            .split("T")[0];

    document
        .getElementById("fechaClase")
        .setAttribute(
            "min",
            fechaActual
        );
}

async function cargarCanchas() {
    try {
        const response = await fetch(
            "/api/coordinador/clases/canchas",
            {
                credentials: "same-origin"
            }
        );

        if (!response.ok) {
            throw new Error(
                "No se pudieron cargar las canchas"
            );
        }

        const canchas = await response.json();

        selectCancha.innerHTML =
            '<option value="">Seleccione una cancha</option>';

        canchas.forEach(cancha => {
            const option =
                document.createElement("option");

            option.value = cancha.idCancha;

            option.textContent =
                `${cancha.nombreSede} - ` +
                `Cancha ${cancha.numeroCancha} - ` +
                `${cancha.tipoSuperficie}`;

            selectCancha.appendChild(option);
        });

        if (canchas.length === 0) {
            selectCancha.innerHTML =
                '<option value="">No existen canchas disponibles</option>';
        }

    } catch (error) {
        mostrarMensaje(
            error.message,
            "danger"
        );
    }
}

async function cargarEntrenadores() {
    try {
        const response = await fetch(
            "/api/coordinador/clases/entrenadores",
            {
                credentials: "same-origin"
            }
        );

        if (!response.ok) {
            throw new Error(
                "No se pudieron cargar los entrenadores"
            );
        }

        const entrenadores =
            await response.json();

        selectEntrenador.innerHTML =
            '<option value="">Seleccione un entrenador</option>';

        entrenadores.forEach(entrenador => {
            const option =
                document.createElement("option");

            option.value =
                entrenador.idEntrenador;

            option.textContent =
                `${entrenador.nombres} ` +
                `${entrenador.apellidos}` +
                (
                    entrenador.especialidad
                        ? ` - ${entrenador.especialidad}`
                        : ""
                );

            selectEntrenador.appendChild(option);
        });

        if (entrenadores.length === 0) {
            selectEntrenador.innerHTML =
                '<option value="">No existen entrenadores activos</option>';
        }

    } catch (error) {
        mostrarMensaje(
            error.message,
            "danger"
        );
    }
}

async function cargarClases() {
    tablaClases.innerHTML = `
        <tr>
            <td colspan="7"
                class="text-center text-muted">
                Cargando clases...
            </td>
        </tr>
    `;

    try {
        const response = await fetch(
            "/api/coordinador/clases",
            {
                credentials: "same-origin"
            }
        );

        if (!response.ok) {
            throw new Error(
                "No se pudieron cargar las clases"
            );
        }

        const clases = await response.json();

        mostrarClases(clases);

    } catch (error) {
        tablaClases.innerHTML = `
            <tr>
                <td colspan="7"
                    class="text-center text-danger">
                    ${escaparHtml(error.message)}
                </td>
            </tr>
        `;
    }
}

function mostrarClases(clases) {
    if (clases.length === 0) {
        tablaClases.innerHTML = `
            <tr>
                <td colspan="7"
                    class="text-center text-muted">
                    No existen clases programadas.
                </td>
            </tr>
        `;

        return;
    }

    tablaClases.innerHTML =
        clases
            .map(clase => `
                <tr>
                    <td>
                        ${formatearFecha(clase.fechaClase)}
                    </td>

                    <td>
                        ${formatearHora(clase.horaInicio)}
                        -
                        ${formatearHora(clase.horaFin)}
                    </td>

                    <td>
                        <strong>
                            ${escaparHtml(clase.titulo)}
                        </strong>

                        ${
                            clase.descripcion
                                ? `
                                    <div class="small text-muted">
                                        ${escaparHtml(clase.descripcion)}
                                    </div>
                                  `
                                : ""
                        }
                    </td>

                    <td>
                        ${escaparHtml(
                            clase.nombreEntrenador
                        )}
                    </td>

                    <td>
                        ${escaparHtml(clase.nombreSede)}
                        <div class="small text-muted">
                            Cancha ${clase.numeroCancha}
                        </div>
                    </td>

                    <td>
                        ${clase.cupoDisponible}
                        /
                        ${clase.cupoMaximo}
                    </td>

                    <td>
                        <span class="badge badge-programada">
                            ${escaparHtml(clase.estado)}
                        </span>
                    </td>
                </tr>
            `)
            .join("");
}

async function registrarClase(event) {
    event.preventDefault();

    const request = {
        titulo:
            document
                .getElementById("titulo")
                .value
                .trim(),

        descripcion:
            document
                .getElementById("descripcion")
                .value
                .trim(),

        fechaClase:
            document
                .getElementById("fechaClase")
                .value,

        horaInicio:
            document
                .getElementById("horaInicio")
                .value,

        horaFin:
            document
                .getElementById("horaFin")
                .value,

        cupoMaximo:
            Number(
                document
                    .getElementById("cupoMaximo")
                    .value
            ),

        idCancha:
            selectCancha.value,

        idEntrenador:
            selectEntrenador.value
    };

    bloquearFormulario(true);

    try {
        const csrf = await obtenerCsrf();

        const response = await fetch(
            "/api/coordinador/clases",
            {
                method: "POST",
                credentials: "same-origin",

                headers: {
                    "Content-Type":
                        "application/json",

                    [csrf.headerName]:
                        csrf.token
                },

                body: JSON.stringify(request)
            }
        );

        if (!response.ok) {
            const error =
                await obtenerMensajeError(
                    response
                );

            throw new Error(error);
        }

        formCrearClase.reset();
        establecerFechaMinima();

        document
            .getElementById("cupoMaximo")
            .value = 15;

        mostrarMensaje(
            "La clase fue registrada correctamente.",
            "success"
        );

        await cargarClases();

    } catch (error) {
        mostrarMensaje(
            error.message,
            "danger"
        );

    } finally {
        bloquearFormulario(false);
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
            "No se pudo obtener el token de seguridad"
        );
    }

    return response.json();
}

async function obtenerMensajeError(response) {
    const contenido =
        await response.text();

    if (!contenido) {
        return "No se pudo completar la operación";
    }

    try {
        const error = JSON.parse(contenido);

        return error.detail
            || error.message
            || error.error
            || "No se pudo registrar la clase";

    } catch {
        return contenido;
    }
}

function bloquearFormulario(bloquear) {
    btnCrearClase.disabled = bloquear;

    btnCrearClase.textContent =
        bloquear
            ? "Registrando..."
            : "Registrar clase";
}

function mostrarMensaje(texto, tipo) {
    mensaje.textContent = texto;

    mensaje.className =
        `alert alert-${tipo}`;

    mensaje.scrollIntoView({
        behavior: "smooth",
        block: "center"
    });
}

function formatearFecha(fecha) {
    const partes = fecha.split("-");

    return `${partes[2]}/${partes[1]}/${partes[0]}`;
}

function formatearHora(hora) {
    return hora.substring(0, 5);
}

function escaparHtml(valor) {
    const elemento =
        document.createElement("div");

    elemento.textContent =
        valor ?? "";

    return elemento.innerHTML;
}