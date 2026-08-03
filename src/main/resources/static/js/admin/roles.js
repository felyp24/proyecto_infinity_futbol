let csrfData = null;
const idUsuarioActual = document.body.dataset.idUsuarioActual;

document.addEventListener("DOMContentLoaded", cargarInformacion);

async function cargarInformacion() {
    const mensajeEstado = document.getElementById("mensajeEstado");
    const tablaContenedor = document.getElementById("tablaContenedor");

    try {
        const [
            respuestaUsuarios,
            respuestaRoles,
            respuestaCsrf
        ] = await Promise.all([
            fetch("/api/admin/usuarios"),
            fetch("/api/admin/roles"),
            fetch("/api/csrf")
        ]);

        if (!respuestaUsuarios.ok) {
            throw new Error(
                `No se pudieron cargar los usuarios. Código: ${respuestaUsuarios.status}`
            );
        }

        if (!respuestaRoles.ok) {
            throw new Error(
                `No se pudieron cargar los roles. Código: ${respuestaRoles.status}`
            );
        }

        if (!respuestaCsrf.ok) {
            throw new Error(
                `No se pudo obtener el token CSRF. Código: ${respuestaCsrf.status}`
            );
        }

        const usuarios = await respuestaUsuarios.json();
        const roles = await respuestaRoles.json();

        csrfData = await respuestaCsrf.json();

        mostrarUsuarios(usuarios, roles);

        mensajeEstado.classList.add("oculto");
        tablaContenedor.classList.remove("oculto");

    } catch (error) {
        console.error(error);

        mensajeEstado.textContent =
            "No se pudo cargar la información de usuarios y roles.";

        mensajeEstado.classList.add("mensaje-error");
    }
}

function mostrarUsuarios(usuarios, rolesDisponibles) {
    const tablaUsuarios = document.getElementById("tablaUsuarios");

    tablaUsuarios.replaceChildren();

    if (usuarios.length === 0) {
        const fila = document.createElement("tr");
        const celda = document.createElement("td");

        celda.colSpan = 7;
        celda.textContent = "No hay usuarios registrados.";

        fila.appendChild(celda);
        tablaUsuarios.appendChild(fila);

        return;
    }

    usuarios.forEach(usuario => {
        const fila = document.createElement("tr");

        fila.appendChild(crearCelda(usuario.username));
        fila.appendChild(crearCelda(usuario.correo));
        fila.appendChild(crearCeldaEstado(usuario));

        fila.appendChild(
            crearCelda(formatearFecha(usuario.fechaCreacion))
        );

        fila.appendChild(
            crearCeldaSelector(usuario, rolesDisponibles)
        );

        fila.appendChild(
            crearCeldaAccion(usuario)
        );
        fila.appendChild(
            crearCeldaAcceso(usuario)
        );

        tablaUsuarios.appendChild(fila);
    });
}

function crearCelda(texto) {
    const celda = document.createElement("td");

    celda.textContent = texto ?? "—";

    return celda;
}

function crearCeldaEstado(usuario) {
    const celda = document.createElement("td");
    const etiqueta = document.createElement("span");

    celda.id = `estado-${usuario.idUsuario}`;

    etiqueta.textContent = usuario.estado;
    etiqueta.classList.add("estado");

    aplicarClaseEstado(etiqueta, usuario.estado);

    celda.appendChild(etiqueta);

    return celda;
}

function crearCeldaSelector(usuario, rolesDisponibles) {
    const celda = document.createElement("td");
    const selector = document.createElement("select");

    selector.id = `rol-${usuario.idUsuario}`;
    selector.dataset.idUsuario = usuario.idUsuario;

    const rolActual = usuario.roles.length > 0
        ? usuario.roles[0]
        : null;

    rolesDisponibles.forEach(rol => {
        const opcion = document.createElement("option");

        opcion.value = rol.nombreRol;
        opcion.textContent = rol.nombreRol;

        if (rol.nombreRol === rolActual) {
            opcion.selected = true;
        }

        selector.appendChild(opcion);
    });

    if (usuario.idUsuario === idUsuarioActual) {
        selector.disabled = true;

        selector.title =
            "No puedes cambiar tu propio rol de administrador";
    }

    celda.appendChild(selector);

    return celda;
}

