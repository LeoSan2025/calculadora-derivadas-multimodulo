package com.tucalculadora.nodos;

public abstract class NodoExpresion {
    public abstract double value(double x);
    public abstract NodoExpresion derivar();
    public abstract NodoExpresion simplificar();

    abstract void printStackCommands();
    @Override
    public abstract String toString();
    public abstract String toLaTeX();
}
