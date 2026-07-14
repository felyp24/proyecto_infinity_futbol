let csrfData = null;

document.addEventListener(
    "DOMContentLoaded",
    iniciarFormulario
);

async function iniciarFormulario() {
    const formulario =
        document.getElementById("formCredenciales");

    const botonGuardar =
        document.getElementById("botonGuardar");

    try {
        const response = await fetch("/api/csrf");

        if (!response.ok) {
            throw new Error(
                "No se pudo preparar el formulario."
            );
        }

        csrfData = await response.json();

        botonGuardar.disabled = false;

        formulario.addEventListener(
            "submit",
            actualizarCredenciales
        );

    } catch (error) {
        mostrarMensaje(error.message, "error");
    }
}

async function actualizarCredenciales(event) {
    event.preventDefault();

    const formulario = event.currentTarget;
    const botonGuardar =
        document.getElementById("botonGuardar");

    if (!formulario.checkValidity()) {
        formulario.reportValidity();
        return;
    }

    const nuevaPassword =
        document.getElementById("nuevaPassword").value;

    const confirmarPassword =
        document.getElementById("confirmarPassword").value;

    if (!validarNuevasPasswords(
            nuevaPassword,
            confirmarPassword
    )) {
        return;
    }

    const datos = {
        nuevoUsername:
            document
                .getElementById("nuevoUsername")
                .value
                .trim(),

        passwordActual:
            document
                .getElementById("passwordActual")
                .value,

        nuevaPassword: nuevaPassword,

        confirmarPassword: confirmarPassword
    };

    botonGuardar.disabled = true;
    botonGuardar.textContent = "Guardando...";

    try {
        const response = await fetch(
            "/api/perfil/credenciales",
            {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json",
                    [csrfData.headerName]: csrfData.token
                },
                body: JSON.stringify(datos)
            }
        );

        if (!response.ok) {
            throw new Error(
                await obtenerMensajeError(response)
            );
        }

        const resultado = await response.json();

        mostrarMensaje(
            resultado.mensaje,
            "exito"
        );

        if (resultado.requiereNuevoInicioSesion) {
            setTimeout(() => {
                window.location.href = "/login";
            }, 1200);

            return;
        }

        botonGuardar.disabled = false;
        botonGuardar.textContent = "Guardar cambios";

    } catch (error) {
        mostrarMensaje(error.message, "error");

        botonGuardar.disabled = false;
        botonGuardar.textContent = "Guardar cambios";
    }
}

function validarNuevasPasswords(
    nuevaPassword,
    confirmarPassword
) {
    const seIngresoAlguna =
        nuevaPassword.length > 0
        || confirmarPassword.length > 0;

    if (!seIngresoAlguna) {
        return true;
    }

    if (!nuevaPassword || !confirmarPassword) {
        mostrarMensaje(
            "Debes ingresar y confirmar la nueva contraseña.",
            "error"
        );

        return false;
    }

    if (nuevaPassword.length < 8) {
        mostrarMensaje(
            "La nueva contraseña debe tener al menos 8 caracteres.",
            "error"
        );

        return false;
    }

    if (nuevaPassword !== confirmarPassword) {
        mostrarMensaje(
            "Las nuevas contraseñas no coinciden.",
            "error"
        );

        return false;
    }

    return true;
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
            ?? "La contraseña actual es incorrecta.";
    }

    if (response.status === 409) {
        return respuesta?.detail
            ?? "El nombre de usuario ya está registrado.";
    }

    if (response.status === 400) {
        return respuesta?.detail
            ?? "Revisa los datos ingresados.";
    }

    if (response.status === 403) {
        return "No tienes permiso para realizar esta operación.";
    }

    return respuesta?.detail
        ?? "No se pudieron actualizar las credenciales.";
}

function mostrarMensaje(texto, tipo) {
    const mensaje = document.getElementById("mensaje");

    mensaje.textContent = texto;

    mensaje.classList.remove(
        "mensaje-exito",
        "mensaje-error"
    );

    if (tipo === "exito") {
        mensaje.classList.add("mensaje-exito");
    } else {
        mensaje.classList.add("mensaje-error");
    }

    mensaje.scrollIntoView({
        behavior: "smooth",
        block: "center"
    });
}