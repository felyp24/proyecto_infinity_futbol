const formularioCupon =
    document.getElementById(
        "formularioCupon"
    );

const codigo =
    document.getElementById(
        "codigo"
    );

const porcentajeDescuento =
    document.getElementById(
        "porcentajeDescuento"
    );

const fechaInicio =
    document.getElementById(
        "fechaInicio"
    );

const fechaExpiracion =
    document.getElementById(
        "fechaExpiracion"
    );

const botonGenerarCodigo =
    document.getElementById(
        "botonGenerarCodigo"
    );

const botonCrearCupon =
    document.getElementById(
        "botonCrearCupon"
    );

const formularioBusqueda =
    document.getElementById(
        "formularioBusqueda"
    );

const textoBusqueda =
    document.getElementById(
        "textoBusqueda"
    );

const tablaCupones =
    document.getElementById(
        "tablaCupones"
    );

const mensajePagina =
    document.getElementById(
        "mensajePagina"
    );

document.addEventListener(
    "DOMContentLoaded",
    iniciarPagina
);

async function iniciarPagina() {

    asignarFechasIniciales();

    codigo.addEventListener(
        "input",
        () => {
            codigo.value =
                codigo.value
                    .toUpperCase()
                    .replace(
                        /[^A-Z0-9-]/g,
                        ""
                    );
        }
    );

    botonGenerarCodigo.addEventListener(
        "click",
        generarCodigoVisual
    );

    formularioCupon.addEventListener(
        "submit",
        crearCupon
    );

    formularioBusqueda.addEventListener(
        "submit",
        async event => {

            event.preventDefault();

            await cargarCupones();
        }
    );

    tablaCupones.addEventListener(
        "click",
        cambiarEstadoDesdeTabla
    );

    await cargarCupones();
}

function asignarFechasIniciales() {

    const hoy =
        new Date();

    const expiracion =
        new Date();

    expiracion.setDate(
        expiracion.getDate() + 30
    );

    fechaInicio.value =
        convertirFechaInput(hoy);

    fechaExpiracion.value =
        convertirFechaInput(expiracion);
}

function generarCodigoVisual() {

    const caracteres =
        "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";

    let aleatorio = "";

    for (let indice = 0; indice < 8; indice++) {

        aleatorio +=
            caracteres.charAt(
                Math.floor(
                    Math.random()
                    * caracteres.length
                )
            );
    }

    codigo.value =
        `INF-${aleatorio}`;
}

