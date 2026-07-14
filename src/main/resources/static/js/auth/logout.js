document.addEventListener(
    "DOMContentLoaded",
    configurarBotonLogout
);

function configurarBotonLogout() {
    const botonLogout =
        document.getElementById("botonLogout");

    if (!botonLogout) {
        return;
    }

    botonLogout.addEventListener(
        "click",
        cerrarSesion
    );
}

async function cerrarSesion() {
    const botonLogout =
        document.getElementById("botonLogout");

    botonLogout.disabled = true;
    botonLogout.textContent = "Cerrando...";

    try {
        const csrfData = await obtenerCsrfLogout();

        const response = await fetch(
            "/api/auth/logout",
            {
                method: "POST",
                headers: {
                    [csrfData.headerName]: csrfData.token
                }
            }
        );

        if (!response.ok) {
            throw new Error(
                "No se pudo cerrar la sesión."
            );
        }

        const resultado = await response.json();

        window.location.href =
            resultado.rutaDestino
            || "/login?logout=exitoso";

    } catch (error) {
        console.error(error);

        botonLogout.disabled = false;
        botonLogout.textContent = "Cerrar sesión";

        alert(
            error.message
            || "No se pudo cerrar la sesión."
        );
    }
}

async function obtenerCsrfLogout() {
    const response = await fetch("/api/csrf");

    if (!response.ok) {
        throw new Error(
            "No se pudo obtener el token de seguridad."
        );
    }

    return response.json();
}