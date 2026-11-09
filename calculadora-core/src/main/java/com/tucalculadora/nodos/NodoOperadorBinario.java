package com.tucalculadora.nodos;

import java.util.*;

public class NodoOperadorBinario extends NodoExpresion {
    public char operador;
    public NodoExpresion izquierdo;
    public NodoExpresion derecho;

    public NodoOperadorBinario(char operador, NodoExpresion izquierdo, NodoExpresion derecho) {
        this.operador = operador;
        this.izquierdo = izquierdo;
        this.derecho = derecho;
    }

    /**
     * Recorre recursivamente el árbol y recoge todos los términos
     * que están siendo sumados o restados.
     */
    private void recogerTerminos(NodoExpresion nodo, List<Termino> terminos, int signo) {
        if (nodo == null) return;

        // Si es una operación de suma o resta, explorar sus hijos
        if (nodo instanceof NodoOperadorBinario bin) {
            if (bin.operador == '+' || bin.operador == '-') {
                int nuevoSigno = (bin.operador == '+') ? signo : -signo;
                recogerTerminos(bin.izquierdo, terminos, signo);
                recogerTerminos(bin.derecho, terminos, nuevoSigno);
                return;
            }
        }

        // Si es una operación de multiplicación o división, no descomponer más
        // Extraer el término directamente
        Termino t = Termino.extraer(nodo);
        if (t != null) {
            // Aplicar el signo al coeficiente
            if (signo == -1 && t.coeficiente instanceof NodoConstante c) {
                t = new Termino(new NodoConstante(-c.number), t.base, t.exponente);
            } else if (signo == -1) {
                // Si el coeficiente no es constante, crear un unario
                t = new Termino(new NodoOperadorUnario(t.coeficiente), t.base, t.exponente);
            }
            terminos.add(t);
        } else {
            // Si no se pudo extraer como término, mantenerlo como está
            // (es una operación más compleja que no se puede simplificar)
            // Lo envolvemos en un término especial
            terminos.add(new Termino(new NodoConstante(1), nodo, new NodoConstante(1)));
        }
    }

    /**
     * Combina términos semejantes de forma recursiva.
     */
    private List<Termino> combinarTerminos(List<Termino> terminos) {
        Map<String, Termino> mapa = new HashMap<>();

        for (Termino t : terminos) {
            String clave = t.base.toString() + "|" + t.exponente.toString();
            Termino existente = mapa.get(clave);

            if (existente == null) {
                mapa.put(clave, t);
            } else {
                // Combinar coeficientes
                double coef1 = extraerNumero(existente.coeficiente);
                double coef2 = extraerNumero(t.coeficiente);
                double nuevoCoef = coef1 + coef2;

                if (nuevoCoef == 0) {
                    mapa.remove(clave);
                } else {
                    mapa.put(clave, new Termino(
                            new NodoConstante(nuevoCoef),
                            existente.base,
                            existente.exponente
                    ));
                }
            }
        }

        return new ArrayList<>(mapa.values());
    }

    /**
     * Extrae un número de un NodoExpresion (si es constante).
     */
    private double extraerNumero(NodoExpresion nodo) {
        if (nodo instanceof NodoConstante c) {
            return c.number;
        }
        if (nodo instanceof NodoOperadorUnario u) {
            return -extraerNumero(u.operando);
        }
        return 0;  // No se puede extraer como número
    }

    /**
     * Reconstruye un árbol a partir de una lista de términos.
     */
    private NodoExpresion reconstruirArbol(List<Termino> terminos) {
        if (terminos.isEmpty()) {
            return new NodoConstante(0);
        }

        NodoExpresion resultado = terminos.get(0).construirNodo();

        for (int i = 1; i < terminos.size(); i++) {
            NodoExpresion siguiente = terminos.get(i).construirNodo();
            resultado = new NodoOperadorBinario('+', resultado, siguiente);
        }

        return resultado;
    }

    @Override
    public double value(double x) {
        double izq = izquierdo.value(x);
        double der = derecho.value(x);

        return switch (operador) {
            case '+' -> izq + der;
            case '-' -> izq - der;
            case '*' -> izq * der;
            case '/' -> izq / der;
            default -> Double.NaN;
        };
    }