async function crearCupon(
    event
) {
    event.preventDefault();

    botonCrearCupon.disabled = true;
    botonCrearCupon.textContent =
        "Creando...";

    try {

        const csrf =
            await obtenerCsrf();

        const response = await fetch(
            "/api/admin/cupones",
            {
                method: "POST",
                credentials: "same-origin",

                headers: {
                    "Content-Type":
                        "application/json",

                    "Accept":
                        "application/json",

                    [csrf.headerName]:
                        csrf.token
                },

                body: JSON.stringify({
                    codigo:
                        codigo.value.trim()
                        || null,

                    porcentajeDescuento:
                        Number(
                            porcentajeDescuento.value
                        ),

                    fechaInicio:
                        fechaInicio.value,

                    fechaExpiracion:
                        fechaExpiracion.value
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

        const cupon =
            await response.json();

        mostrarMensaje(
            `Cupón ${cupon.codigo} creado correctamente.`,
            "success"
        );

        formularioCupon.reset();
        asignarFechasIniciales();

        await cargarCupones();

    } catch (error) {

        mostrarMensaje(
            error.message,
            "danger"
        );

    } finally {

        botonCrearCupon.disabled = false;
        botonCrearCupon.textContent =
            "Crear cupón";
    }
}

async function cargarCupones() {

    const parametros =
        new URLSearchParams();

    const texto =
        textoBusqueda.value.trim();

    if (texto) {
        parametros.set(
            "texto",
            texto
        );
    }

    try {

        const response = await fetch(
            `/api/admin/cupones?${
                parametros.toString()
            }`,
            {
                credentials: "same-origin",
                cache: "no-store"
            }
        );

        if (!response.ok) {

            throw new Error(
                await obtenerMensajeError(
                    response
                )
            );
        }

        const cupones =
            await response.json();

        mostrarCupones(cupones);

    } catch (error) {

        tablaCupones.innerHTML = `
            <tr>
                <td
                        colspan="7"
                        class="text-center text-danger py-4"
                >
                    ${escaparHtml(error.message)}
                </td>
            </tr>
        `;
    }
}

function mostrarCupones(
    cupones
) {

    if (
        !Array.isArray(cupones)
        || cupones.length === 0
    ) {
        tablaCupones.innerHTML = `
            <tr>
                <td
                        colspan="7"
                        class="text-center text-muted py-4"
                >
                    No se encontraron cupones.
                </td>
            </tr>
        `;

        return;
    }

    tablaCupones.innerHTML =
        cupones
            .map(cupon => {

                const nuevoEstado =
                    cupon.estado === "ACTIVO"
                        ? "INACTIVO"
                        : "ACTIVO";

                return `
                    <tr>

                        <td class="codigo-cupon">
                            ${escaparHtml(
                                cupon.codigo
                            )}
                        </td>

                        <td>
                            ${Number(
                                cupon.porcentajeDescuento
                            ).toFixed(2)}%
                        </td>

                        <td>
                            ${formatearFecha(
                                cupon.fechaInicio
                            )}
                        </td>

                        <td>
                            ${formatearFecha(
                                cupon.fechaExpiracion
                            )}
                        </td>

                        <td>
                            <span class="
                                situacion
                                ${obtenerClaseSituacion(
                                    cupon.situacion
                                )}
                            ">
                                ${formatearSituacion(
                                    cupon.situacion
                                )}
                            </span>
                        </td>

                        <td>
                            ${cupon.estado === "ACTIVO"
                                ? "Activo"
                                : "Inactivo"
                            }
                        </td>

                        <td class="text-end">

                            <button
                                    type="button"
                                    class="btn
                                           btn-sm
                                           ${cupon.estado === "ACTIVO"
                                                ? "btn-outline-danger"
                                                : "btn-outline-success"
                                           }
                                           boton-estado"
                                    data-id-cupon="${
                                        escaparHtml(
                                            cupon.idCupon
                                        )
                                    }"
                                    data-nuevo-estado="${
                                        nuevoEstado
                                    }"
                            >
                                ${cupon.estado === "ACTIVO"
                                    ? "Desactivar"
                                    : "Activar"
                                }
                            </button>

                        </td>

                    </tr>
                `;
            })
            .join("");
}

async function cambiarEstadoDesdeTabla(
    event
) {

    const boton =
        event.target.closest(
            ".boton-estado"
        );

    if (!boton) {
        return;
    }

    boton.disabled = true;

    try {

        const csrf =
            await obtenerCsrf();

        const response = await fetch(
            `/api/admin/cupones/${
                encodeURIComponent(
                    boton.dataset.idCupon
                )
            }/estado`,
            {
                method: "PATCH",
                credentials: "same-origin",

                headers: {
                    "Content-Type":
                        "application/json",

                    "Accept":
                        "application/json",

                    [csrf.headerName]:
                        csrf.token
                },

                body: JSON.stringify({
                    estado:
                        boton.dataset.nuevoEstado
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

        mostrarMensaje(
            "Estado del cupón actualizado.",
            "success"
        );

        await cargarCupones();

    } catch (error) {

        mostrarMensaje(
            error.message,
            "danger"
        );

    } finally {

        boton.disabled = false;
    }
}

async function obtenerCsrf() {

    const response = await fetch(
        "/api/csrf",
        {
            credentials: "same-origin",
            cache: "no-store"
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

    let contenido = null;

    try {
        contenido =
            await response.json();
    } catch {
        contenido = null;
    }

    return contenido?.detail
        ?? contenido?.message
        ?? `No se pudo completar la operación. Código HTTP: ${response.status}`;
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

function obtenerClaseSituacion(
    situacion
) {

    const clases = {
        VIGENTE:
            "situacion-vigente",

        VENCIDO:
            "situacion-vencido",

        INACTIVO:
            "situacion-inactivo",

        PROXIMAMENTE:
            "situacion-proximamente"
    };

    return clases[situacion]
        ?? "";
}

function formatearSituacion(
    situacion
) {

    const situaciones = {
        VIGENTE:
            "Vigente",

        VENCIDO:
            "Vencido",

        INACTIVO:
            "Inactivo",

        PROXIMAMENTE:
            "Próximamente"
    };

    return situaciones[situacion]
        ?? situacion;
}

function formatearFecha(
    valor
) {

    if (!valor) {
        return "-";
    }

    return new Intl.DateTimeFormat(
        "es-PE",
        {
            dateStyle: "short"
        }
    ).format(
        new Date(
            `${valor}T00:00:00`
        )
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
        ).padStart(2, "0");

    const dia =
        String(
            fecha.getDate()
        ).padStart(2, "0");

    return `${anio}-${mes}-${dia}`;
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