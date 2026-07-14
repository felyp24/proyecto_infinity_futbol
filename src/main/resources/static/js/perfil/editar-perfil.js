document.addEventListener(
    "DOMContentLoaded",
    iniciarPagina
);

function iniciarPagina() {
    const formulario =
        document.getElementById("formEditarPerfil");

    const botonGuardar =
        document.getElementById("botonGuardar");

    const mensaje =
        document.getElementById("mensaje");

    formulario.addEventListener(
        "submit",
        actualizarPerfil
    );

    configurarFechaMaxima();

    async function actualizarPerfil(event) {
        event.preventDefault();

        ocultarMensaje();

        botonGuardar.disabled = true;
        botonGuardar.textContent = "Guardando...";

        try {
            const csrfData = await obtenerCsrf();

            const request = {
                nombres:
                    obtenerValor("nombres"),

                apellidos:
                    obtenerValor("apellidos"),

                tipoDocumento:
                    obtenerValor("tipoDocumento"),

                numeroDocumento:
                    obtenerValor("numeroDocumento"),

                fechaNacimiento:
                    obtenerValorOpcional(
                        "fechaNacimiento"
                    ),

                telefono:
                    obtenerValorOpcional("telefono")
            };

            const response = await fetch(
                "/api/perfil",
                {
                    method: "PUT",
                    headers: {
                        "Content-Type":
                            "application/json",

                        [csrfData.headerName]:
                            csrfData.token
                    },
                    body: JSON.stringify(request)
                }
            );

            if (!response.ok) {
                throw new Error(
                    await obtenerMensajeError(response)
                );
            }

            mostrarExito(
                "Los datos personales fueron actualizados correctamente."
            );

        } catch (error) {
            console.error(error);

            mostrarError(
                error.message
                || "No se pudieron actualizar los datos."
            );

        } finally {
            botonGuardar.disabled = false;
            botonGuardar.textContent =
                "Guardar cambios";
        }
    }

    async function obtenerCsrf() {
        const response =
            await fetch("/api/csrf");

        if (!response.ok) {
            throw new Error(
                "No se pudo obtener el token de seguridad."
            );
        }

        return response.json();
    }

    function obtenerValor(idElemento) {
        return document
            .getElementById(idElemento)
            .value
            .trim();
    }

    function obtenerValorOpcional(idElemento) {
        const valor =
            obtenerValor(idElemento);

        return valor === ""
            ? null
            : valor;
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
                ?? "El número de documento ya pertenece a otro alumno.";
        }

        if (response.status === 400) {
            return respuesta?.detail
                ?? respuesta?.message
                ?? "Revisa los datos ingresados.";
        }

        if (response.status === 401) {
            return "Tu sesión ha terminado. Inicia sesión nuevamente.";
        }

        if (response.status === 403) {
            return "La solicitud fue rechazada por seguridad.";
        }

        return respuesta?.detail
            ?? "No se pudieron guardar los cambios.";
    }

    function configurarFechaMaxima() {
        const campoFecha =
            document.getElementById(
                "fechaNacimiento"
            );

        const ayer = new Date();

        ayer.setDate(
            ayer.getDate() - 1
        );

        const anio =
            ayer.getFullYear();

        const mes = String(
            ayer.getMonth() + 1
        ).padStart(2, "0");

        const dia = String(
            ayer.getDate()
        ).padStart(2, "0");

        campoFecha.max =
            `${anio}-${mes}-${dia}`;
    }

    function mostrarExito(texto) {
        mensaje.textContent = texto;

        mensaje.classList.remove(
            "mensaje-error"
        );

        mensaje.classList.add(
            "mensaje-exito"
        );
    }

    function mostrarError(texto) {
        mensaje.textContent = texto;

        mensaje.classList.remove(
            "mensaje-exito"
        );

        mensaje.classList.add(
            "mensaje-error"
        );
    }

    function ocultarMensaje() {
        mensaje.textContent = "";

        mensaje.classList.remove(
            "mensaje-exito",
            "mensaje-error"
        );
    }
}