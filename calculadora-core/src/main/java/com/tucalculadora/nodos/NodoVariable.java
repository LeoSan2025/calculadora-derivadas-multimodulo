package com.tucalculadora.nodos;

public class NodoVariable extends NodoExpresion {
    final char variable;

    public NodoVariable() {
        this.variable = 'x';
    }

    @Override
    public double value(double x) {
        return x;
    }

    @Override
    public NodoExpresion derivar() {
        return new NodoConstante(1);
    }

    @Override
    public NodoExpresion simplificar() {
        return this;
    }

    @Override
    public String toString() {
        return "x";
    }

    @Override
    public String toLaTeX() {
        return "x";
    }

    @Override
    void printStackCommands() {
        System.out.println("  Push x");
    }
}