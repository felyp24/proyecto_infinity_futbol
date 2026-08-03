document.addEventListener(
    "DOMContentLoaded",
    iniciarPaginaCreditos
);

const saldoActualCreditos =
    document.getElementById(
        "saldoActualCreditos"
    );

const contenedorPaquetes =
    document.getElementById(
        "contenedorPaquetes"
    );

const mensajePagina =
    document.getElementById(
        "mensajePagina"
    );

const tablaPagos =
    document.getElementById(
        "tablaPagos"
    );

const btnActualizarPagos =
    document.getElementById(
        "btnActualizarPagos"
    );

const CLAVE_PAGO_PENDIENTE =
    "infinityFutbolPagoPendiente";

const codigoCupon =
    document.getElementById(
        "codigoCupon"
    );

const botonValidarCupon =
    document.getElementById(
        "botonValidarCupon"
    );

const resultadoCupon =
    document.getElementById(
        "resultadoCupon"
    );

let cuponValidado = null;

let paquetesCargados = [];

let modalBoleta;

async function iniciarPaginaCreditos() {

    modalBoleta =
        new bootstrap.Modal(
            document.getElementById(
                "modalBoleta"
            )
        );

    contenedorPaquetes.addEventListener(
        "click",
        manejarClickPaquete
    );

    tablaPagos.addEventListener(
        "click",
        manejarClickPago
    );

    btnActualizarPagos.addEventListener(
        "click",
        cargarPagos
    );

    mostrarResultadoRetorno();

    /*
     * Revisa automáticamente la operación que
     * envió al usuario a Mercado Pago.
     */
    await confirmarPagoPendienteAutomaticamente();

    await Promise.all([
        cargarSaldoActual(),
        cargarPaquetes(),
        cargarPagos()
    ]);

    botonValidarCupon.addEventListener(
        "click",
        validarCuponIngresado
    );

    codigoCupon.addEventListener(
        "input",
        () => {

            codigoCupon.value =
                codigoCupon.value
                    .toUpperCase()
                    .replace(
                        /[^A-Z0-9-]/g,
                        ""
                    );

            cuponValidado = null;

            resultadoCupon.classList.add(
                "d-none"
            );
        }
    );
}

/*
   SALDO ACTUAL
*/

