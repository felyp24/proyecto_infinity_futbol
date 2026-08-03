const TAMANO_PAGINA = 10;

let paginaActual = 0;
let totalPaginas = 0;
let textoBusquedaActual = "";

let modalComprobante = null;

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

const contenidoComprobantes =
    document.getElementById(
        "contenidoComprobantes"
    );

const resumen =
    document.getElementById(
        "resumen"
    );

const tablaComprobantes =
    document.getElementById(
        "tablaComprobantes"
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

    modalComprobante =
        new bootstrap.Modal(
            document.getElementById(
                "modalComprobante"
            )
        );

    registrarEventos();

    try {

        await cargarComprobantes();

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
        async event => {

            event.preventDefault();

            textoBusquedaActual =
                textoBusqueda.value.trim();

            paginaActual = 0;

            await cargarComprobantes();
        }
    );

    botonLimpiar.addEventListener(
        "click",
        async () => {

            textoBusqueda.value = "";
            textoBusquedaActual = "";
            paginaActual = 0;

            await cargarComprobantes();
        }
    );

    botonAnterior.addEventListener(
        "click",
        async () => {

            if (paginaActual <= 0) {
                return;
            }

            paginaActual--;

            await cargarComprobantes();
        }
    );

    botonSiguiente.addEventListener(
        "click",
        async () => {

            if (
                totalPaginas === 0
                || paginaActual >= totalPaginas - 1
            ) {
                return;
            }

            paginaActual++;

            await cargarComprobantes();
        }
    );

    tablaComprobantes.addEventListener(
        "click",
        manejarClickTabla
    );
}

