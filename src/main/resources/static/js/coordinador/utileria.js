const formularioFiltros =
    document.getElementById(
        "formularioFiltros"
    );

const filtroSede =
    document.getElementById(
        "filtroSede"
    );

const filtroSituacion =
    document.getElementById(
        "filtroSituacion"
    );

const textoBusqueda =
    document.getElementById(
        "textoBusqueda"
    );

const incluirInactivos =
    document.getElementById(
        "incluirInactivos"
    );

const tablaUtileria =
    document.getElementById(
        "tablaUtileria"
    );

const mensajePagina =
    document.getElementById(
        "mensajePagina"
    );

const formularioUtileria =
    document.getElementById(
        "formularioUtileria"
    );

const botonNuevaUtileria =
    document.getElementById(
        "botonNuevaUtileria"
    );

const botonGuardar =
    document.getElementById(
        "botonGuardar"
    );

const modalElemento =
    document.getElementById(
        "modalUtileria"
    );

let modalUtileria;
let registrosActuales = [];

document.addEventListener(
    "DOMContentLoaded",
    iniciarPagina
);

async function iniciarPagina() {

    modalUtileria =
        bootstrap.Modal.getOrCreateInstance(
            modalElemento
        );

    formularioFiltros.addEventListener(
        "submit",
        async event => {
            event.preventDefault();
            await cargarUtileria();
        }
    );

    botonNuevaUtileria.addEventListener(
        "click",
        abrirFormularioNuevo
    );

    formularioUtileria.addEventListener(
        "submit",
        guardarUtileria
    );

    tablaUtileria.addEventListener(
        "click",
        manejarAccionTabla
    );

    await cargarSedes();
    await cargarUtileria();
}

async function cargarSedes() {

    const response = await fetch(
        "/api/coordinador/utileria/sedes",
        {
            credentials: "same-origin",
            cache: "no-store",

            headers: {
                "Accept": "application/json"
            }
        }
    );

    if (!response.ok) {
        throw new Error(
            await obtenerMensajeError(response)
        );
    }

    const sedes =
        await response.json();

    const opciones =
        sedes
            .map(sede => `
                <option value="${escaparHtml(
                    sede.idSede
                )}">
                    ${escaparHtml(
                        sede.nombre
                    )}
                </option>
            `)
            .join("");

    filtroSede.innerHTML =
        `
            <option value="">
                Todas las sedes
            </option>
        `
        + opciones;

    document
        .getElementById(
            "idSedeFormulario"
        )
        .innerHTML =
        `
            <option value="">
                Seleccione una sede
            </option>
        `
        + opciones;
}