    @Override
    public NodoExpresion derivar() {
        return switch (operador) {
            // SUMA: (u + v)' = u' + v'
            case '+' -> {
                NodoExpresion termino1 = izquierdo.derivar();
                NodoExpresion termino2 = derecho.derivar();

                if (termino1 instanceof NodoConstante nodo && nodo.number == 0 && termino2 instanceof NodoOperadorBinario nob) {
                    yield new NodoOperadorBinario(nob.operador, nob.izquierdo, nob.derecho);
                } else {
                    yield new NodoOperadorBinario(operador, termino1, termino2);
                }
            }
            // RESTA: (u - v)' = u' - v'
            case '-' -> {
                NodoExpresion termino1 = izquierdo.derivar();
                NodoExpresion termino2 = derecho.derivar();

                if (termino1 instanceof NodoConstante nodoConstante && nodoConstante.number == 0 && termino2 instanceof NodoOperadorBinario nodoOperadorBinario) {
                    yield new NodoOperadorUnario(nodoOperadorBinario);
                } else {
                    yield new NodoOperadorBinario(operador, termino1, termino2);
                }
            }
            // PRODUCTO: (u * v)' = u'v + uv'
            case '*' -> {
                NodoExpresion u = izquierdo;
                NodoExpresion v = derecho;
                NodoExpresion du = izquierdo.derivar();
                NodoExpresion dv = derecho.derivar();

                NodoExpresion termino1;
                NodoExpresion termino2;

                termino1 = new NodoOperadorBinario('*', du, v);
                termino2 = new NodoOperadorBinario('*', u, dv);

                if (((NodoOperadorBinario) termino1).izquierdo instanceof NodoConstante c && c.number == 0) {
                    termino1 = new NodoConstante(0);
                }

                // Eliminar (0 * x^3) → 0
                if (termino1 instanceof NodoOperadorBinario nob1 && nob1.izquierdo instanceof NodoConstante c && c.number == 0) {
                    termino1 = new NodoConstante(0);
                }

                if (termino2 instanceof NodoOperadorBinario nob2) {
                    if (nob2.izquierdo instanceof NodoConstante c1 &&
                            c1.number != 0 &&
                            nob2.derecho instanceof NodoOperadorBinario nob3 &&
                            nob3.izquierdo instanceof NodoConstante c2 &&
                            c2.number != 0) {
                        NodoConstante coeficiente = new NodoConstante(c1.number * c2.number);
                        termino2 = new NodoOperadorBinario('*', coeficiente, nob3.derecho);
                    }
                }
                yield new NodoOperadorBinario('+', termino1, termino2);

            }
            // COCIENTE: (u / v)' = (u'v - uv') / v²
            case '/' -> {
                NodoExpresion u = izquierdo;
                NodoExpresion v = derecho;
                NodoExpresion du = izquierdo.derivar();
                NodoExpresion dv = derecho.derivar();

                // numerador = u'v - uv'
                NodoExpresion termino1 = new NodoOperadorBinario('*', du, v);
                NodoExpresion termino2 = new NodoOperadorBinario('*', u, dv);
                NodoExpresion numerador = new NodoOperadorBinario('-', termino1, termino2);

                // denominador = v²
                NodoExpresion denominador = new NodoOperadorBinario('*', v, v);

                yield new NodoOperadorBinario('/', numerador, denominador);
            }
            default -> throw new UnsupportedOperationException("Operador no soportado");
        };
    }

