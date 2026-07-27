const TAMANO_PAGINA = 10;

let paginaActual = 0;
let totalPaginas = 0;
let textoBusquedaActual = "";


let cuentasActuales = [];

const formularioBusqueda =
    document.getElementById(
        "formularioBusqueda"
    );

const textoBusqueda =
    document.getElementById(
        "textoBusqueda"
    );

const botonLimpiar =
    document.getElementById(
        "botonLimpiar"
    );

const mensajeEstado =
    document.getElementById(
        "mensajeEstado"
    );

const contenidoCreditos =
    document.getElementById(
        "contenidoCreditos"
    );

const resumen =
    document.getElementById(
        "resumen"
    );

const tablaCreditos =
    document.getElementById(
        "tablaCreditos"
    );

const informacionPagina =
    document.getElementById(
        "informacionPagina"
    );

const botonAnterior =
    document.getElementById(
        "botonAnterior"
    );

const botonSiguiente =
    document.getElementById(
        "botonSiguiente"
    );

const modalAjuste =
    document.getElementById(
        "modalAjuste"
    );

const botonCerrarModal =
    document.getElementById(
        "botonCerrarModal"
    );

const botonCancelarModal =
    document.getElementById(
        "botonCancelarModal"
    );

const formularioAjuste =
    document.getElementById(
        "formularioAjuste"
    );

const idAlumnoSeleccionado =
    document.getElementById(
        "idAlumnoSeleccionado"
    );

const nombreAlumnoModal =
    document.getElementById(
        "nombreAlumnoModal"
    );

const detalleAlumnoModal =
    document.getElementById(
        "detalleAlumnoModal"
    );

const saldoActualModal =
    document.getElementById(
        "saldoActualModal"
    );

const nuevoSaldo =
    document.getElementById(
        "nuevoSaldo"
    );

const motivo =
    document.getElementById(
        "motivo"
    );

const contadorMotivo =
    document.getElementById(
        "contadorMotivo"
    );

const botonGuardarAjuste =
    document.getElementById(
        "botonGuardarAjuste"
    );

document.addEventListener(
    "DOMContentLoaded",
    iniciarPagina
);

async function iniciarPagina() {

    registrarEventos();

    try {

        await cargarCreditos();

    } catch (error) {

        mostrarMensaje(
            error.message,
            "error"
        );
    }
}

function registrarEventos() {

    formularioBusqueda.addEventListener(
        "submit",
        manejarBusqueda
    );

    botonLimpiar.addEventListener(
        "click",
        limpiarBusqueda
    );

    botonAnterior.addEventListener(
        "click",
        cargarPaginaAnterior
    );

    botonSiguiente.addEventListener(
        "click",
        cargarPaginaSiguiente
    );

    tablaCreditos.addEventListener(
        "click",
        manejarClickTabla
    );

    botonCerrarModal.addEventListener(
        "click",
        cerrarModal
    );

    botonCancelarModal.addEventListener(
        "click",
        cerrarModal
    );

    modalAjuste.addEventListener(
        "click",
        event => {

            if (event.target === modalAjuste) {
                cerrarModal();
            }
        }
    );

    document.addEventListener(
        "keydown",
        event => {

            if (
                event.key === "Escape"
                && !modalAjuste.classList
                    .contains("oculto")
            ) {
                cerrarModal();
            }
        }
    );

    formularioAjuste.addEventListener(
        "submit",
        guardarAjuste
    );

    motivo.addEventListener(
        "input",
        actualizarContadorMotivo
    );
}

async function manejarBusqueda(
    event
) {
    event.preventDefault();

    textoBusquedaActual =
        textoBusqueda.value.trim();

    paginaActual = 0;

    await cargarCreditos();
}

async function limpiarBusqueda() {

    textoBusqueda.value = "";
    textoBusquedaActual = "";
    paginaActual = 0;

    await cargarCreditos();
}

async function cargarPaginaAnterior() {

    if (paginaActual <= 0) {
        return;
    }

    paginaActual--;

    await cargarCreditos();
}

async function cargarPaginaSiguiente() {

    if (
        totalPaginas === 0
        || paginaActual >= totalPaginas - 1
    ) {
        return;
    }

    paginaActual++;

    await cargarCreditos();
}

async function cargarCreditos() {

    mostrarMensaje(
        "Cargando cuentas de crédito...",
        "informacion"
    );

    const parametros =
        new URLSearchParams({
            page: paginaActual,
            size: TAMANO_PAGINA
        });

    if (textoBusquedaActual) {
        parametros.set(
            "texto",
            textoBusquedaActual
        );
    }

    const response = await fetch(
        `/api/admin/creditos?${parametros.toString()}`,
        {
            credentials: "same-origin"
        }
    );

    if (!response.ok) {
        throw new Error(
            await obtenerMensajeError(response)
        );
    }

    const pagina =
        await response.json();

    cuentasActuales =
        Array.isArray(pagina.content)
            ? pagina.content
            : [];

    paginaActual =
        Number.isInteger(pagina.number)
            ? pagina.number
            : 0;

    totalPaginas =
        Number.isInteger(pagina.totalPages)
            ? pagina.totalPages
            : 0;

    mostrarCuentas(cuentasActuales);

    actualizarPaginacion(pagina);

    contenidoCreditos.classList.remove(
        "oculto"
    );

    ocultarMensaje();
}

