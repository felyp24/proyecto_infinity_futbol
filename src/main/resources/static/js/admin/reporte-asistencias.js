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

const textoBusqueda =
    document.getElementById(
        "textoBusqueda"
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

const tablaAsistencias =
    document.getElementById(
        "tablaAsistencias"
    );

document.addEventListener(
    "DOMContentLoaded",
    iniciarPagina
);

async function iniciarPagina() {

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
        exportarPdf
    );

    await cargarReporte();
}

function asignarPeriodoActual() {

    const hoy =
        new Date();

    const inicioMes =
        new Date(
            hoy.getFullYear(),
            hoy.getMonth(),
            1
        );

    fechaInicio.value =
        convertirFechaInput(
            inicioMes
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

    try {

        const response = await fetch(
            `/api/admin/reportes/asistencias?${
                crearParametros()
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

        const reporte =
            await response.json();

        mostrarResumen(
            reporte
        );

        mostrarDetalle(
            reporte.asistencias
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

function exportarPdf() {

    if (!validarFechas()) {
        return;
    }

    window.location.href =
        `/api/admin/reportes/asistencias/pdf?${
            crearParametros()
        }`;
}

function crearParametros() {

    const parametros =
        new URLSearchParams({
            fechaInicio:
                fechaInicio.value,

            fechaFin:
                fechaFin.value
        });

    const texto =
        textoBusqueda.value.trim();

    if (texto) {

        parametros.set(
            "texto",
            texto
        );
    }

    return parametros.toString();
}

function validarFechas() {

    if (
        !fechaInicio.value
        || !fechaFin.value
    ) {
        mostrarMensaje(
            "Debe seleccionar ambas fechas.",
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

    asignarTexto(
        "totalRegistros",
        reporte.totalRegistros ?? 0
    );

    asignarTexto(
        "totalAlumnos",
        reporte.totalAlumnos ?? 0
    );

    asignarTexto(
        "totalPresentes",
        reporte.presentes ?? 0
    );

    asignarTexto(
        "totalTardanzas",
        reporte.tardanzas ?? 0
    );

    asignarTexto(
        "totalAusentes",
        reporte.ausentes ?? 0
    );

    asignarTexto(
        "totalJustificadas",
        reporte.justificadas ?? 0
    );

    asignarTexto(
        "porcentajeAsistencia",
        `${Number(
            reporte.porcentajeAsistencia
            ?? 0
        ).toFixed(2)}%`
    );
}

function mostrarDetalle(
    asistencias
) {

    if (
        !Array.isArray(asistencias)
        || asistencias.length === 0
    ) {
        tablaAsistencias.innerHTML = `
            <tr>
                <td
                        colspan="9"
                        class="sin-resultados"
                >
                    No se encontraron registros de asistencia
                    en el periodo seleccionado.
                </td>
            </tr>
        `;

        return;
    }

    tablaAsistencias.innerHTML =
        asistencias
            .map(asistencia => `
                <tr>

                    <td>

                        <span class="dato-principal">
                            ${formatearFecha(
                                asistencia.fechaClase
                            )}
                        </span>

                        <span class="dato-secundario">
                            ${formatearHora(
                                asistencia.horaInicio
                            )}
                            -
                            ${formatearHora(
                                asistencia.horaFin
                            )}
                        </span>

                    </td>

                    <td>

                        <span class="dato-principal">
                            ${escaparHtml(
                                asistencia.nombreAlumno
                            )}
                        </span>

                        <span class="dato-secundario">
                            ${escaparHtml(
                                asistencia.username
                            )}
                        </span>

                    </td>

                    <td>

                        <span class="dato-principal">
                            ${formatearTipoDocumento(
                                asistencia.tipoDocumento
                            )}
                        </span>

                        <span class="dato-secundario">
                            ${escaparHtml(
                                asistencia.numeroDocumento
                            )}
                        </span>

                    </td>

                    <td>
                        ${escaparHtml(
                            asistencia.tituloClase
                        )}
                    </td>

                    <td>

                        <span class="dato-principal">
                            ${escaparHtml(
                                asistencia.nombreSede
                            )}
                        </span>

                        <span class="dato-secundario">
                            Cancha ${
                                asistencia.numeroCancha
                                ?? "-"
                            }
                        </span>

                    </td>

                    <td>
                        ${escaparHtml(
                            asistencia.nombreEntrenador
                        )}
                    </td>

                    <td>
                        <span class="
                            estado-asistencia
                            ${obtenerClaseEstado(
                                asistencia.estadoAsistencia
                            )}
                        ">
                            ${formatearEstado(
                                asistencia.estadoAsistencia
                            )}
                        </span>
                    </td>

                    <td>
                        ${formatearFechaHora(
                            asistencia.horaMarcacion
                        )}
                    </td>

                    <td>
                        ${escaparHtml(
                            asistencia.observacion
                            || "-"
                        )}
                    </td>

                </tr>
            `)
            .join("");
}

function obtenerClaseEstado(
    estado
) {

    const clases = {
        PRESENTE:
            "estado-presente",

        AUSENTE:
            "estado-ausente",

        TARDANZA:
            "estado-tardanza",

        JUSTIFICADA:
            "estado-justificada"
    };

    return clases[estado]
        ?? "";
}

function formatearEstado(
    estado
) {

    const estados = {
        PRESENTE:
            "Presente",

        AUSENTE:
            "Ausente",

        TARDANZA:
            "Tardanza",

        JUSTIFICADA:
            "Justificada"
    };

    return estados[estado]
        ?? estado
        ?? "-";
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

function formatearFecha(
    valor
) {

    if (!valor) {
        return "-";
    }

    const fecha =
        new Date(
            `${valor}T00:00:00`
        );

    return new Intl.DateTimeFormat(
        "es-PE",
        {
            dateStyle: "short"
        }
    ).format(fecha);
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

function formatearHora(
    valor
) {

    if (!valor) {
        return "-";
    }

    return String(valor)
        .substring(
            0,
            5
        );
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

function asignarTexto(
    idElemento,
    valor
) {

    const elemento =
        document.getElementById(
            idElemento
        );

    if (elemento) {
        elemento.textContent =
            valor;
    }
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
            JSON.parse(contenido);

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