async function cargarComprobantes() {

    mostrarMensaje(
        "Cargando comprobantes...",
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
        `/api/admin/comprobantes?${parametros.toString()}`,
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

    const pagina =
        await response.json();

    const comprobantes =
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

    mostrarComprobantes(
        comprobantes
    );

    actualizarPaginacion(
        pagina
    );

    contenidoComprobantes.classList.remove(
        "oculto"
    );

    ocultarMensaje();
}

function mostrarComprobantes(
    comprobantes
) {

    if (
        !Array.isArray(comprobantes)
        || comprobantes.length === 0
    ) {
        tablaComprobantes.innerHTML = `
            <tr>
                <td
                        colspan="8"
                        style="
                            padding: 28px;
                            text-align: center;
                            color: #6c757d;
                        "
                >
                    No se encontraron comprobantes emitidos.
                </td>
            </tr>
        `;

        return;
    }

    tablaComprobantes.innerHTML =
        comprobantes
            .map(comprobante => `
                <tr>

                    <td>
                        ${formatearFechaHora(
                            comprobante.fechaEmision
                        )}
                    </td>

                    <td>

                        <span class="dato-principal numero-comprobante">
                            ${escaparHtml(
                                comprobante.numeroCompleto
                            )}
                        </span>

                        <span class="dato-secundario">
                            ${escaparHtml(
                                comprobante.idComprobante
                            )}
                        </span>

                    </td>

                    <td>

                        <span class="dato-principal">
                            ${escaparHtml(
                                comprobante.nombreCliente
                            )}
                        </span>

                        <span class="dato-secundario">
                            ${escaparHtml(
                                comprobante.username
                            )}
                        </span>

                    </td>

                    <td>

                        <span class="dato-principal">
                            ${formatearTipoDocumento(
                                comprobante.tipoDocumento
                            )}
                        </span>

                        <span class="dato-secundario">
                            ${escaparHtml(
                                comprobante.numeroDocumento
                            )}
                        </span>

                    </td>

                    <td>

                        <span class="dato-principal">
                            ${escaparHtml(
                                comprobante.nombrePaquete
                            )}
                        </span>

                        <span class="dato-secundario">
                            ${comprobante.cantidadCreditos ?? 0}
                            créditos
                        </span>

                    </td>

                    <td class="monto">
                        ${formatearMonto(
                            comprobante.montoTotal,
                            comprobante.moneda
                        )}
                    </td>

                    <td>
                        <span class="estado">
                            ${formatearEstado(
                                comprobante.estado
                            )}
                        </span>
                    </td>

                    <td>

                        <button
                                type="button"
                                class="btn
                                       btn-sm
                                       btn-outline-primary
                                       boton-ver-comprobante"
                                data-id-comprobante="${
                                    escaparHtml(
                                        comprobante.idComprobante
                                    )
                                }"
                        >
                            Ver detalle
                        </button>

                    </td>

                </tr>
            `)
            .join("");
}

async function manejarClickTabla(
    event
) {

    const boton =
        event.target.closest(
            ".boton-ver-comprobante"
        );

    if (!boton) {
        return;
    }

    const idComprobante =
        boton.dataset.idComprobante;

    await cargarDetalleComprobante(
        idComprobante
    );
}

async function cargarDetalleComprobante(
    idComprobante
) {

    try {

        const response = await fetch(
            `/api/admin/comprobantes/${
                encodeURIComponent(
                    idComprobante
                )
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

        const comprobante =
            await response.json();

        llenarDetalle(
            comprobante
        );

        modalComprobante.show();

    } catch (error) {

        mostrarMensaje(
            error.message,
            true
        );
    }
}

function llenarDetalle(
    comprobante
) {

    asignarTexto(
        "detalleNumeroCompleto",
        comprobante.numeroCompleto
    );

    asignarTexto(
        "detalleFecha",
        formatearFechaHora(
            comprobante.fechaEmision
        )
    );

    asignarTexto(
        "detalleEstado",
        formatearEstado(
            comprobante.estado
        )
    );

    asignarTexto(
        "detalleCliente",
        comprobante.nombreCliente
    );

    asignarTexto(
        "detalleDocumento",
        `${formatearTipoDocumento(
            comprobante.tipoDocumento
        )} ${comprobante.numeroDocumento ?? ""}`
    );

    asignarTexto(
        "detalleCorreo",
        comprobante.correo
    );

    asignarTexto(
        "detalleUsername",
        comprobante.username
    );

    asignarTexto(
        "detalleIdPago",
        comprobante.idPago
    );

    asignarTexto(
        "detalleMetodoPago",
        formatearMetodoPago(
            comprobante.metodoPago
        )
    );

    asignarTexto(
        "detallePaquete",
        comprobante.nombrePaquete
    );

    asignarTexto(
        "detalleCreditos",
        `${comprobante.cantidadCreditos ?? 0} créditos`
    );

    asignarTexto(
        "detalleTotal",
        formatearMonto(
            comprobante.montoTotal,
            comprobante.moneda
        )
    );
}

function actualizarPaginacion(
    pagina
) {

    const totalElementos =
        pagina.totalElements ?? 0;

    resumen.textContent =
        totalElementos === 1
            ? "1 comprobante emitido."
            : `${totalElementos} comprobantes emitidos.`;

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

function asignarTexto(
    idElemento,
    valor
) {

    const elemento =
        document.getElementById(
            idElemento
        );

    if (!elemento) {
        return;
    }

    elemento.textContent =
        valor ?? "-";
}

function formatearMonto(
    monto,
    moneda
) {

    const valor =
        Number(monto);

    if (Number.isNaN(valor)) {
        return "-";
    }

    const codigoMoneda =
        moneda || "PEN";

    try {

        return new Intl.NumberFormat(
            "es-PE",
            {
                style: "currency",
                currency: codigoMoneda
            }
        ).format(valor);

    } catch (error) {

        return `S/ ${valor.toFixed(2)}`;
    }
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

function formatearMetodoPago(
    metodo
) {

    const metodos = {
        MERCADO_PAGO:
            "Mercado Pago",

        TARJETA:
            "Tarjeta"
    };

    return metodos[metodo]
        ?? metodo
        ?? "-";
}

function formatearEstado(
    estado
) {

    const estados = {
        EMITIDO:
            "Emitido",

        ANULADO:
            "Anulado"
    };

    return estados[estado]
        ?? estado
        ?? "-";
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