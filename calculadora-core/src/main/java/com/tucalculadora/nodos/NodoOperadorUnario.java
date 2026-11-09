package com.tucalculadora.nodos;

public class NodoOperadorUnario extends NodoExpresion {
    NodoExpresion operando;

    public NodoOperadorUnario(NodoExpresion operador) {
        this.operando = operador;
    }

    @Override
    public double value(double x) {
        return -operando.value(x);
    }

    public NodoExpresion derivar() {
        return new NodoOperadorUnario(operando.derivar());
    }

    @Override
    public NodoExpresion simplificar() {
        NodoExpresion op = operando.simplificar();

        // Si el operando es constante, calcular
        if (op instanceof NodoConstante) {
            return new NodoConstante(-((NodoConstante) op).number);
        }

        // Si el operando es otro negativo: -(-x) → x
        if (op instanceof NodoOperadorUnario) {
            return ((NodoOperadorUnario) op).operando;
        }

        return new NodoOperadorUnario(op);
    }

    @Override
    public String toString() {
        return "-( " + operando.toString() + " )";
    }

    @Override
    public String toLaTeX() {
        String opStr = operando.toLaTeX();

        // Si el operando es una constante, simplemente poner "-5"
        if (operando instanceof NodoConstante) {
            return "-" + opStr;
        }

        // Si el operando ya es complejo, poner paréntesis: "-(x+2)"
        return "-(" + opStr + ")";
    }

    @Override
    void printStackCommands() {
        operando.printStackCommands();
        System.out.println("  Unary minus");
    }
}