async function cargarSaldoActual() {

    try {

        const response = await fetch(
            "/api/inicio/resumen",
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

        const resumen =
            await response.json();

        saldoActualCreditos.textContent =
            resumen.saldoCreditos ?? 0;

    } catch (error) {

        saldoActualCreditos.textContent =
            "No disponible";

        mostrarMensaje(
            error.message,
            "danger"
        );
    }
}


/*
   PAQUETES
*/

async function cargarPaquetes() {

    try {

        const response = await fetch(
            "/api/inicio/creditos/paquetes",
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

        paquetesCargados =
            await response.json();

        mostrarPaquetes(
            paquetesCargados
        );

    } catch (error) {

        contenedorPaquetes.innerHTML = `
            <div class="col-12">
                <div class="alert alert-danger">
                    ${escaparHtml(error.message)}
                </div>
            </div>
        `;
    }
}

function mostrarPaquetes(
    paquetes
) {

    if (!Array.isArray(paquetes)
            || paquetes.length === 0) {

        contenedorPaquetes.innerHTML = `
            <div class="col-12">
                <div class="alert alert-info">
                    No existen paquetes de créditos disponibles.
                </div>
            </div>
        `;

        return;
    }

    contenedorPaquetes.innerHTML =
        paquetes
            .map(paquete => `
                <div class="col-md-6 col-lg-4">

                    <article class="tarjeta-paquete">

                        <div class="contenido-paquete">

                            <h3 class="nombre-paquete">
                                ${escaparHtml(
                                    paquete.nombre
                                )}
                            </h3>

                            <div class="cantidad-creditos">
                                ${paquete.cantidadCreditos}
                            </div>

                            <div class="texto-creditos">
                                créditos
                            </div>

                            <div class="precio-paquete">
                                ${formatearMoneda(
                                    paquete.precio
                                )}
                            </div>

                            <div class="vigencia-paquete">
                                Vigencia:
                                ${paquete.diasVigencia}
                                días
                            </div>

                            <button
                                    type="button"
                                    class="btn
                                           btn-success
                                           boton-comprar
                                           btn-comprar-paquete"
                                    data-id-paquete="${
                                        escaparHtml(
                                            paquete
                                                .idPaqueteCredito
                                        )
                                    }"
                            >
                                Comprar paquete
                            </button>

                        </div>

                    </article>

                </div>
            `)
            .join("");
}


/*
   INICIAR COMPRA
*/

async function manejarClickPaquete(
    event
) {

    const boton =
        event.target.closest(
            ".btn-comprar-paquete"
        );

    if (!boton) {
        return;
    }

    const idPaquete =
        boton.dataset.idPaquete;

    const paquete =
        paquetesCargados.find(
            item =>
                item.idPaqueteCredito
                === idPaquete
        );

    if (!paquete) {

        mostrarMensaje(
            "No se encontró el paquete seleccionado.",
            "danger"
        );

        return;
    }

    try {

        const codigoIngresado =
            codigoCupon.value.trim();

        let informacionCupon = null;

        /*
         * Se vuelve a validar antes de comprar.
         * No se confía solamente en la validación anterior.
         */
        if (codigoIngresado) {

            informacionCupon =
                await consultarCupon(
                    codigoIngresado
                );

            cuponValidado =
                informacionCupon;

            mostrarResultadoCupon(
                informacionCupon
            );
        }

        const montoBruto =
            Number(paquete.precio);

        const porcentaje =
            Number(
                informacionCupon
                    ?.porcentajeDescuento
                ?? 0
            );

        const montoDescuento =
            redondearMoneda(
                montoBruto
                * porcentaje
                / 100
            );

        const montoTotal =
            redondearMoneda(
                montoBruto
                - montoDescuento
            );

        let mensajeConfirmacion =
            `Vas a comprar ${paquete.cantidadCreditos} `
            + `créditos por ${formatearMoneda(montoTotal)}.`;

        if (informacionCupon) {

            mensajeConfirmacion =
                `Precio original: ${
                    formatearMoneda(montoBruto)
                }\n`
                + `Cupón: ${
                    informacionCupon.codigo
                } (${porcentaje}%)\n`
                + `Descuento: ${
                    formatearMoneda(montoDescuento)
                }\n`
                + `Total: ${
                    formatearMoneda(montoTotal)
                }`;
        }

        const confirmado =
            window.confirm(
                mensajeConfirmacion
                + "\n\n¿Deseas continuar?"
            );

        if (!confirmado) {
            return;
        }

        await crearPreferenciaPago(
            idPaquete,
            informacionCupon?.codigo
                ?? null,
            boton
        );

    } catch (error) {

        mostrarResultadoCuponError(
            error.message
        );

        mostrarMensaje(
            error.message,
            "danger"
        );
    }
}

async function crearPreferenciaPago(
    idPaquete,
    codigoCuponAplicado,
    boton
) {

    ocultarMensaje();

    bloquearBoton(
        boton,
        true
    );

    try {

        const csrf =
            await obtenerCsrf();

        const response = await fetch(
            "/api/inicio/creditos/preferencias",
            {
                method: "POST",

                credentials: "same-origin",

                headers: {
                    "Content-Type":
                        "application/json",

                    [csrf.headerName]:
                        csrf.token
                },

                body: JSON.stringify({
                    idPaqueteCredito:
                        idPaquete
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

        const resultado =
            await response.json();

        if (!resultado.urlCheckout) {
            throw new Error(
                "Mercado Pago no devolvió "
                + "la dirección del checkout."
            );
        }

        /*
         * Conservamos el identificador local.
         *
         * Si trabajamos en localhost y Mercado Pago
         * no puede redirigir automáticamente, al volver
         * manualmente la página confirmará el pago.
         */
        localStorage.setItem(
            CLAVE_PAGO_PENDIENTE,
            resultado.idPago
        );

        boton.textContent =
            "Redirigiendo...";

        window.location.assign(
            resultado.urlCheckout
        );

    } catch (error) {

        mostrarMensaje(
            error.message,
            "danger"
        );

        bloquearBoton(
            boton,
            false
        );
    }
}

/*
   HISTORIAL DE PAGOS
*/

async function cargarPagos() {

    try {

        const response = await fetch(
            "/api/inicio/creditos/pagos",
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

        const pagos =
            await response.json();

        mostrarPagos(pagos);

    } catch (error) {

        tablaPagos.innerHTML = `
            <tr>
                <td
                        colspan="6"
                        class="text-center
                               text-danger
                               py-4"
                >
                    ${escaparHtml(error.message)}
                </td>
            </tr>
        `;
    }
}

function crearAccionesPago(
    pago
) {

    if (pago.acreditado) {
        return `
            <div class="acciones-pago">

                <span class="text-success fw-semibold">
                    Créditos acreditados
                </span>

                <button
                        type="button"
                        class="btn
                               btn-sm
                               btn-outline-success
                               btn-ver-boleta"
                        data-id-pago="${
                            escaparHtml(
                                pago.idPago
                            )
                        }"
                >
                    Ver boleta
                </button>

            </div>
        `;
    }

    if (pago.estadoPago === "PENDIENTE") {
        return `
            <div class="acciones-pago">

                <button
                        type="button"
                        class="btn
                               btn-sm
                               btn-success
                               btn-continuar-pago"
                        data-id-pago="${
                            escaparHtml(
                                pago.idPago
                            )
                        }"
                >
                    Continuar pago
                </button>

            </div>
        `;
    }

    if (
        pago.estadoPago === "EN_PROCESO"
        || pago.estadoPago === "AUTORIZADO"
    ) {
        return `
            <span class="text-muted">
                Procesando pago
            </span>
        `;
    }

    return `
        <span class="text-muted">
            Sin acciones
        </span>
    `;
}

function mostrarPagos(
    pagos
) {

    if (!Array.isArray(pagos)
            || pagos.length === 0) {

        tablaPagos.innerHTML = `
            <tr>
                <td
                        colspan="6"
                        class="text-center
                               text-muted
                               py-4"
                >
                    Todavía no tienes pagos registrados.
                </td>
            </tr>
        `;

        return;
    }

    tablaPagos.innerHTML =
        pagos
            .map(pago => `
                <tr>

                    <td>
                        ${formatearFechaHora(
                            pago.fechaPago
                        )}

                        <span class="identificador-pago">
                            ${escaparHtml(
                                pago.idPago
                            )}
                        </span>
                    </td>

                    <td>
                        <strong>
                            ${escaparHtml(
                                pago.nombrePaquete
                            )}
                        </strong>

                        <span class="detalle-pago">
                            ${pago.cantidadCreditos}
                            créditos
                        </span>
                    </td>

                    <td>
                        ${formatearMoneda(
                            pago.montoTotal
                        )}
                    </td>

                    <td>
                        <span class="
                            estado-pago
                            ${obtenerClaseEstadoPago(
                                pago.estadoPago
                            )}
                        ">
                            ${formatearEstadoPago(
                                pago.estadoPago
                            )}
                        </span>

                        ${
                            pago.estadoDetalle
                                ? `
                                    <span class="detalle-pago">
                                        ${escaparHtml(
                                            pago.estadoDetalle
                                        )}
                                    </span>
                                `
                                : ""
                        }
                    </td>

                    <td>
                        ${
                            pago.fechaExpiracion
                                ? formatearFecha(
                                    pago.fechaExpiracion
                                )
                                : pago.acreditado
                                    ? "Sin fecha registrada"
                                    : "-"
                        }
                    </td>

                    <td class="text-end">
                        ${crearAccionesPago(pago)}
                    </td>

                </tr>
            `)
            .join("");
}


/*
   FUNCIONES AUXILIARES
*/

async function obtenerCsrf() {

    const response = await fetch(
        "/api/csrf",
        {
            credentials: "same-origin"
        }
    );

    if (!response.ok) {
        throw new Error(
            "No se pudo obtener el token de seguridad."
        );
    }

    return response.json();
}

function bloquearBoton(
    boton,
    bloquear
) {

    boton.disabled =
        bloquear;

    boton.textContent =
        bloquear
            ? "Preparando pago..."
            : "Comprar paquete";
}

function formatearMoneda(
    valor
) {

    return new Intl.NumberFormat(
        "es-PE",
        {
            style: "currency",
            currency: "PEN",
            minimumFractionDigits: 2
        }
    ).format(
        Number(valor ?? 0)
    );
}

async function obtenerMensajeError(
    response
) {

    let respuesta = null;

    try {
        respuesta =
            await response.json();
    } catch {
        respuesta = null;
    }

    if (response.status === 401) {
        return respuesta?.detail
            ?? "Debes iniciar sesión nuevamente.";
    }

    if (response.status === 403) {
        return respuesta?.detail
            ?? "No tienes permiso para realizar esta operación.";
    }

    if (response.status === 404) {
        return respuesta?.detail
            ?? "No se encontró el recurso solicitado.";
    }

    if (response.status === 502) {
        return respuesta?.detail
            ?? "No fue posible comunicarse con Mercado Pago.";
    }

    return respuesta?.detail
        ?? respuesta?.message
        ?? "No se pudo iniciar el proceso de pago.";
}

function mostrarMensaje(
    texto,
    tipo
) {

    mensajePagina.textContent =
        texto;

    mensajePagina.className =
        `alert alert-${tipo}`;

    mensajePagina.scrollIntoView({
        behavior: "smooth",
        block: "center"
    });
}

function ocultarMensaje() {

    mensajePagina.textContent = "";

    mensajePagina.className =
        "alert d-none";
}

function escaparHtml(
    valor
) {

    const texto =
        valor == null
            ? ""
            : String(valor);

    return texto
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}

async function manejarClickPago(
    event
) {

    const botonBoleta =
        event.target.closest(
            ".btn-ver-boleta"
        );

    if (botonBoleta) {

        await mostrarBoleta(
            botonBoleta.dataset.idPago,
            botonBoleta
        );

        return;
    }

    const botonContinuar =
        event.target.closest(
            ".btn-continuar-pago"
        );

    if (!botonContinuar) {
        return;
    }

    await continuarPago(
        botonContinuar
                .dataset
                .idPago,

        botonContinuar
    );
}

async function continuarPago(
    idPago,
    boton
) {

    if (!idPago) {
        return;
    }

    ocultarMensaje();

    boton.disabled = true;

    boton.textContent =
        "Preparando...";

    try {

        const csrf =
            await obtenerCsrf();

        const response = await fetch(
            `/api/inicio/creditos/pagos/${idPago}/continuar`,
            {
                method: "POST",

                credentials: "same-origin",

                headers: {
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

        const resultado =
            await response.json();

        if (!resultado.urlCheckout) {
            throw new Error(
                "Mercado Pago no devolvió la dirección del checkout."
            );
        }

        localStorage.setItem(
            CLAVE_PAGO_PENDIENTE,
            resultado.idPago
        );

        boton.textContent =
            "Redirigiendo...";

        window.location.assign(
            resultado.urlCheckout
        );

    } catch (error) {

        await Promise.all([
            cargarSaldoActual(),
            cargarPagos()
        ]);

        mostrarMensaje(
            error.message,
            "warning"
        );
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

    return new Intl.DateTimeFormat(
        "es-PE",
        {
            dateStyle: "short",
            timeStyle: "short"
        }
    ).format(fecha);
}

function formatearFecha(
    valor
) {

    if (!valor) {
        return "-";
    }

    /*
     * Se agrega una hora intermedia para evitar
     * cambios de día por la zona horaria.
     */
    const fecha =
        new Date(`${valor}T12:00:00`);

    return new Intl.DateTimeFormat(
        "es-PE",
        {
            dateStyle: "medium"
        }
    ).format(fecha);
}

function formatearEstadoPago(
    estado
) {

    const nombres = {
        PENDIENTE: "Pendiente",
        EN_PROCESO: "En proceso",
        APROBADO: "Aprobado",
        AUTORIZADO: "Autorizado",
        RECHAZADO: "Rechazado",
        CANCELADO: "Cancelado",
        REEMBOLSADO: "Reembolsado",
        CONTRACARGO: "Contracargo",
        EN_MEDIACION: "En mediación"
    };

    return nombres[estado]
        ?? estado
        ?? "Sin estado";
}

function obtenerClaseEstadoPago(
    estado
) {

    if (estado === "APROBADO") {
        return "estado-pago-aprobado";
    }

    if (estado === "PENDIENTE") {
        return "estado-pago-pendiente";
    }

    if (
        estado === "EN_PROCESO"
        || estado === "AUTORIZADO"
    ) {
        return "estado-pago-proceso";
    }

    if (
        estado === "RECHAZADO"
        || estado === "CANCELADO"
        || estado === "CONTRACARGO"
    ) {
        return "estado-pago-rechazado";
    }

    return "estado-pago-neutral";
}

async function confirmarPagoPendienteAutomaticamente() {

    const idPago =
        localStorage.getItem(
            CLAVE_PAGO_PENDIENTE
        );

    if (!idPago) {
        return;
    }

    try {
        const csrf =
            await obtenerCsrf();

        const response = await fetch(
            `/api/inicio/creditos/pagos/${idPago}/confirmar`,
            {
                method: "POST",

                credentials: "same-origin",

                headers: {
                    [csrf.headerName]:
                        csrf.token
                }
            }
        );

        /*
         * 409 significa normalmente que el usuario
         * todavía no terminó el pago.
         *
         * No mostramos error ni eliminamos el ID,
         * para poder comprobarlo nuevamente al volver.
         */
        if (response.status === 409) {
            return;
        }

        if (!response.ok) {
            throw new Error(
                await obtenerMensajeError(
                    response
                )
            );
        }

        const resultado =
            await response.json();

        if (
                resultado.estadoPago
                        === "APROBADO"
        ) {
            localStorage.removeItem(
                CLAVE_PAGO_PENDIENTE
            );

            mostrarMensaje(
                resultado.creditosAcreditados > 0
                    ? resultado.mensaje
                    : "El pago fue aprobado y los créditos ya se encuentran acreditados.",
                "success"
            );

            return;
        }

        const estadosFinales = [
            "RECHAZADO",
            "CANCELADO",
            "REEMBOLSADO",
            "CONTRACARGO"
        ];

        if (
                estadosFinales.includes(
                    resultado.estadoPago
                )
        ) {
            localStorage.removeItem(
                CLAVE_PAGO_PENDIENTE
            );
        }

    } catch (error) {

        /*
         * No bloqueamos la carga de la página si
         * Mercado Pago no responde temporalmente.
         */
        console.warn(
            "No se pudo sincronizar automáticamente el pago:",
            error
        );
    }
}

function mostrarResultadoRetorno() {

    const parametros =
        new URLSearchParams(
            window.location.search
        );

    const resultado =
        parametros.get(
            "resultado"
        );

    if (!resultado) {
        return;
    }

    if (resultado === "aprobado") {
        mostrarMensaje(
            "El pago fue aprobado y los créditos fueron procesados.",
            "success"
        );
    }

    if (resultado === "pendiente") {
        mostrarMensaje(
            "El pago todavía está pendiente de confirmación.",
            "warning"
        );
    }

    if (resultado === "fallido") {
        mostrarMensaje(
            "El pago no pudo completarse. Puedes continuar el intento o iniciar otra compra.",
            "danger"
        );
    }

    if (resultado === "error") {
        mostrarMensaje(
            "El pago terminó, pero no se pudo actualizar la operación en este momento.",
            "warning"
        );
    }

    /*
     * Limpia el parámetro para que el mensaje no
     * vuelva a mostrarse al actualizar la página.
     */
    window.history.replaceState(
        {},
        document.title,
        "/inicio/creditos"
    );
}

async function mostrarBoleta(
    idPago,
    boton
) {

    if (!idPago) {
        return;
    }

    const textoOriginal =
        boton.textContent;

    boton.disabled = true;
    boton.textContent =
        "Cargando...";

    try {

        const response = await fetch(
            `/api/inicio/creditos/pagos/${idPago}/boleta`,
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

        const boleta =
            await response.json();

        llenarBoleta(boleta);

        modalBoleta.show();

    } catch (error) {

        mostrarMensaje(
            error.message,
            "danger"
        );

    } finally {

        boton.disabled = false;

        boton.textContent =
            textoOriginal;
    }
}

function llenarBoleta(
    boleta
) {

    document.getElementById(
        "boletaNumero"
    ).textContent =
        `${boleta.serie}-${boleta.numero}`;

    document.getElementById(
        "boletaFecha"
    ).textContent =
        formatearFechaHora(
            boleta.fechaEmision
        );

    document.getElementById(
        "boletaCliente"
    ).textContent =
        boleta.nombreCliente ?? "-";

    document.getElementById(
        "boletaDocumento"
    ).textContent =
        `${formatearTipoDocumento(
            boleta.tipoDocumento
        )}: ${boleta.numeroDocumento ?? "-"}`;

    document.getElementById(
        "boletaCorreo"
    ).textContent =
        boleta.correo ?? "-";

    document.getElementById(
        "boletaIdPago"
    ).textContent =
        boleta.idPago ?? "-";

    document.getElementById(
        "boletaMetodo"
    ).textContent =
        formatearMetodoPago(
            boleta.metodoPago
        );

    document.getElementById(
        "boletaPaquete"
    ).textContent =
        boleta.nombrePaquete ?? "-";

    document.getElementById(
        "boletaCreditos"
    ).textContent =
        boleta.cantidadCreditos ?? 0;

    const monto =
        formatearMoneda(
            boleta.montoTotal
        );

    document.getElementById(
        "boletaImporte"
    ).textContent =
        monto;

    document.getElementById(
        "boletaTotal"
    ).textContent =
        monto;
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
        ?? "Documento";
}

function formatearMetodoPago(
    metodoPago
) {

    if (!metodoPago) {
        return "-";
    }

    return String(metodoPago)
        .replaceAll("_", " ")
        .toUpperCase();
}

async function validarCuponIngresado() {

    const codigoIngresado =
        codigoCupon.value.trim();

    if (!codigoIngresado) {

        mostrarResultadoCuponError(
            "Debe ingresar un código de descuento."
        );

        return;
    }

    botonValidarCupon.disabled = true;
    botonValidarCupon.textContent =
        "Validando...";

    try {

        cuponValidado =
            await consultarCupon(
                codigoIngresado
            );

        mostrarResultadoCupon(
            cuponValidado
        );

    } catch (error) {

        cuponValidado = null;

        mostrarResultadoCuponError(
            error.message
        );

    } finally {

        botonValidarCupon.disabled = false;
        botonValidarCupon.textContent =
            "Validar cupón";
    }
}

async function consultarCupon(
    codigoIngresado
) {

    const parametros =
        new URLSearchParams({
            codigo:
                codigoIngresado
        });

    const response = await fetch(
        `/api/inicio/creditos/cupones/validar?${
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

    return response.json();
}

function mostrarResultadoCupon(
    cupon
) {

    resultadoCupon.textContent =
        `${cupon.mensaje}. Válido hasta ${
            formatearFecha(
                cupon.fechaExpiracion
            )
        }.`;

    resultadoCupon.className =
        "resultado-cupon-valido mt-2";
}

function mostrarResultadoCuponError(
    mensaje
) {

    resultadoCupon.textContent =
        mensaje;

    resultadoCupon.className =
        "resultado-cupon-error mt-2";
}

function redondearMoneda(
    valor
) {

    return Math.round(
        (
            Number(valor)
            + Number.EPSILON
        )
        * 100
    ) / 100;
}