const TAMANO_PAGINA = 10;

let paginaActual = 0;
let totalPaginas = 0;
let textoBusquedaActual = "";

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

const contenidoHistorial =
    document.getElementById(
        "contenidoHistorial"
    );

const resumen =
    document.getElementById(
        "resumen"
    );

const tablaHistorial =
    document.getElementById(
        "tablaHistorial"
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

document.addEventListener(
    "DOMContentLoaded",
    iniciarPagina
);

async function iniciarPagina() {

    registrarEventos();

    try {

        await cargarHistorial();

    } catch (error) {

        mostrarMensaje(
            error.message,
            true
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
}

async function manejarBusqueda(
    event
) {
    event.preventDefault();

    textoBusquedaActual =
        textoBusqueda.value.trim();

    paginaActual = 0;

    await cargarHistorial();
}

async function limpiarBusqueda() {

    textoBusqueda.value = "";
    textoBusquedaActual = "";
    paginaActual = 0;

    await cargarHistorial();
}

async function cargarPaginaAnterior() {

    if (paginaActual <= 0) {
        return;
    }

    paginaActual--;

    await cargarHistorial();
}

async function cargarPaginaSiguiente() {

    if (
        totalPaginas === 0
        || paginaActual >= totalPaginas - 1
    ) {
        return;
    }

    paginaActual++;

    await cargarHistorial();
}

async function cargarHistorial() {

    mostrarMensaje(
        "Cargando historial...",
        false
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
        `/api/admin/creditos/historial?${parametros.toString()}`,
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

    const movimientos =
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

    mostrarMovimientos(
        movimientos
    );

    actualizarPaginacion(
        pagina
    );

    contenidoHistorial.classList.remove(
        "oculto"
    );

    ocultarMensaje();
}

function mostrarMovimientos(
    movimientos
) {

    if (
        !Array.isArray(movimientos)
        || movimientos.length === 0
    ) {
        tablaHistorial.innerHTML = `
            <tr>

                <td colspan="6"
                    style="
                        padding: 28px;
                        text-align: center;
                        color: #6c757d;
                    ">
                    No se encontraron ajustes administrativos.
                </td>

            </tr>
        `;

        return;
    }

    tablaHistorial.innerHTML =
        movimientos
            .map(movimiento => {

                const cambio =
                    Number(
                        movimiento.cambioCreditos
                    ) || 0;

                const textoCambio =
                    cambio > 0
                        ? `+${cambio} créditos`
                        : `${cambio} créditos`;

                const claseCambio =
                    cambio >= 0
                        ? "cambio-positivo"
                        : "cambio-negativo";

                const claseTipo =
                    movimiento.tipoCambio
                        === "AUMENTO"
                        ? "tipo-aumento"
                        : "tipo-reduccion";

                const nombreTipo =
                    movimiento.tipoCambio
                        === "AUMENTO"
                        ? "Aumento"
                        : "Reducción";

                return `
                    <tr>

                        <td>
                            ${formatearFechaHora(
                                movimiento.fechaMovimiento
                            )}

                            <span class="dato-secundario">
                                ${escaparHtml(
                                    movimiento.idMovimiento
                                )}
                            </span>
                        </td>

                        <td>
                            <span class="dato-principal">
                                ${escaparHtml(
                                    movimiento.nombreCompleto
                                )}
                            </span>

                            <span class="dato-secundario">
                                ${escaparHtml(
                                    movimiento.numeroDocumento
                                )}
                            </span>
                        </td>

                        <td>
                            ${escaparHtml(
                                movimiento.username
                            )}
                        </td>

                        <td>
                            <span class="
                                cambio
                                ${claseCambio}
                            ">
                                ${textoCambio}
                            </span>
                        </td>

                        <td>
                            <span class="
                                tipo-cambio
                                ${claseTipo}
                            ">
                                ${nombreTipo}
                            </span>
                        </td>

                        <td class="descripcion">
                            ${escaparHtml(
                                movimiento.descripcion
                            )}
                        </td>

                    </tr>
                `;
            })
            .join("");
}

function actualizarPaginacion(
    pagina
) {

    const totalElementos =
        pagina.totalElements ?? 0;

    resumen.textContent =
        totalElementos === 1
            ? "1 ajuste administrativo registrado."
            : `${totalElementos} ajustes administrativos registrados.`;

    if (totalPaginas === 0) {

        informacionPagina.textContent =
            "Página 0 de 0";

    } else {

        informacionPagina.textContent =
            `Página ${paginaActual + 1} de ${totalPaginas}`;
    }

    botonAnterior.disabled =
        paginaActual <= 0;

    botonSiguiente.disabled =
        totalPaginas === 0
        || paginaActual >= totalPaginas - 1;
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

        return error.detail
            ?? error.message
            ?? contenido;

    } catch (error) {

        return contenido;
    }
}

function mostrarMensaje(
    texto,
    esError
) {

    mensajeEstado.textContent =
        texto;

    mensajeEstado.classList.remove(
        "oculto",
        "mensaje-error"
    );

    if (esError) {

        mensajeEstado.classList.add(
            "mensaje-error"
        );
    }
}

function ocultarMensaje() {

    mensajeEstado.classList.add(
        "oculto"
    );

    mensajeEstado.classList.remove(
        "mensaje-error"
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