package com.tucalculadora;

import com.tucalculadora.nodos.NodoExpresion;
import com.tucalculadora.parser.ParseError;
import com.tucalculadora.parser.ParserInterno;

/**
 * Fachada del core: la única puerta de entrada a la lógica de derivación.
 *
 * <p>Las vistas (fx y web) hablan solo con esta clase; nunca importan
 * {@code NodoExpresion}, {@code ParserInterno} ni {@code ParseError}.</p>
 */
public class ServicioDerivadas {

    /**
     * Deriva la expresión dada y evalúa f' en {@code punto}.
     *
     * @param expresion texto con la función, p. ej. "x^2 + 5 - 3*x^2"
     * @param punto     valor de x donde evaluar la derivada
     * @return un {@link ResultadoDerivada} con las fórmulas LaTeX, el valor
     *         evaluado y, si hubo fallo, el mensaje de error en {@code error()}.
     */
    public ResultadoDerivada derivar(String expresion, double punto) {
        String expr = (expresion == null) ? "" : expresion.trim();

        if (expr.isEmpty()) {
            return new ResultadoDerivada(expr, null, null, Double.NaN,
                    "Por favor, escribe una expresión.");
        }

        if (!parentesisBalanceados(expr)) {
            return new ResultadoDerivada(expr, null, null, Double.NaN,
                    "Error: Paréntesis desbalanceados.");
        }

        try {
            ParserInterno parser = new ParserInterno(expr);
            NodoExpresion arbol = parser.expressionTree().simplificar();

            NodoExpresion derivada = arbol.derivar();
            NodoExpresion derivadaSimplificada = derivada.simplificar();

            String fLaTeX = arbol.toLaTeX();
            String fPrimaLaTeX = derivadaSimplificada.toLaTeX();
            double valor = derivadaSimplificada.value(punto);

            return new ResultadoDerivada(expr, fLaTeX, fPrimaLaTeX, valor, null);

        } catch (ParseError e) {
            return new ResultadoDerivada(expr, null, null, Double.NaN,
                    "Error de sintaxis: " + e.getMessage());
        } catch (Exception e) {
            return new ResultadoDerivada(expr, null, null, Double.NaN,
                    "Error: " + e.getMessage());
        }
    }

    private boolean parentesisBalanceados(String s) {
        int balance = 0;
        for (char c : s.toCharArray()) {
            if (c == '(') balance++;
            if (c == ')') balance--;
        }
        return balance == 0;
    }
}
