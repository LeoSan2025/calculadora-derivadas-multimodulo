package com.tucalculadora.nodos;

/**
 * Cada término se representa como: coeficiente * base ^ exponente
 */
public class Termino {
    public final NodoExpresion coeficiente;
    public final NodoExpresion base;
    public final NodoExpresion exponente;

    public Termino(NodoExpresion coeficiente, NodoExpresion base, NodoExpresion exponente) {
        this.coeficiente = coeficiente;
        this.base = base;
        this.exponente = exponente;
    }

    // Extrae un término de un nodo
    public static Termino extraer(NodoExpresion nodo) {
        // Caso: 3x² → NodoOperadorBinario('*', NodoConstante(3), NodoPotencia(x, 2))
        if (nodo instanceof NodoOperadorBinario bin) {
            if (bin.operador == '*') {
                NodoExpresion left = bin.izquierdo;
                NodoExpresion right = bin.derecho;

                // Caso: constante * potencia
                if (left instanceof NodoConstante && right instanceof NodoPotencia) {
                    return new Termino(left, ((NodoPotencia) right).base, ((NodoPotencia) right).exponente);
                }
                // Caso: constante * variable
                if (left instanceof NodoConstante && right instanceof NodoVariable) {
                    return new Termino(left, right, new NodoConstante(1));
                }
                // Caso: variable * constante (orden inverso)
                if (left instanceof NodoVariable && right instanceof NodoConstante) {
                    return new Termino(right, left, new NodoConstante(1));
                }
                // Caso: potencia * constante
                if (left instanceof NodoPotencia && right instanceof NodoConstante) {
                    return new Termino(right, ((NodoPotencia) left).base, ((NodoPotencia) left).exponente);
                }
            }
        }

        // Caso: x³ → NodoPotencia
        if (nodo instanceof NodoPotencia pot && pot.exponente instanceof NodoConstante) {
            return new Termino(new NodoConstante(1), pot.base, pot.exponente);
        }

        // Caso: x → NodoVariable
        if (nodo instanceof NodoVariable var) {
            return new Termino(new NodoConstante(1), var, new NodoConstante(1));
        }

        // Caso: constante → NodoConstante
        if (nodo instanceof NodoConstante c) {
            return new Termino(c, new NodoConstante(1), new NodoConstante(0));
        }

        return null;  // No es un término simplificable
    }

    // Compara si dos términos tienen la misma base y exponente
    public static boolean sonSemejantes(Termino t1, Termino t2) {
        if (t1 == null || t2 == null) return false;
        return t1.base.toString().equals(t2.base.toString()) &&
               t1.exponente.toString().equals(t2.exponente.toString());
    }

    // Reconstruye el nodo a partir de un término
    public NodoExpresion construirNodo() {
        // Si el coeficiente es 0, devolver 0
        if (coeficiente instanceof NodoConstante c && c.number == 0) {
            return new NodoConstante(0);
        }

        // Si el exponente es 0 → solo coeficiente
        if (exponente instanceof NodoConstante c && c.number == 0) {
            return coeficiente;
        }

        // Si el exponente es 1 → coeficiente * base
        if (exponente instanceof NodoConstante c && c.number == 1) {
            // Si coeficiente es 1, solo la base
            if (coeficiente instanceof NodoConstante c2 && c2.number == 1) {
                return base;
            }
            return new NodoOperadorBinario('*', coeficiente, base);
        }

        // Construir: coeficiente * base^exponente
        NodoPotencia pot = new NodoPotencia(base, exponente);
        if (coeficiente instanceof NodoConstante c && c.number == 1) {
            return pot;
        }
        return new NodoOperadorBinario('*', coeficiente, pot);
    }

    @Override
    public String toString() {
        return coeficiente + " * " + base + "^" + exponente;
    }
}