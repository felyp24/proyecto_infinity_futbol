(() => {

    const navbar =
        document.querySelector(
            "[data-nav-app]"
        );

    if (!navbar) {
        return;
    }

    const botonMovil =
        navbar.querySelector(
            "[data-nav-movil]"
        );

    const menu =
        navbar.querySelector(
            "[data-nav-menu]"
        );

    const desplegables =
        navbar.querySelectorAll(
            ".nav-app-desplegable"
        );

    function cerrarDesplegables(
        excepcion = null
    ) {
        desplegables.forEach(
            desplegable => {

                if (
                    excepcion
                    && desplegable === excepcion
                ) {
                    return;
                }

                desplegable.classList.remove(
                    "nav-app-abierto"
                );

                const boton =
                    desplegable.querySelector(
                        ".nav-app-boton-desplegable"
                    );

                boton?.setAttribute(
                    "aria-expanded",
                    "false"
                );
            }
        );
    }

    desplegables.forEach(
        desplegable => {

            const boton =
                desplegable.querySelector(
                    ".nav-app-boton-desplegable"
                );

            if (!boton) {
                return;
            }

            boton.addEventListener(
                "click",
                event => {

                    event.stopPropagation();

                    const estabaAbierto =
                        desplegable.classList
                            .contains(
                                "nav-app-abierto"
                            );

                    cerrarDesplegables(
                        desplegable
                    );

                    desplegable.classList.toggle(
                        "nav-app-abierto",
                        !estabaAbierto
                    );

                    boton.setAttribute(
                        "aria-expanded",
                        String(!estabaAbierto)
                    );
                }
            );
        }
    );

    botonMovil?.addEventListener(
        "click",
        () => {

            const visible =
                menu.classList.toggle(
                    "nav-app-menu-visible"
                );

            botonMovil.setAttribute(
                "aria-expanded",
                String(visible)
            );
        }
    );

    navbar.querySelectorAll(
        ".nav-app-submenu a"
    ).forEach(
        enlace => {

            enlace.addEventListener(
                "click",
                () => {

                    cerrarDesplegables();

                    menu.classList.remove(
                        "nav-app-menu-visible"
                    );
                }
            );
        }
    );

    document.addEventListener(
        "click",
        event => {

            if (!navbar.contains(event.target)) {
                cerrarDesplegables();
            }
        }
    );

    document.addEventListener(
        "keydown",
        event => {

            if (event.key === "Escape") {
                cerrarDesplegables();

                menu.classList.remove(
                    "nav-app-menu-visible"
                );
            }
        }
    );

})();