function mostrarCuentas(
    cuentas
) {

    if (
        !Array.isArray(cuentas)
        || cuentas.length === 0
    ) {
        tablaCreditos.innerHTML = `
            <tr>
                <td colspan="7"
                    style="text-align: center;
                           color: #6c757d;
                           padding: 28px;">
                    No se encontraron cuentas de crédito.
                </td>
            </tr>
        `;

        return;
    }

    tablaCreditos.innerHTML =
        cuentas
            .map(cuenta => `
                <tr>

                    <td>
                        <span class="dato-principal">
                            ${escaparHtml(
                                cuenta.nombreCompleto
                            )}
                        </span>

                        <span class="dato-secundario">
                            ${escaparHtml(
                                cuenta.idAlumno
                            )}
                        </span>
                    </td>

                    <td>
                        <span class="dato-principal">
                            ${escaparHtml(
                                cuenta.username
                            )}
                        </span>

                        <span class="dato-secundario">
                            ${escaparHtml(
                                cuenta.correo
                            )}
                        </span>
                    </td>

                    <td>
                        <span class="dato-principal">
                            ${formatearTipoDocumento(
                                cuenta.tipoDocumento
                            )}
                        </span>

                        <span class="dato-secundario">
                            ${escaparHtml(
                                cuenta.numeroDocumento
                            )}
                        </span>
                    </td>

                    <td>
                        <span class="
                            estado
                            ${obtenerClaseEstado(
                                cuenta.estadoUsuario
                            )}
                        ">
                            ${formatearEstado(
                                cuenta.estadoUsuario
                            )}
                        </span>

                        <span class="dato-secundario">
                            Alumno:
                            ${formatearEstado(
                                cuenta.estadoAlumno
                            )}
                        </span>
                    </td>

                    <td>
                        <span class="saldo">
                            ${cuenta.saldoActual ?? 0}
                            créditos
                        </span>
                    </td>

                    <td>
                        ${formatearFechaHora(
                            cuenta.fechaActualizacion
                        )}
                    </td>

                    <td>
                        <button
                                type="button"
                                class="boton-ajustar"
                                data-id-alumno="${
                                    escaparHtml(
                                        cuenta.idAlumno
                                    )
                                }"
                        >
                            Editar créditos
                        </button>
                    </td>

                </tr>
            `)
            .join("");
}

function manejarClickTabla(
    event
) {

    const boton =
        event.target.closest(
            ".boton-ajustar"
        );

    if (!boton) {
        return;
    }

    const idAlumno =
        boton.dataset.idAlumno;

    const cuenta =
        cuentasActuales.find(
            elemento =>
                elemento.idAlumno
                    === idAlumno
        );

    if (!cuenta) {
        mostrarMensaje(
            "No se encontró la cuenta seleccionada.",
            "error"
        );

        return;
    }

    abrirModal(cuenta);
}

function abrirModal(
    cuenta
) {

    idAlumnoSeleccionado.value =
        cuenta.idAlumno;

    nombreAlumnoModal.textContent =
        cuenta.nombreCompleto
        ?? "Alumno";

    detalleAlumnoModal.textContent =
        `${cuenta.username ?? "-"} · `
        + `${formatearTipoDocumento(
            cuenta.tipoDocumento
        )} `
        + `${cuenta.numeroDocumento ?? "-"}`;

    saldoActualModal.value =
        cuenta.saldoActual ?? 0;

    nuevoSaldo.value =
        cuenta.saldoActual ?? 0;

    motivo.value = "";

    actualizarContadorMotivo();

    modalAjuste.classList.remove(
        "oculto"
    );

    document.body.classList.add(
        "modal-abierto"
    );

    setTimeout(
        () => nuevoSaldo.focus(),
        50
    );
}

function cerrarModal() {

    modalAjuste.classList.add(
        "oculto"
    );

    document.body.classList.remove(
        "modal-abierto"
    );

    formularioAjuste.reset();

    idAlumnoSeleccionado.value = "";
    contadorMotivo.textContent = "0/100";

    botonGuardarAjuste.disabled = false;

    botonGuardarAjuste.textContent =
        "Guardar ajuste";
}