    @Override
    public NodoExpresion simplificar() {
        NodoExpresion left = izquierdo.simplificar();
        NodoExpresion right = derecho.simplificar();

        // 1. Si ambos son números, operar directamente
        if (left instanceof NodoConstante && right instanceof NodoConstante) {
            double leftNum = ((NodoConstante) left).number;
            double rightNum = ((NodoConstante) right).number;
            return switch (operador) {
                case '+' -> new NodoConstante(leftNum + rightNum);
                case '-' -> new NodoConstante(leftNum - rightNum);
                case '*' -> new NodoConstante(leftNum * rightNum);
                case '/' ->
                        rightNum == 0 ? new NodoOperadorBinario('/', left, right) : new NodoConstante(leftNum / rightNum);
                default -> new NodoOperadorBinario(operador, left, right);
            };
        }

        // 2. IDENTIDADES DE SUMA Y RESTA
        if (operador == '+' && right instanceof NodoConstante && ((NodoConstante) right).number == 0) return left;
        if (operador == '+' && left instanceof NodoConstante && ((NodoConstante) left).number == 0) return right;
        if (operador == '+' && left instanceof NodoVariable && right instanceof NodoVariable)
            return new NodoOperadorBinario('*', new NodoConstante(2), new NodoVariable());
        if (operador == '-' && right instanceof NodoConstante && ((NodoConstante) right).number == 0) return left;
        if (operador == '-' && left instanceof NodoConstante && ((NodoConstante) left).number == 0) {
            return new NodoOperadorUnario(right);
        }

        // 3. IDENTIDADES DE MULTIPLICACIÓN
        if (operador == '*' && right instanceof NodoConstante && ((NodoConstante) right).number == 1) return left;
        if (operador == '*' && left instanceof NodoConstante && ((NodoConstante) left).number == 1) return right;

        if (operador == '*' && right instanceof NodoConstante && ((NodoConstante) right).number == 0) {
            return new NodoConstante(0);
        }
        if (operador == '*' && left instanceof NodoConstante && ((NodoConstante) left).number == 0) {
            return new NodoConstante(0);
        }

        // 4. Simplificar números con expresiones (3 * 2x → 6x) SOLO PARA MULTIPLICACIÓN
        if (operador == '*') {
            if (left instanceof NodoConstante && right instanceof NodoOperadorBinario bin) {
                if (bin.operador == '*') {
                    if (bin.izquierdo instanceof NodoConstante) {
                        double a = ((NodoConstante) left).number;
                        double b = ((NodoConstante) bin.izquierdo).number;
                        return new NodoOperadorBinario('*',
                                new NodoConstante(a * b),
                                bin.derecho
                        );
                    }
                }
            }
        }

        // 5. IDENTIDADES DE DIVIÓN
        if (operador == '/' && right instanceof NodoConstante && ((NodoConstante) right).number == 1) return left;
        if (operador == '/' && left instanceof NodoConstante && ((NodoConstante) left).number == 0) {
            return new NodoConstante(0);
        }

        //  6. Si es suma o resta, recoger y combinar términos
        if (operador == '+' || operador == '-') {
            // Recoger todos los términos
            List<Termino> terminos = new ArrayList<>();
            recogerTerminos(left, terminos, 1);
            recogerTerminos(right, terminos, (operador == '+') ? 1 : -1);

            // Combinar semejantes
            List<Termino> combinados = combinarTerminos(terminos);

            // Reconstruir el árbol

            // Simplificar recursivamente el resultado
            return reconstruirArbol(combinados);
        }

        // 7. Si no se aplica ninguna regla, devolver con hijos simplificados
        return new NodoOperadorBinario(operador, left, right);
    }

    @Override
    public String toString() {
        return "( " + izquierdo.toString() + " " + operador + " " + derecho.toString() + " )";
    }

    @Override
    public String toLaTeX() {
        String izq = izquierdo.toLaTeX();
        String der = derecho.toLaTeX();

        return switch (operador) {
            case '+' -> izq + " + " + der;
            case '-' -> izq + " - " + der;
            case '*' -> {
                // Si es número * variable, no mostrar punto de multiplicación
                if (izquierdo instanceof NodoConstante && derecho instanceof NodoVariable) {
                    yield izq + der;  // "2x" en lugar de "2*x"
                }
                // Si es variable * número, mostrar con punto
                if (izquierdo instanceof NodoVariable && derecho instanceof NodoConstante) {
                    yield izq + " \\cdot " + der;  // "x · 2"
                }
                yield izq + " \\cdot " + der;      // "x · y"
            }
            case '/' -> "\\frac{" + izq + "}{" + der + "}";
            default -> izq + " " + operador + " " + der;
        };
    }

    @Override
    void printStackCommands() {
        izquierdo.printStackCommands();
        derecho.printStackCommands();
        System.out.println("  Operator " + operador);
    }
}