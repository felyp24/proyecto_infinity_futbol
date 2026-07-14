const tamanioPagina = 10;

let paginaActual = 0;
let totalPaginas = 0;
let textoBusquedaActual = "";

document.addEventListener(
    "DOMContentLoaded",
    iniciarPagina
);

function iniciarPagina() {
    const formularioBusqueda =
        document.getElementById("formularioBusqueda");

    const botonLimpiar =
        document.getElementById("botonLimpiar");

    formularioBusqueda.addEventListener(
        "submit",
        buscarAlumnos
    );

    botonLimpiar.addEventListener(
        "click",
        limpiarBusqueda
    );

    cargarAlumnos(0);
}
function buscarAlumnos(event) {
    event.preventDefault();

    const campoBusqueda =
        document.getElementById("textoBusqueda");

    textoBusquedaActual =
        campoBusqueda.value.trim();

    cargarAlumnos(0);
}

function limpiarBusqueda() {
    const campoBusqueda =
        document.getElementById("textoBusqueda");

    campoBusqueda.value = "";
    textoBusquedaActual = "";

    cargarAlumnos(0);
}

async function cargarAlumnos(numeroPagina) {
    const mensajeEstado =
        document.getElementById("mensajeEstado");

    const contenidoAlumnos =
        document.getElementById("contenidoAlumnos");

    mensajeEstado.textContent = "Cargando alumnos...";
    mensajeEstado.classList.remove(
        "oculto",
        "mensaje-error"
    );

    contenidoAlumnos.classList.add("oculto");

    try {
        const parametros =
            new URLSearchParams({
                texto: textoBusquedaActual,
                page: numeroPagina,
                size: tamanioPagina
            });

        const response = await fetch(
            `/api/admin/alumnos?${parametros.toString()}`
        );
        if (!response.ok) {
            throw new Error(
                `No se pudieron cargar los alumnos. Código: ${response.status}`
            );
        }

        const pagina = await response.json();

        paginaActual = pagina.number;
        totalPaginas = pagina.totalPages;

        mostrarAlumnos(pagina.content);
        actualizarResumen(pagina);
        actualizarPaginacion(pagina);

        mensajeEstado.classList.add("oculto");
        contenidoAlumnos.classList.remove("oculto");

    } catch (error) {
        console.error(error);

        mensajeEstado.textContent =
            "No se pudo cargar la lista de alumnos.";

        mensajeEstado.classList.add("mensaje-error");
    }
}

function mostrarAlumnos(alumnos) {
    const tablaAlumnos =
        document.getElementById("tablaAlumnos");

    tablaAlumnos.replaceChildren();

    if (alumnos.length === 0) {
        const fila = document.createElement("tr");
        const celda = document.createElement("td");

        celda.colSpan = 9;
        celda.textContent =
            textoBusquedaActual
                ? "No se encontraron alumnos con el criterio ingresado."
                : "No hay alumnos registrados.";

        fila.appendChild(celda);
        tablaAlumnos.appendChild(fila);

        return;
    }

    alumnos.forEach(alumno => {
        const fila = document.createElement("tr");

        fila.appendChild(
            crearCelda(alumno.nombres)
        );

        fila.appendChild(
            crearCelda(alumno.apellidos)
        );

        fila.appendChild(
            crearCelda(
                `${alumno.tipoDocumento} - ${alumno.numeroDocumento}`
            )
        );

        fila.appendChild(
            crearCelda(alumno.correo)
        );

        fila.appendChild(
            crearCelda(alumno.telefono)
        );

        fila.appendChild(
            crearCeldaEstado(alumno.estadoAlumno)
        );

        fila.appendChild(
            crearCeldaEstado(alumno.estadoUsuario)
        );

        fila.appendChild(
            crearCelda(
                formatearFecha(alumno.fechaRegistro)
            )
        );

        fila.appendChild(
            crearCeldaAcciones(alumno)
        );

        tablaAlumnos.appendChild(fila);
    });
}

function crearCelda(texto) {
    const celda = document.createElement("td");

    celda.textContent = texto || "—";

    return celda;
}

function crearCeldaEstado(estado) {
    const celda = document.createElement("td");
    const etiqueta = document.createElement("span");

    etiqueta.textContent = estado || "—";
    etiqueta.classList.add("estado");

    if (estado === "ACTIVO") {
        etiqueta.classList.add("estado-activo");
    } else {
        etiqueta.classList.add("estado-inactivo");
    }

    celda.appendChild(etiqueta);

    return celda;
}

function crearCeldaAcciones(alumno) {
    const celda = document.createElement("td");
    const enlaceEditar = document.createElement("a");

    enlaceEditar.textContent = "Editar";

    enlaceEditar.href =
        `/admin/alumnos/${encodeURIComponent(alumno.idAlumno)}/editar`;

    enlaceEditar.classList.add(
        "boton-editar"
    );

    celda.appendChild(enlaceEditar);

    return celda;
}

function actualizarResumen(pagina) {
    const resumen =
        document.getElementById("resumen");

    if (textoBusquedaActual) {
        resumen.textContent =
            `${pagina.totalElements} resultado(s) para “${textoBusquedaActual}”.`;

        return;
    }

    resumen.textContent =
        `Total de alumnos registrados: ${pagina.totalElements}`;
}

function actualizarPaginacion(pagina) {
    const informacionPagina =
        document.getElementById("informacionPagina");

    const botonAnterior =
        document.getElementById("botonAnterior");

    const botonSiguiente =
        document.getElementById("botonSiguiente");

    const numeroVisual =
        pagina.totalPages === 0
            ? 0
            : pagina.number + 1;

    informacionPagina.textContent =
        `Página ${numeroVisual} de ${pagina.totalPages}`;

    botonAnterior.disabled = pagina.first;

    botonSiguiente.disabled =
        pagina.last || pagina.totalPages === 0;

    botonAnterior.onclick = () => {
        if (paginaActual > 0) {
            cargarAlumnos(paginaActual - 1);
        }
    };

    botonSiguiente.onclick = () => {
        if (paginaActual + 1 < totalPaginas) {
            cargarAlumnos(paginaActual + 1);
        }
    };
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