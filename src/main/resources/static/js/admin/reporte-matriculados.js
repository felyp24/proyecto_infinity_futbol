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

const estadoAlumno =
    document.getElementById(
        "estadoAlumno"
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

const tablaMatriculados =
    document.getElementById(
        "tablaMatriculados"
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

    /*
     * El reporte inicia desde el primer día
     * del año actual.
     */
    const inicioAnio =
        new Date(
            hoy.getFullYear(),
            0,
            1
        );

    fechaInicio.value =
        convertirFechaInput(
            inicioAnio
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
            `/api/admin/reportes/matriculados?${
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
            reporte.matriculados
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
        `/api/admin/reportes/matriculados/pdf?${
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

    if (estadoAlumno.value) {

        parametros.set(
            "estado",
            estadoAlumno.value
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

    asignarTexto(
        "totalMatriculados",
        reporte.totalMatriculados
        ?? 0
    );

    asignarTexto(
        "matriculadosActivos",
        reporte.matriculadosActivos
        ?? 0
    );

    asignarTexto(
        "matriculadosInactivos",
        reporte.matriculadosInactivos
        ?? 0
    );
}

function mostrarDetalle(
    matriculados
) {

    if (
        !Array.isArray(matriculados)
        || matriculados.length === 0
    ) {
        tablaMatriculados.innerHTML = `
            <tr>
                <td
                        colspan="7"
                        class="sin-resultados"
                >
                    No se encontraron alumnos matriculados
                    en el periodo seleccionado.
                </td>
            </tr>
        `;

        return;
    }

    tablaMatriculados.innerHTML =
        matriculados
            .map(matriculado => `
                <tr>

                    <td>
                        ${formatearFechaHora(
                            matriculado.fechaRegistro
                        )}
                    </td>

                    <td>

                        <span class="dato-principal">
                            ${escaparHtml(
                                matriculado.nombreCompleto
                            )}
                        </span>

                        <span class="dato-secundario">
                            ${escaparHtml(
                                matriculado.idAlumno
                            )}
                        </span>

                    </td>

                    <td>

                        <span class="dato-principal">
                            ${formatearTipoDocumento(
                                matriculado.tipoDocumento
                            )}
                        </span>

                        <span class="dato-secundario">
                            ${escaparHtml(
                                matriculado.numeroDocumento
                            )}
                        </span>

                    </td>

                    <td>
                        ${formatearFecha(
                            matriculado.fechaNacimiento
                        )}
                    </td>

                    <td>

                        <span class="dato-principal">
                            ${escaparHtml(
                                matriculado.telefono
                                || "-"
                            )}
                        </span>

                        <span class="dato-secundario">
                            ${escaparHtml(
                                matriculado.correo
                            )}
                        </span>

                    </td>

                    <td>

                        <span class="dato-principal">
                            ${escaparHtml(
                                matriculado.username
                            )}
                        </span>

                        <span class="dato-secundario">
                            ${escaparHtml(
                                matriculado.correo
                            )}
                        </span>

                    </td>

                    <td>

                        <span class="
                            estado
                            ${obtenerClaseEstado(
                                matriculado.estadoUsuario
                            )}
                        ">
                            ${formatearEstadoUsuario(
                                matriculado.estadoUsuario
                            )}
                        </span>

                    </td>

                </tr>
            `)
            .join("");
}

function obtenerClaseEstado(
    estado
) {

    const clases = {
        ACTIVO:
            "estado-activo",

        INACTIVO:
            "estado-inactivo"
    };

    return clases[estado]
        ?? "";
}

function formatearEstadoAlumno(
    estado
) {

    const estados = {
        ACTIVO:
            "Activo",

        INACTIVO:
            "Inactivo"
    };

    return estados[estado]
        ?? estado
        ?? "-";
}

function formatearEstadoUsuario(
    estado
) {

    const estados = {
        ACTIVO:
            "Activo",

        INACTIVO:
            "Inactivo"
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