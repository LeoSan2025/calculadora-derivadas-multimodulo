package com.tucalculadora.parser;

import com.tucalculadora.nodos.*;

public class ParserInterno {
    private final String input;
    private int posicion;

    public ParserInterno(String input) {
        this.input = input;
        this.posicion = 0;
    }

    // MÉTODOS DEL PARSER
    public NodoExpresion expressionTree() throws ParseError {
        skipBlanks();

        NodoExpresion term = termTree();
        skipBlanks();

        while (peek() == '+' || peek() == '-') {
            char op = getAnyChar();
            NodoExpresion next = termTree();
            term = new NodoOperadorBinario(op, term, next);
            skipBlanks();
        }
        return term;
    }

    NodoExpresion termTree() throws ParseError {
        skipBlanks();

        NodoExpresion factor = factorTree();
        skipBlanks();

        while (peek() == '*' || peek() == '/') {
            char op = getAnyChar();
            NodoExpresion next = factorTree();
            factor = new NodoOperadorBinario(op, factor, next);
            skipBlanks();
        }
        return factor;
    }

    NodoExpresion factorTree() throws ParseError {
        skipBlanks();

        NodoExpresion factor;

        // 1. Signo negativo unario
        if (peek() == '-') {
            getAnyChar();
            factor = new NodoOperadorUnario(factorTree());
        }
        // 2. Número
        else if (Character.isDigit(peek()) || peek() == '.') {
            factor = new NodoConstante(getDouble());
        }
        // 3. Variable x
        else if (peek() == 'x') {
            getAnyChar();
            factor = new NodoVariable();
        }
        // 4. Expresión entre paréntesis
        else if (peek() == '(') {
            getAnyChar();
            factor = expressionTree();
            if (peek() != ')') {
                throw new ParseError("Falta ')'");
            }
            getAnyChar();
        } else {
            throw new ParseError("Factor inválido: " + peek());
        }

        // 5. POTENCIA (se aplica AL factor que acabamos de leer)
        if (peek() == '^') {
            getAnyChar();
            NodoExpresion exponente = factorTree();  // Recursión para exponente
            factor = new NodoPotencia(factor, exponente);
        }

        return factor;
    }

    // MÉTODOS AUXILIARES

    /**
     * Devuelve el carácter actual sin consumirlo.
     * Si estamos al final, devuelve '\0'.
     */
    private char peek() {
        if (posicion >= input.length()) {
            return '\0';
        }
        return input.charAt(posicion);
    }

    /**
     * Salta espacios en blanco y tabuladores.
     */
    private void skipBlanks() {
        while (peek() == ' ' || peek() == '\t') {
            posicion++;
        }
    }

    /**
     * Devuelve el siguiente carácter y avanza la posición.
     */
    private char getAnyChar() {
        char c = peek();
        posicion++;
        return c;
    }

    /**
     * Lee un número double.
     * Ejemplos: 3, 3.14, .5, 2.
     */
    private double getDouble() {
        StringBuilder sb = new StringBuilder();
        while (Character.isDigit(peek()) || peek() == '.') {
            sb.append(getAnyChar());
        }
        return Double.parseDouble(sb.toString());
    }
}

