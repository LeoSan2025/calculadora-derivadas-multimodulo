package com.tucalculadora.ui;

import com.tucalculadora.ResultadoDerivada;
import com.tucalculadora.ServicioDerivadas;
import com.tucalculadora.util.LaTeXRendererSwing;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.awt.image.BufferedImage;

/**
 * Controlador de la vista {@code calculadora.fxml}.
 *
 * <p>Recibe los eventos del FXML, llama a {@link ServicioDerivadas} (la fachada
 * del core) y actualiza la vista. No conoce los nodos ni el parser.</p>
 */
public class CalculadoraController {

    // Configuración de renderizado LaTeX (la misma de la vieja UI).
    private static final int IMAGE_WIDTH = 360;
    private static final int IMAGE_HEIGHT = 90;
    private static final int FONT_SIZE = 24;
    private static final double SCALE_FACTOR = 2.0;

    private final ServicioDerivadas servicio = new ServicioDerivadas();

    @FXML private TextField entradaField;
    @FXML private TextField puntoField;
    @FXML private ImageView fImgView;
    @FXML private ImageView fPrimaImgView;
    @FXML private Label resultadoLabel;
    @FXML private Label errorLabel;
    @FXML private TextArea historialArea;

    /** Handler del botón "Derivar" (referenciado como onAction="#calcularDerivada"). */
    @FXML
    private void calcularDerivada() {
        String expresion = entradaField.getText();
        double punto = parsePunto();

        ResultadoDerivada r = servicio.derivar(expresion, punto);

        if (r.hayError()) {
            errorLabel.setText(r.error());
            errorLabel.setVisible(true);
            return;
        }

        errorLabel.setVisible(false);

        mostrarImagen(fImgView, r.fLaTeX());
        mostrarImagen(fPrimaImgView, r.fPrimaLaTeX());

        resultadoLabel.setText("f'(" + formatear(punto) + ") = " + r.valor());

        historialArea.appendText("f(x) = " + r.expresion() + "\n");
        historialArea.appendText("  f'(x) = " + r.fPrimaLaTeX() + "\n");
        historialArea.appendText("  f'(" + formatear(punto) + ") = " + r.valor() + "\n\n");
    }

    private void mostrarImagen(ImageView view, String latex) {
        if (latex == null) {
            view.setImage(null);
            return;
        }
        BufferedImage buf = LaTeXRendererSwing.renderizarConAnchoFijo(
                latex, FONT_SIZE, SCALE_FACTOR, IMAGE_WIDTH);
        Image img = SwingFXUtils.toFXImage(buf, null);
        view.setImage(img);
        view.setFitWidth(IMAGE_WIDTH);
        view.setFitHeight(IMAGE_HEIGHT);
    }

    /** Lee el punto de evaluación; si está vacío o es inválido usa 2.0 por defecto. */
    private double parsePunto() {
        String t = (puntoField.getText() == null) ? "" : puntoField.getText().trim();
        if (t.isEmpty()) {
            return 2.0;
        }
        try {
            return Double.parseDouble(t);
        } catch (NumberFormatException e) {
            return 2.0;
        }
    }

    private String formatear(double n) {
        return (n == (int) n) ? String.valueOf((int) n) : String.valueOf(n);
    }
}
