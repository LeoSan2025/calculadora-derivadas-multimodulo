package com.tucalculadora.nodos;

public class NodoPotencia extends NodoExpresion {
    NodoExpresion base;
    NodoExpresion exponente;

    public NodoPotencia(NodoExpresion base, NodoExpresion exponente) {
        this.base = base;
        this.exponente = exponente;
    }

    public NodoPotencia(NodoPotencia nodoPotencia) {
        this.base = nodoPotencia.base;
        this.exponente = nodoPotencia.exponente;
    }

    @Override
    public double value(double x) {
        return Math.pow(base.value(x), exponente.value(x));
    }

    @Override
    public NodoExpresion derivar() {
        // Regla de la potencia: (u^n)' = n * u^(n-1) * u'
        NodoExpresion n = exponente;
        NodoExpresion u = base;
        NodoExpresion uPrima = u.derivar();

        NodoExpresion nMenosUno;
        NodoExpresion uElevado;

        // n * u^(n-1) * u'
        if (n instanceof NodoConstante c) {
            nMenosUno = new NodoConstante(c.number - 1);
        } else {
            nMenosUno = new NodoOperadorBinario('-', n, new NodoConstante(1));
        }
        uElevado = new NodoPotencia(u, nMenosUno);
        if (uPrima instanceof NodoConstante nodo && nodo.number == 1) {
            return new NodoOperadorBinario('*', n, uElevado);
        } else {
            NodoExpresion termino1 = new NodoOperadorBinario('*', n, uElevado);
            return new NodoOperadorBinario('*', termino1, uPrima);
        }
    }

    @Override
    public NodoExpresion simplificar() {
        NodoExpresion baseSimp = base.simplificar();
        NodoExpresion expSimp = exponente.simplificar();

        // Si exponente es 0 → 1
        if (expSimp instanceof NodoConstante && ((NodoConstante) expSimp).number == 0) {
            return new NodoConstante(1);
        }

        // Si exponente es 1 → base
        if (expSimp instanceof NodoConstante && ((NodoConstante) expSimp).number == 1) {
            return baseSimp;
        }

        // Si base es 0 → 0^n = 0 (excepto 0^0 = 1)
        if (baseSimp instanceof NodoConstante && ((NodoConstante) baseSimp).number == 0) {
            return new NodoConstante(0);
        }

        // Si base y exponente son constantes → calcular
        if (baseSimp instanceof NodoConstante && expSimp instanceof NodoConstante) {
            double baseNum = ((NodoConstante) baseSimp).number;
            double expNum = ((NodoConstante) expSimp).number;
            return new NodoConstante(Math.pow(baseNum, expNum));
        }

        return new NodoPotencia(baseSimp, expSimp);
    }

    @Override
    public String toLaTeX() {
        String baseStr = base.toLaTeX();
        String expStr = exponente.toLaTeX();

        // Si el exponente es 1, no mostrar ^1
        if (exponente instanceof NodoConstante && ((NodoConstante) exponente).number == 1) {
            return baseStr;
        }

        // Si el exponente es 0, x^0 = 1
        if (exponente instanceof NodoConstante && ((NodoConstante) exponente).number == 0) {
            return "1";
        }

        // Si la base es simple (variable o número), no poner paréntesis
        if (base instanceof NodoVariable || base instanceof NodoConstante) {
            return baseStr + "^{" + expStr + "}";
        }

        // Si la base es compleja, poner paréntesis
        return "(" + baseStr + ")^{" + expStr + "}";
    }

    @Override
    public String toString() {
        String baseStr = base.toString();
        String expStr = exponente.toString();

        // Si exponente == 1, no mostrar ^1
        if (exponente instanceof NodoConstante && ((NodoConstante) exponente).number == 1) {
            return baseStr;
        }

        // Si exponente == 0, devolver "1"
        if (exponente instanceof NodoConstante && ((NodoConstante) exponente).number == 0) {
            return "1";
        }

        // Si la base es variable o constante, no poner paréntesis
        if (base instanceof NodoVariable || base instanceof NodoConstante) {
            return baseStr + "^" + expStr;
        }

        return "( " + baseStr + " ^ " + expStr + " )";
    }

    @Override
    public void printStackCommands() {
        base.printStackCommands();
        exponente.printStackCommands();
        System.out.println("  Operator ^");
    }
}