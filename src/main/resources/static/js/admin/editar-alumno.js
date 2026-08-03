const idAlumno =
    document.getElementById("idAlumno").value;

const mensajeEstado =
    document.getElementById("mensajeEstado");

const formularioAlumno =
    document.getElementById("formularioAlumno");

const botonGuardar =
    document.getElementById("botonGuardar");

let csrfData = null;

document.addEventListener(
    "DOMContentLoaded",
    iniciarPagina
);

async function iniciarPagina() {
    configurarFechaMaxima();

    try {
        await cargarAlumno();

        formularioAlumno.classList.remove(
            "oculto"
        );

        mensajeEstado.classList.add(
            "oculto"
        );

    } catch (error) {
        console.error(error);

        mostrarError(
            error.message
            || "No se pudo cargar la información del alumno."
        );
    }
}

formularioAlumno.addEventListener(
    "submit",
    actualizarAlumno
);

async function obtenerCsrf() {

    const response = await fetch(
        "/api/csrf",
        {
            method: "GET",
            credentials: "same-origin",
            cache: "no-store",

            headers: {
                "Accept": "application/json"
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

async function cargarAlumno() {
    const response = await fetch(
        `/api/admin/alumnos/${encodeURIComponent(idAlumno)}`
    );

    if (!response.ok) {
        throw new Error(
            await obtenerMensajeError(response)
        );
    }

    const alumno = await response.json();

    document.getElementById("nombres").value =
        alumno.nombres ?? "";

    document.getElementById("apellidos").value =
        alumno.apellidos ?? "";

    document.getElementById("tipoDocumento").value =
        alumno.tipoDocumento ?? "";

    document.getElementById("numeroDocumento").value =
        alumno.numeroDocumento ?? "";

    document.getElementById("fechaNacimiento").value =
        alumno.fechaNacimiento ?? "";

    document.getElementById("telefono").value =
        alumno.telefono ?? "";

    document.getElementById("correo").value =
        alumno.correo ?? "";
}

async function actualizarAlumno(
    event
) {
    event.preventDefault();

    ocultarMensaje();

    botonGuardar.disabled = true;
    botonGuardar.textContent = "Guardando...";

    const request = {
        nombres:
            document
                .getElementById("nombres")
                .value
                .trim(),

        apellidos:
            document
                .getElementById("apellidos")
                .value
                .trim(),

        tipoDocumento:
            document
                .getElementById("tipoDocumento")
                .value,

        numeroDocumento:
            document
                .getElementById("numeroDocumento")
                .value
                .trim(),

        fechaNacimiento:
            obtenerValorOpcional(
                "fechaNacimiento"
            ),

        telefono:
            obtenerValorOpcional(
                "telefono"
            ),

        correo:
            document
                .getElementById("correo")
                .value
                .trim()
    };

    try {

        /*
         * Se obtiene un token actualizado
         * inmediatamente antes del PUT.
         */
        const csrfActual =
            await obtenerCsrf();

        const response = await fetch(
            `/api/admin/alumnos/${
                encodeURIComponent(idAlumno)
            }`,
            {
                method: "PUT",

                credentials: "same-origin",

                cache: "no-store",

                headers: {
                    "Content-Type":
                        "application/json",

                    "Accept":
                        "application/json",

                    [csrfActual.headerName]:
                        csrfActual.token
                },

                body: JSON.stringify(
                    request
                )
            }
        );

        if (!response.ok) {
            throw new Error(
                await obtenerMensajeError(
                    response
                )
            );
        }

        const alumnoActualizado =
            await response.json();

        mostrarExito(
            `La información de ${
                alumnoActualizado.nombres
            } fue actualizada correctamente.`
        );

    } catch (error) {
        console.error(
            "Error al actualizar alumno:",
            error
        );

        mostrarError(
            error.message
            || "No se pudo actualizar la información."
        );

    } finally {
        botonGuardar.disabled = false;
        botonGuardar.textContent =
            "Guardar cambios";
    }
}

function obtenerValorOpcional(idElemento) {
    const valor =
        document.getElementById(idElemento).value.trim();

    return valor === "" ? null : valor;
}

function configurarFechaMaxima() {
    const fechaNacimiento =
        document.getElementById("fechaNacimiento");

    const ayer = new Date();

    ayer.setDate(
        ayer.getDate() - 1
    );

    fechaNacimiento.max =
        convertirFechaInput(ayer);
}

function convertirFechaInput(fecha) {
    const anio = fecha.getFullYear();

    const mes = String(
        fecha.getMonth() + 1
    ).padStart(2, "0");

    const dia = String(
        fecha.getDate()
    ).padStart(2, "0");

    return `${anio}-${mes}-${dia}`;
}

async function obtenerMensajeError(
    response
) {
    let respuesta = null;

    try {
        respuesta =
            await response.json();

    } catch (error) {
        respuesta = null;
    }

    /*
     * Mostrar primero el mensaje real
     * generado por Spring Boot.
     */
    if (respuesta?.detail) {
        return respuesta.detail;
    }

    if (respuesta?.message) {
        return respuesta.message;
    }

    if (response.status === 404) {
        return "No se encontró el alumno.";
    }

    if (response.status === 409) {
        return "El documento o correo ya está registrado.";
    }

    if (response.status === 400) {
        return obtenerMensajeValidacion(
            respuesta
        );
    }

    if (response.status === 401) {
        return "Tu sesión venció. Inicia sesión nuevamente.";
    }

    if (response.status === 403) {
        return "La solicitud fue rechazada por el token de seguridad.";
    }

    return `No se pudo actualizar el alumno. Código HTTP: ${response.status}`;
}

function obtenerMensajeValidacion(respuesta) {
    if (respuesta?.detail) {
        return respuesta.detail;
    }

    if (respuesta?.message) {
        return respuesta.message;
    }

    return "Revisa los datos ingresados.";
}

function mostrarExito(mensaje) {
    mensajeEstado.textContent = mensaje;

    mensajeEstado.classList.remove(
        "oculto",
        "mensaje-error"
    );

    mensajeEstado.classList.add(
        "mensaje-exito"
    );
}

function mostrarError(mensaje) {
    mensajeEstado.textContent = mensaje;

    mensajeEstado.classList.remove(
        "oculto",
        "mensaje-exito"
    );

    mensajeEstado.classList.add(
        "mensaje-error"
    );
}

function ocultarMensaje() {
    mensajeEstado.classList.add("oculto");

    mensajeEstado.classList.remove(
        "mensaje-error",
        "mensaje-exito"
    );
}