function crearCeldaAccion(usuario) {
    const celda = document.createElement("td");
    const boton = document.createElement("button");

    boton.type = "button";

    if (usuario.idUsuario === idUsuarioActual) {
        boton.textContent = "Tu cuenta";
        boton.disabled = true;

        boton.title =
            "No puedes modificar tu propio rol";

        celda.appendChild(boton);

        return celda;
    }

    boton.textContent = "Guardar";
    boton.dataset.idUsuario = usuario.idUsuario;

    boton.addEventListener("click", () => {
        guardarCambioRol(
            usuario.idUsuario,
            boton
        );
    });

    celda.appendChild(boton);

    return celda;
}
function crearCeldaAcceso(usuario) {
    const celda = document.createElement("td");
    const boton = document.createElement("button");

    boton.type = "button";
    boton.id = `acceso-${usuario.idUsuario}`;

    if (usuario.idUsuario === idUsuarioActual) {
        boton.textContent = "Tu cuenta";
        boton.disabled = true;
        boton.title =
            "No puedes deshabilitar tu propia cuenta";

        celda.appendChild(boton);

        return celda;
    }

    configurarBotonAcceso(
        boton,
        usuario.idUsuario,
        usuario.estado
    );

    celda.appendChild(boton);

    return celda;
}

async function obtenerCsrfActual() {

    const response = await fetch(
        "/api/csrf",
        {
            method: "GET",

            credentials:
                "same-origin",

            cache:
                "no-store",

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

async function guardarCambioRol(
    idUsuario,
    boton
) {

    const selector =
        document.getElementById(
            `rol-${idUsuario}`
        );

    const nuevoRol =
        selector.value;

    const mensajeEstado =
        document.getElementById(
            "mensajeEstado"
        );

    boton.disabled = true;
    boton.textContent = "Guardando...";

    try {

        /*
         * Se solicita un token actualizado
         * inmediatamente antes del PUT.
         */
        const csrfActual =
            await obtenerCsrfActual();

        const response = await fetch(
            `/api/admin/usuarios/${
                encodeURIComponent(idUsuario)
            }/rol`,
            {
                method:
                    "PUT",

                credentials:
                    "same-origin",

                cache:
                    "no-store",

                headers: {
                    "Content-Type":
                        "application/json",

                    "Accept":
                        "application/json",

                    [csrfActual.headerName]:
                        csrfActual.token
                },

                body: JSON.stringify({
                    rol:
                        nuevoRol
                })
            }
        );

        if (!response.ok) {

            throw new Error(
                await obtenerMensajeCambioRol(
                    response
                )
            );
        }

        const usuarioActualizado =
            await response.json();

        mensajeEstado.textContent =
            `El rol de ${
                usuarioActualizado.username
            } fue actualizado a ${
                nuevoRol
            }.`;

        mensajeEstado.classList.remove(
            "oculto",
            "mensaje-error"
        );

        mensajeEstado.classList.add(
            "mensaje-exito"
        );

        /*
         * Volvemos a cargar la información para
         * reflejar los roles guardados en la base de datos.
         */
        await cargarInformacion();

    } catch (error) {

        console.error(
            "Error al cambiar el rol:",
            error
        );

        mensajeEstado.textContent =
            error.message;

        mensajeEstado.classList.remove(
            "oculto",
            "mensaje-exito"
        );

        mensajeEstado.classList.add(
            "mensaje-error"
        );

    } finally {

        boton.disabled = false;
        boton.textContent = "Guardar";
    }
}

async function obtenerMensajeCambioRol(
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
     * Primero mostramos el mensaje real
     * enviado por Spring Boot.
     */
    if (respuesta?.detail) {
        return respuesta.detail;
    }

    if (respuesta?.message) {
        return respuesta.message;
    }

    if (response.status === 409) {

        return "No puedes retirar tu propio rol de administrador.";
    }

    if (response.status === 404) {

        return "El usuario solicitado no existe.";
    }

    if (response.status === 400) {

        return "El rol seleccionado no es válido.";
    }

    if (response.status === 401) {

        return "Tu sesión venció. Inicia sesión nuevamente.";
    }

    if (response.status === 403) {

        return "La solicitud fue rechazada. Verifica el token de seguridad y los permisos.";
    }

    return `No se pudo actualizar el rol. Código HTTP: ${response.status}`;
}

async function cambiarEstadoUsuario(
    idUsuario,
    boton
) {
    const estadoActual = boton.dataset.estadoActual;

    const nuevoEstado =
        estadoActual === "ACTIVO"
            ? "INACTIVO"
            : "ACTIVO";

    const accion =
        nuevoEstado === "INACTIVO"
            ? "deshabilitar"
            : "habilitar";

    const confirmado = window.confirm(
        `¿Deseas ${accion} esta cuenta?`
    );

    if (!confirmado) {
        return;
    }

    const mensajeEstado =
        document.getElementById("mensajeEstado");

    boton.disabled = true;
    boton.textContent =
        nuevoEstado === "INACTIVO"
            ? "Deshabilitando..."
            : "Habilitando...";

    try {
        const csrfActual =
                await obtenerCsrfActual();

        const response = await fetch(
            `/api/admin/usuarios/${idUsuario}/estado`,
            {
                method: "PATCH",

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

                body: JSON.stringify({
                    estado:
                        nuevoEstado
                })
            }
        );

        if (!response.ok) {
            throw new Error(
                await obtenerMensajeEstadoError(response)
            );
        }

        const resultado = await response.json();

        actualizarEstadoVisual(
            idUsuario,
            resultado.estado
        );

        configurarBotonAcceso(
            boton,
            idUsuario,
            resultado.estado
        );

        mensajeEstado.textContent =
            resultado.mensaje;

        mensajeEstado.classList.remove(
            "oculto",
            "mensaje-error"
        );

    } catch (error) {
          console.error(error);

          mensajeEstado.textContent = error.message;

          mensajeEstado.classList.remove("oculto");
          mensajeEstado.classList.add("mensaje-error");

          configurarBotonAcceso(
              boton,
              idUsuario,
              estadoActual
          );
      }
}

function formatearFecha(fecha) {
    if (!fecha) {
        return "—";
    }

    const fechaConvertida = new Date(fecha);

    if (Number.isNaN(fechaConvertida.getTime())) {
        return fecha;
    }

    return new Intl.DateTimeFormat(
        "es-PE",
        {
            dateStyle: "short",
            timeStyle: "short"
        }
    ).format(fechaConvertida);
}

function aplicarClaseEstado(etiqueta, estado) {
    etiqueta.classList.remove(
        "estado-activo",
        "estado-inactivo"
    );

    if (estado === "ACTIVO") {
        etiqueta.classList.add("estado-activo");
    } else {
        etiqueta.classList.add("estado-inactivo");
    }
}

function configurarBotonAcceso(
    boton,
    idUsuario,
    estadoActual
) {
    boton.disabled = false;

    boton.classList.remove(
        "boton-deshabilitar",
        "boton-habilitar"
    );

    boton.dataset.idUsuario = idUsuario;
    boton.dataset.estadoActual = estadoActual;

    if (estadoActual === "ACTIVO") {
        boton.textContent = "Deshabilitar";
        boton.classList.add("boton-deshabilitar");
    } else {
        boton.textContent = "Habilitar";
        boton.classList.add("boton-habilitar");
    }

    boton.onclick = () => {
        cambiarEstadoUsuario(idUsuario, boton);
    };
}

function actualizarEstadoVisual(
    idUsuario,
    nuevoEstado
) {
    const celda =
        document.getElementById(`estado-${idUsuario}`);

    const etiqueta = celda.querySelector(".estado");

    etiqueta.textContent = nuevoEstado;

    aplicarClaseEstado(
        etiqueta,
        nuevoEstado
    );
}

async function obtenerMensajeEstadoError(response) {
    let respuesta = null;

    try {
        respuesta = await response.json();
    } catch {
        respuesta = null;
    }

    if (response.status === 409) {
        return respuesta?.detail
            ?? "No puedes deshabilitar tu propia cuenta.";
    }

    if (response.status === 404) {
        return respuesta?.detail
            ?? "No se encontró el usuario solicitado.";
    }

    if (response.status === 403) {
        return "No tienes permiso para cambiar el estado.";
    }

    if (response.status === 400) {
        return "El estado enviado no es válido.";
    }

    return respuesta?.detail
        ?? "No se pudo actualizar el estado del usuario.";
}