async function guardarAjuste(
    event
) {
    event.preventDefault();

    const idAlumno =
        idAlumnoSeleccionado.value;

    const saldoAnterior =
        Number(saldoActualModal.value);

    const saldoNuevo =
        Number(nuevoSaldo.value);

    const motivoAjuste =
        motivo.value.trim();

    if (!idAlumno) {
        mostrarMensaje(
            "No se pudo identificar al alumno.",
            "error"
        );

        return;
    }

    if (
        !Number.isInteger(saldoNuevo)
        || saldoNuevo < 0
        || saldoNuevo > 9999
    ) {
        mostrarMensaje(
            "El nuevo saldo debe ser un número entero entre 0 y 9999.",
            "advertencia"
        );

        return;
    }

    if (saldoNuevo === saldoAnterior) {
        mostrarMensaje(
            "El nuevo saldo debe ser diferente al saldo actual.",
            "advertencia"
        );

        return;
    }

    if (!motivoAjuste) {
        mostrarMensaje(
            "Debe indicar el motivo del ajuste.",
            "advertencia"
        );

        return;
    }

    if (motivoAjuste.length > 100) {
        mostrarMensaje(
            "El motivo no puede superar los 100 caracteres.",
            "advertencia"
        );

        return;
    }

    botonGuardarAjuste.disabled = true;

    botonGuardarAjuste.textContent =
        "Guardando...";

    try {
        const csrfActual =
            await obtenerCsrf();

        const response = await fetch(
            `/api/admin/creditos/${encodeURIComponent(idAlumno)}`,
            {
                method: "PATCH",

                credentials: "same-origin",

                headers: {
                    "Content-Type":
                        "application/json",

                    [csrfActual.headerName]:
                        csrfActual.token
                },

                body: JSON.stringify({
                    nuevoSaldo: saldoNuevo,
                    motivo: motivoAjuste
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

        const cuentaActualizada =
            await response.json();

        cerrarModal();

        await cargarCreditos();

        mostrarMensaje(
            `El saldo de ${
                cuentaActualizada.nombreCompleto
            } se actualizó correctamente a ${
                cuentaActualizada.saldoActual
            } créditos.`,
            "exito"
        );

    } catch (error) {

        mostrarMensaje(
            error.message,
            "error"
        );

        botonGuardarAjuste.disabled = false;

        botonGuardarAjuste.textContent =
            "Guardar ajuste";
    }
}

function actualizarPaginacion(
    pagina
) {

    const totalElementos =
        pagina.totalElements ?? 0;

    resumen.textContent =
        totalElementos === 1
            ? "1 cuenta de crédito encontrada."
            : `${totalElementos} cuentas de crédito encontradas.`;

    if (totalPaginas === 0) {

        informacionPagina.textContent =
            "Página 0 de 0";

    } else {

        informacionPagina.textContent =
            `Página ${paginaActual + 1} `
            + `de ${totalPaginas}`;
    }

    botonAnterior.disabled =
        paginaActual <= 0;

    botonSiguiente.disabled =
        totalPaginas === 0
        || paginaActual >= totalPaginas - 1;
}

function actualizarContadorMotivo() {

    contadorMotivo.textContent =
        `${motivo.value.length}/100`;
}

async function obtenerCsrf() {

    const response = await fetch(
        "/api/csrf",
        {
            method: "GET",

            credentials: "same-origin",

            /*
             * Evita que Chrome reutilice un token
             * perteneciente a una sesión anterior.
             */
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
        return `Ocurrió un error. Código HTTP: ${response.status}`;
    }

    try {
        const error =
            JSON.parse(contenido);

        if (
            error.detail
            && typeof error.detail === "string"
        ) {
            return error.detail;
        }

        if (
            error.message
            && typeof error.message === "string"
        ) {
            return error.message;
        }

        if (
            Array.isArray(error.errors)
            && error.errors.length > 0
        ) {
            return error.errors
                .map(item =>
                    item.defaultMessage
                    ?? item.message
                    ?? "Dato inválido"
                )
                .join(". ");
        }

    } catch (error) {
        /*
         * La respuesta no era JSON.
         */
    }

    return contenido;
}

function mostrarMensaje(
    texto,
    tipo
) {

    mensajeEstado.textContent =
        texto;

    mensajeEstado.classList.remove(
        "oculto",
        "mensaje-error",
        "mensaje-exito",
        "mensaje-advertencia"
    );

    if (tipo === "error") {
        mensajeEstado.classList.add(
            "mensaje-error"
        );
    }

    if (tipo === "exito") {
        mensajeEstado.classList.add(
            "mensaje-exito"
        );
    }

    if (tipo === "advertencia") {
        mensajeEstado.classList.add(
            "mensaje-advertencia"
        );
    }
}

function ocultarMensaje() {

    mensajeEstado.classList.add(
        "oculto"
    );

    mensajeEstado.classList.remove(
        "mensaje-error",
        "mensaje-exito",
        "mensaje-advertencia"
    );
}

function formatearTipoDocumento(
    tipoDocumento
) {

    const nombres = {
        DNI: "DNI",

        CARNET_EXTRANJERIA:
            "Carnet de extranjería"
    };

    return nombres[tipoDocumento]
        ?? tipoDocumento
        ?? "-";
}

function formatearEstado(
    estado
) {

    if (!estado) {
        return "-";
    }

    const nombres = {
        ACTIVO: "Activo",
        INACTIVO: "Inactivo"
    };

    return nombres[estado]
        ?? estado;
}

function obtenerClaseEstado(
    estado
) {

    return estado === "ACTIVO"
        ? "estado-activo"
        : "estado-inactivo";
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