async function cargarUtileria() {

    mostrarMensaje(
        "Cargando utilería...",
        "info"
    );

    const parametros =
        new URLSearchParams();

    if (filtroSede.value) {
        parametros.set(
            "idSede",
            filtroSede.value
        );
    }

    if (filtroSituacion.value) {
        parametros.set(
            "situacion",
            filtroSituacion.value
        );
    }

    if (textoBusqueda.value.trim()) {
        parametros.set(
            "texto",
            textoBusqueda.value.trim()
        );
    }

    parametros.set(
        "incluirInactivos",
        incluirInactivos.checked
    );

    try {

        const response = await fetch(
            `/api/coordinador/utileria?${
                parametros.toString()
            }`,
            {
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

        const resultado =
            await response.json();

        registrosActuales =
            resultado.utileria ?? [];

        mostrarResumen(resultado);
        mostrarTabla(registrosActuales);

        ocultarMensaje();

    } catch (error) {

        mostrarMensaje(
            error.message,
            "danger"
        );
    }
}

function mostrarResumen(
    resultado
) {

    document.getElementById(
        "itemsActivos"
    ).textContent =
        resultado.itemsActivos ?? 0;

    document.getElementById(
        "itemsBajoStock"
    ).textContent =
        resultado.itemsBajoStock ?? 0;

    document.getElementById(
        "itemsAgotados"
    ).textContent =
        resultado.itemsAgotados ?? 0;

    document.getElementById(
        "unidadesFaltantes"
    ).textContent =
        resultado.unidadesFaltantes ?? 0;
}

function mostrarTabla(
    registros
) {

    if (
        !Array.isArray(registros)
        || registros.length === 0
    ) {
        tablaUtileria.innerHTML = `
            <tr>
                <td
                        colspan="9"
                        class="text-center text-muted py-4"
                >
                    No se encontraron registros de utilería.
                </td>
            </tr>
        `;

        return;
    }

    tablaUtileria.innerHTML =
        registros
            .map(item => {

                const activo =
                    item.estado === "ACTIVO";

                return `
                    <tr>

                        <td>
                            <span class="dato-principal">
                                ${escaparHtml(
                                    item.nombreSede
                                )}
                            </span>

                            <span class="dato-secundario">
                                ${escaparHtml(
                                    item.distrito
                                )}
                            </span>
                        </td>

                        <td>
                            <span class="dato-principal">
                                ${escaparHtml(
                                    item.nombre
                                )}
                            </span>

                            <span class="dato-secundario">
                                ${escaparHtml(
                                    item.unidadMedida
                                )}
                            </span>
                        </td>

                        <td>
                            ${formatearTexto(
                                item.categoria
                            )}
                        </td>

                        <td>
                            ${item.cantidadActual ?? 0}
                        </td>

                        <td>
                            ${item.stockMinimo ?? 0}
                        </td>

                        <td>
                            <strong>
                                ${item.cantidadFaltante ?? 0}
                            </strong>
                        </td>

                        <td>
                            <span class="
                                situacion-utileria
                                ${obtenerClaseSituacion(
                                    item.situacion
                                )}
                            ">
                                ${formatearSituacion(
                                    item.situacion
                                )}
                            </span>
                        </td>

                        <td>
                            <span class="dato-principal">
                                ${escaparHtml(
                                    item.actualizadoPor
                                )}
                            </span>

                            <span class="dato-secundario">
                                ${formatearFechaHora(
                                    item.fechaActualizacion
                                )}
                            </span>
                        </td>

                        <td class="text-end">

                            <button
                                    type="button"
                                    class="btn btn-sm btn-outline-primary"
                                    data-accion="editar"
                                    data-id="${escaparHtml(
                                        item.idUtileria
                                    )}"
                            >
                                Editar
                            </button>

                            <button
                                    type="button"
                                    class="btn btn-sm ${
                                        activo
                                            ? "btn-outline-danger"
                                            : "btn-outline-success"
                                    }"
                                    data-accion="${
                                        activo
                                            ? "eliminar"
                                            : "restaurar"
                                    }"
                                    data-id="${escaparHtml(
                                        item.idUtileria
                                    )}"
                            >
                                ${
                                    activo
                                        ? "Eliminar"
                                        : "Restaurar"
                                }
                            </button>

                        </td>

                    </tr>
                `;
            })
            .join("");
}

function abrirFormularioNuevo() {

    formularioUtileria.reset();

    document.getElementById(
        "idUtileria"
    ).value = "";

    document.getElementById(
        "cantidadActual"
    ).value = "0";

    document.getElementById(
        "stockMinimo"
    ).value = "0";

    document.getElementById(
        "unidadMedida"
    ).value = "UNIDAD";

    document.getElementById(
        "modalTitulo"
    ).textContent =
        "Registrar implemento";

    modalUtileria.show();
}

function abrirFormularioEditar(
    idUtileria
) {

    const item =
        registrosActuales.find(
            registro =>
                registro.idUtileria
                === idUtileria
        );

    if (!item) {
        mostrarMensaje(
            "No se encontró el registro seleccionado.",
            "danger"
        );

        return;
    }

    document.getElementById(
        "idUtileria"
    ).value =
        item.idUtileria;

    document.getElementById(
        "idSedeFormulario"
    ).value =
        item.idSede;

    document.getElementById(
        "nombre"
    ).value =
        item.nombre;

    document.getElementById(
        "categoria"
    ).value =
        item.categoria;

    document.getElementById(
        "unidadMedida"
    ).value =
        item.unidadMedida;

    document.getElementById(
        "cantidadActual"
    ).value =
        item.cantidadActual;

    document.getElementById(
        "stockMinimo"
    ).value =
        item.stockMinimo;

    document.getElementById(
        "observacion"
    ).value =
        item.observacion ?? "";

    document.getElementById(
        "modalTitulo"
    ).textContent =
        "Editar implemento";

    modalUtileria.show();
}

async function guardarUtileria(
    event
) {
    event.preventDefault();

    const idUtileria =
        document.getElementById(
            "idUtileria"
        ).value;

    const request = {
        idSede:
            document.getElementById(
                "idSedeFormulario"
            ).value,

        nombre:
            document.getElementById(
                "nombre"
            ).value.trim(),

        categoria:
            document.getElementById(
                "categoria"
            ).value,

        unidadMedida:
            document.getElementById(
                "unidadMedida"
            ).value,

        cantidadActual:
            Number(
                document.getElementById(
                    "cantidadActual"
                ).value
            ),

        stockMinimo:
            Number(
                document.getElementById(
                    "stockMinimo"
                ).value
            ),

        observacion:
            document.getElementById(
                "observacion"
            ).value.trim()
    };

    botonGuardar.disabled = true;
    botonGuardar.textContent =
        "Guardando...";

    try {

        const csrf =
            await obtenerCsrf();

        const response = await fetch(
            idUtileria
                ? `/api/coordinador/utileria/${
                    encodeURIComponent(idUtileria)
                }`
                : "/api/coordinador/utileria",
            {
                method:
                    idUtileria
                        ? "PUT"
                        : "POST",

                credentials:
                    "same-origin",

                headers: {
                    "Content-Type":
                        "application/json",

                    "Accept":
                        "application/json",

                    [csrf.headerName]:
                        csrf.token
                },

                body:
                    JSON.stringify(request)
            }
        );

        if (!response.ok) {
            throw new Error(
                await obtenerMensajeError(
                    response
                )
            );
        }

        modalUtileria.hide();

        mostrarMensaje(
            idUtileria
                ? "La utilería fue actualizada correctamente."
                : "La utilería fue registrada correctamente.",
            "success"
        );

        await cargarUtileria();

    } catch (error) {

        mostrarMensaje(
            error.message,
            "danger"
        );

    } finally {

        botonGuardar.disabled = false;
        botonGuardar.textContent =
            "Guardar";
    }
}

async function manejarAccionTabla(
    event
) {

    const boton =
        event.target.closest(
            "button[data-accion]"
        );

    if (!boton) {
        return;
    }

    const accion =
        boton.dataset.accion;

    const idUtileria =
        boton.dataset.id;

    if (accion === "editar") {
        abrirFormularioEditar(
            idUtileria
        );

        return;
    }

    if (accion === "eliminar") {

        const confirmado =
            window.confirm(
                "El registro quedará inactivo. ¿Deseas continuar?"
            );

        if (!confirmado) {
            return;
        }

        await cambiarEstado(
            idUtileria,
            "DELETE"
        );
    }

    if (accion === "restaurar") {

        await cambiarEstado(
            idUtileria,
            "PATCH"
        );
    }
}

async function cambiarEstado(
    idUtileria,
    metodo
) {

    try {

        const csrf =
            await obtenerCsrf();

        const ruta =
            metodo === "PATCH"
                ? `/api/coordinador/utileria/${
                    encodeURIComponent(idUtileria)
                }/restaurar`
                : `/api/coordinador/utileria/${
                    encodeURIComponent(idUtileria)
                }`;

        const response = await fetch(
            ruta,
            {
                method: metodo,

                credentials:
                    "same-origin",

                headers: {
                    "Accept":
                        "application/json",

                    [csrf.headerName]:
                        csrf.token
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

        mostrarMensaje(
            metodo === "PATCH"
                ? "El registro fue restaurado."
                : "El registro fue eliminado.",
            "success"
        );

        await cargarUtileria();

    } catch (error) {

        mostrarMensaje(
            error.message,
            "danger"
        );
    }
}

async function obtenerCsrf() {

    const response = await fetch(
        "/api/csrf",
        {
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

async function obtenerMensajeError(
    response
) {

    const contenido =
        await response.text();

    if (!contenido) {
        return `Error HTTP ${response.status}`;
    }

    try {

        const error =
            JSON.parse(contenido);

        return error.detail
            ?? error.message
            ?? "No se pudo completar la operación.";

    } catch {
        return contenido;
    }
}

function obtenerClaseSituacion(
    situacion
) {

    const clases = {
        SUFICIENTE:
            "situacion-suficiente",

        BAJO_STOCK:
            "situacion-bajo",

        AGOTADO:
            "situacion-agotado",

        INACTIVO:
            "situacion-inactivo"
    };

    return clases[situacion]
        ?? "";
}

function formatearSituacion(
    situacion
) {

    const situaciones = {
        SUFICIENTE:
            "Stock suficiente",

        BAJO_STOCK:
            "Bajo stock",

        AGOTADO:
            "Agotado",

        INACTIVO:
            "Inactivo"
    };

    return situaciones[situacion]
        ?? situacion
        ?? "-";
}

function formatearTexto(
    valor
) {

    if (!valor) {
        return "-";
    }

    return valor
        .toLowerCase()
        .replaceAll("_", " ")
        .replace(
            /^\w/,
            letra => letra.toUpperCase()
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

function mostrarMensaje(
    texto,
    tipo
) {

    mensajePagina.textContent =
        texto;

    mensajePagina.className =
        `alert alert-${tipo} mt-3`;
}

function ocultarMensaje() {

    mensajePagina.classList.add(
        "d-none"
    );
}

function escaparHtml(
    valor
) {

    return String(
        valor ?? ""
    )
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}