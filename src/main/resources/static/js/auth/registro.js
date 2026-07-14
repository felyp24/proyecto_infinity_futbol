const formularioRegistro =
    document.getElementById("formularioRegistro");

const mensajeEstado =
    document.getElementById("mensajeEstado");

const botonRegistrar =
    document.getElementById("botonRegistrar");

let csrfData = null;

document.addEventListener(
    "DOMContentLoaded",
    iniciarPagina
);

formularioRegistro.addEventListener(
    "submit",
    registrarUsuario
);

async function iniciarPagina() {
    configurarFechaMaxima();

    try {
        csrfData = await obtenerCsrf();
    } catch (error) {
        console.error(error);

        mostrarError(
            "No se pudo inicializar el formulario de registro."
        );

        botonRegistrar.disabled = true;
    }
}

async function obtenerCsrf() {
    const response = await fetch("/api/csrf");

    if (!response.ok) {
        throw new Error(
            "No se pudo obtener el token de seguridad."
        );
    }

    return response.json();
}

async function registrarUsuario(event) {
    event.preventDefault();

    ocultarMensaje();

    const password =
        document.getElementById("password").value;

    const confirmarPassword =
        document.getElementById(
            "confirmarPassword"
        ).value;

    if (password !== confirmarPassword) {
        mostrarError(
            "Las contraseñas no coinciden."
        );

        return;
    }

    const request = construirRequest(
        password,
        confirmarPassword
    );

    botonRegistrar.disabled = true;
    botonRegistrar.textContent = "Registrando...";

    try {
        if (!csrfData) {
            csrfData = await obtenerCsrf();
        }

        const response = await fetch(
            "/api/auth/registro",
            {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    [csrfData.headerName]: csrfData.token
                },
                body: JSON.stringify(request)
            }
        );

        if (!response.ok) {
            throw new Error(
                await obtenerMensajeError(response)
            );
        }

        const resultado = await response.json();

        mostrarExito(
            `${resultado.mensaje}. Ahora puedes iniciar sesión.`
        );

        formularioRegistro.reset();

        setTimeout(() => {
            window.location.href =
                "/login?registro=exitoso";
        }, 1800);

    } catch (error) {
        console.error(error);

        mostrarError(
            error.message
            || "No se pudo completar el registro."
        );

    } finally {
        botonRegistrar.disabled = false;
        botonRegistrar.textContent = "Crear cuenta";
    }
}

function construirRequest(
    password,
    confirmarPassword
) {
    return {
        username:
            obtenerValor("username"),

        correo:
            obtenerValor("correo"),

        password,

        confirmarPassword,

        nombres:
            obtenerValor("nombres"),

        apellidos:
            obtenerValor("apellidos"),

        tipoDocumento:
            obtenerValor("tipoDocumento"),

        numeroDocumento:
            obtenerValor("numeroDocumento"),

        fechaNacimiento:
            obtenerValorOpcional("fechaNacimiento"),

        telefono:
            obtenerValorOpcional("telefono")
    };
}

function obtenerValor(idElemento) {
    return document
        .getElementById(idElemento)
        .value
        .trim();
}

function obtenerValorOpcional(idElemento) {
    const valor = obtenerValor(idElemento);

    return valor === "" ? null : valor;
}

function configurarFechaMaxima() {
    const campoFecha =
        document.getElementById("fechaNacimiento");

    const ayer = new Date();

    ayer.setDate(
        ayer.getDate() - 1
    );

    campoFecha.max =
        convertirFechaInput(ayer);
}

function convertirFechaInput(fecha) {
    const anio =
        fecha.getFullYear();

    const mes = String(
        fecha.getMonth() + 1
    ).padStart(2, "0");

    const dia = String(
        fecha.getDate()
    ).padStart(2, "0");

    return `${anio}-${mes}-${dia}`;
}

async function obtenerMensajeError(response) {
    let respuesta = null;

    try {
        respuesta = await response.json();
    } catch {
        respuesta = null;
    }

    if (response.status === 409) {
        return respuesta?.detail
            ?? "El usuario, correo o documento ya está registrado.";
    }

    if (response.status === 400) {
        return obtenerMensajeValidacion(respuesta);
    }

    if (response.status === 403) {
        return "La solicitud fue rechazada por seguridad. Recarga la página.";
    }

    if (response.status === 500) {
        return respuesta?.detail
            ?? "No se pudo completar el registro.";
    }

    return respuesta?.detail
        ?? respuesta?.message
        ?? "Ocurrió un error al registrar la cuenta.";
}

function obtenerMensajeValidacion(respuesta) {
    if (respuesta?.detail) {
        return respuesta.detail;
    }

    if (Array.isArray(respuesta?.errors)
            && respuesta.errors.length > 0) {

        return respuesta.errors
            .map(error =>
                error.defaultMessage
                ?? error.message
            )
            .filter(Boolean)
            .join(" ");
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

    mensajeEstado.scrollIntoView({
        behavior: "smooth",
        block: "center"
    });
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

    mensajeEstado.scrollIntoView({
        behavior: "smooth",
        block: "center"
    });
}

function ocultarMensaje() {
    mensajeEstado.classList.add("oculto");

    mensajeEstado.classList.remove(
        "mensaje-error",
        "mensaje-exito"
    );
}