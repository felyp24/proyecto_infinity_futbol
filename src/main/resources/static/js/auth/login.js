const formularioLogin =
    document.getElementById("formularioLogin");

const botonLogin =
    document.getElementById("botonLogin");

const mensajeEstado =
    document.getElementById("mensajeEstado");

let csrfData = null;

document.addEventListener(
    "DOMContentLoaded",
    iniciarPagina
);

formularioLogin.addEventListener(
    "submit",
    iniciarSesion
);

async function iniciarPagina() {
    try {
        csrfData = await obtenerCsrf();
    } catch (error) {
        console.error(error);

        mostrarError(
            "No se pudo inicializar el formulario de inicio de sesión."
        );

        botonLogin.disabled = true;
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

async function iniciarSesion(event) {
    event.preventDefault();

    ocultarMensaje();

    const username =
        document
            .getElementById("username")
            .value
            .trim();

    const password =
        document
            .getElementById("password")
            .value;

    if (!username || !password) {
        mostrarError(
            "Ingresa el nombre de usuario y la contraseña."
        );

        return;
    }

    botonLogin.disabled = true;
    botonLogin.textContent = "Ingresando...";

    try {
        if (!csrfData) {
            csrfData = await obtenerCsrf();
        }

        const response = await fetch(
            "/api/auth/login",
            {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    [csrfData.headerName]: csrfData.token
                },
                body: JSON.stringify({
                    username,
                    password
                })
            }
        );

        if (!response.ok) {
            throw new Error(
                await obtenerMensajeError(response)
            );
        }

        const resultado = await response.json();

        window.location.href =
            resultado.rutaDestino || "/perfil";

    } catch (error) {
        console.error(error);

        mostrarError(
            error.message
            || "No se pudo iniciar sesión."
        );

    } finally {
        botonLogin.disabled = false;
        botonLogin.textContent = "Iniciar sesión";
    }
}

async function obtenerMensajeError(response) {
    let respuesta = null;

    try {
        respuesta = await response.json();
    } catch {
        respuesta = null;
    }

    if (response.status === 401) {
        return respuesta?.detail
            ?? "El usuario, la contraseña o el estado de la cuenta no son válidos.";
    }

    if (response.status === 400) {
        return respuesta?.detail
            ?? "Debes ingresar el usuario y la contraseña.";
    }

    if (response.status === 403) {
        return "La solicitud fue rechazada por seguridad. Recarga la página.";
    }

    return respuesta?.detail
        ?? respuesta?.message
        ?? "Ocurrió un error al iniciar sesión.";
}

function mostrarError(mensaje) {
    mensajeEstado.textContent = mensaje;

    mensajeEstado.classList.remove("oculto");
}

function ocultarMensaje() {
    mensajeEstado.classList.add("oculto");
    mensajeEstado.textContent = "";
}