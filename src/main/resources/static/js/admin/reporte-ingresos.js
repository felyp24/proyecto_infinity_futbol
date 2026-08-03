const formularioReporte =
    document.getElementById(
        "formularioReporte"
    );

const fechaInicio =
    document.getElementById(
        "fechaInicio"
    );

const fechaFin =
    document.getElementById(
        "fechaFin"
    );

const botonExportarPdf =
    document.getElementById(
        "botonExportarPdf"
    );

const mensajeReporte =
    document.getElementById(
        "mensajeReporte"
    );

const contenidoReporte =
    document.getElementById(
        "contenidoReporte"
    );

const totalIngresos =
    document.getElementById(
        "totalIngresos"
    );

const cantidadPagos =
    document.getElementById(
        "cantidadPagos"
    );

const creditosVendidos =
    document.getElementById(
        "creditosVendidos"
    );

const ticketPromedio =
    document.getElementById(
        "ticketPromedio"
    );

const tablaIngresos =
    document.getElementById(
        "tablaIngresos"
    );

document.addEventListener(
    "DOMContentLoaded",
    iniciarReporteIngresos
);

async function iniciarReporteIngresos() {

    asignarPeriodoActual();

    formularioReporte.addEventListener(
        "submit",
        async event => {

            event.preventDefault();

            await cargarReporte();
        }
    );

    botonExportarPdf.addEventListener(
        "click",
        exportarReportePdf
    );

    await cargarReporte();
}

function asignarPeriodoActual() {

    const hoy =
        new Date();

    const primerDiaMes =
        new Date(
            hoy.getFullYear(),
            hoy.getMonth(),
            1
        );

    fechaInicio.value =
        convertirFechaInput(
            primerDiaMes
        );

    fechaFin.value =
        convertirFechaInput(
            hoy
        );
}

async function cargarReporte() {

    if (!validarFechas()) {
        return;
    }

    mostrarMensaje(
        "Generando reporte...",
        false
    );

    contenidoReporte.classList.add(
        "oculto"
    );

    const parametros =
        crearParametros();

    try {

        const response = await fetch(
            `/api/admin/reportes/ingresos?${parametros}`,
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

        const reporte =
            await response.json();

        mostrarResumen(
            reporte
        );

        mostrarDetalle(
            reporte.ingresos
        );

        ocultarMensaje();

        contenidoReporte.classList.remove(
            "oculto"
        );

    } catch (error) {

        mostrarMensaje(
            error.message,
            true
        );
    }
}

function exportarReportePdf() {

    if (!validarFechas()) {
        return;
    }

    const parametros =
        crearParametros();

    window.location.href =
        `/api/admin/reportes/ingresos/pdf?${parametros}`;
}

function crearParametros() {

    return new URLSearchParams({
        fechaInicio:
            fechaInicio.value,

        fechaFin:
            fechaFin.value
    }).toString();
}

function validarFechas() {

    if (
        !fechaInicio.value
        || !fechaFin.value
    ) {
        mostrarMensaje(
            "Debe seleccionar la fecha inicial y la fecha final.",
            true
        );

        return false;
    }

    if (
        fechaInicio.value
        > fechaFin.value
    ) {
        mostrarMensaje(
            "La fecha inicial no puede ser posterior a la fecha final.",
            true
        );

        return false;
    }

    return true;
}

function mostrarResumen(
    reporte
) {

    totalIngresos.textContent =
        formatearMonto(
            reporte.totalIngresos,
            reporte.moneda
        );

    cantidadPagos.textContent =
        reporte.cantidadPagos
        ?? 0;

    creditosVendidos.textContent =
        reporte.totalCreditosVendidos
        ?? 0;

    ticketPromedio.textContent =
        formatearMonto(
            reporte.ticketPromedio,
            reporte.moneda
        );
}

function mostrarDetalle(
    ingresos
) {

    if (
        !Array.isArray(ingresos)
        || ingresos.length === 0
    ) {
        tablaIngresos.innerHTML = `
            <tr>
                <td
                        colspan="8"
                        class="sin-resultados"
                >
                    No se registraron ingresos
                    en el periodo seleccionado.
                </td>
            </tr>
        `;

        return;
    }

    tablaIngresos.innerHTML =
        ingresos
            .map(ingreso => `
                <tr>

                    <td>
                        ${formatearFechaHora(
                            ingreso.fechaAprobacion
                        )}
                    </td>

                    <td>
                        <span class="dato-principal">
                            ${escaparHtml(
                                ingreso.idPago
                            )}
                        </span>
                    </td>

                    <td>

                        <span class="dato-principal">
                            ${escaparHtml(
                                ingreso.nombreCliente
                            )}
                        </span>

                        <span class="dato-secundario">
                            ${escaparHtml(
                                ingreso.idAlumno
                            )}
                        </span>

                    </td>

                    <td>

                        <span class="dato-principal">
                            ${formatearTipoDocumento(
                                ingreso.tipoDocumento
                            )}
                        </span>

                        <span class="dato-secundario">
                            ${escaparHtml(
                                ingreso.numeroDocumento
                            )}
                        </span>

                    </td>

                    <td>

                        <span class="dato-principal">
                            ${escaparHtml(
                                ingreso.nombrePaquete
                            )}
                        </span>

                    </td>

                    <td>
                        ${ingreso.cantidadCreditos ?? 0}
                    </td>

                    <td>
                        ${formatearMetodoPago(
                            ingreso.metodoPago
                        )}
                    </td>

                    <td class="monto-ingreso">
                        ${formatearMonto(
                            ingreso.montoTotal,
                            ingreso.moneda
                        )}
                    </td>

                </tr>
            `)
            .join("");
}

function convertirFechaInput(
    fecha
) {

    const anio =
        fecha.getFullYear();

    const mes =
        String(
            fecha.getMonth() + 1
        ).padStart(
            2,
            "0"
        );

    const dia =
        String(
            fecha.getDate()
        ).padStart(
            2,
            "0"
        );

    return `${anio}-${mes}-${dia}`;
}

function formatearMonto(
    monto,
    moneda
) {

    const valor =
        Number(monto);

    if (
        Number.isNaN(valor)
    ) {
        return "S/ 0.00";
    }

    try {

        return new Intl.NumberFormat(
            "es-PE",
            {
                style: "currency",
                currency: moneda || "PEN"
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

async function obtenerMensajeError(
    response
) {

    const contenido =
        await response.text();

    if (!contenido) {

        return `No se pudo generar el reporte. Código HTTP: ${response.status}`;
    }

    try {

        const error =
            JSON.parse(
                contenido
            );

        return error.detail
            ?? error.message
            ?? "No se pudo generar el reporte.";

    } catch (error) {

        return contenido;
    }
}

function mostrarMensaje(
    texto,
    esError
) {

    mensajeReporte.textContent =
        texto;

    mensajeReporte.classList.remove(
        "oculto",
        "alert-info",
        "alert-danger"
    );

    mensajeReporte.classList.add(
        esError
            ? "alert-danger"
            : "alert-info"
    );
}

function ocultarMensaje() {

    mensajeReporte.classList.add(
        "oculto"
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