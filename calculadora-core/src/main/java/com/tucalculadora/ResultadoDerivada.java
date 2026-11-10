package com.tucalculadora;

/**
 * Resultado inmutable de una derivación.
 *
 * <p>{@code error} es {@code null} cuando todo salió bien; en caso contrario
 * contiene el mensaje a mostrar. {@code fLaTeX} y {@code fPrimaLaTeX} son las
 * fórmulas en LaTeX de f(x) y f'(x); {@code valor} es f'(punto).</p>
 */
public record ResultadoDerivada(
        String expresion,
        String fLaTeX,
        String fPrimaLaTeX,
        double valor,
        String error
) {

    /** Devuelve {@code true} si la derivación falló. */
    public boolean hayError() {
        return error != null;
    }
}
