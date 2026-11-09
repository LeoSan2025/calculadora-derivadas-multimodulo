package com.tucalculadora.nodos;

public class NodoConstante extends NodoExpresion {
    public double number;

    public NodoConstante(double number) {
        this.number = number;
    }

    @Override
    public double value(double x) {
        return number;
    }

    @Override
    public NodoExpresion derivar() {
        return new NodoConstante(0);
    }

    @Override
    public NodoExpresion simplificar() {
        return this;
    }

    @Override
    public String toString() {
        if (number == (int) number) {
            return String.valueOf((int) number);
        }
        return String.valueOf(number);
    }

    @Override
    public String toLaTeX() {
        // Si el número es entero (ej: 3.0), mostrarlo como "3"
        // Si es decimal (ej: 3.14), mostrarlo como "3.14"
        if (number == (int) number) {
            return String.valueOf((int) number);
        }
        return String.valueOf(number);
    }

    @Override
    void printStackCommands() {
        System.out.println("  Push " + number);
    }
}
