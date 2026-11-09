package com.tucalculadora.util;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class LaTeXRendererFX {

    /**
     * Renderiza una fórmula LaTeX a una imagen JavaFX.
     *
     * @param latex Código LaTeX
     * @param fontSize Tamaño de fuente
     * @return Image de JavaFX
     */
    public static Image renderizarFX(String latex, int fontSize) {
        try {
            TeXFormula formula = new TeXFormula(latex);
            TeXIcon icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY, fontSize);
            icon.setInsets(new Insets(5, 5, 5, 5));

            BufferedImage bufferedImage = new BufferedImage(
                icon.getIconWidth(),
                icon.getIconHeight(),
                BufferedImage.TYPE_INT_ARGB
            );

            Graphics2D g2 = bufferedImage.createGraphics();
            g2.setColor(Color.WHITE);
            g2.fillRect(0, 0, icon.getIconWidth(), icon.getIconHeight());
            icon.paintIcon(new JLabel(), g2, 0, 0);
            g2.dispose();

            return SwingFXUtils.toFXImage(bufferedImage, null);
        } catch (Exception e) {
            System.err.println("Error al renderizar LaTeX: " + e.getMessage());
            return null;
        }
    }

    /**
     * Versión simplificada con tamaño por defecto.
     */
    public static Image renderizarFX(String latex) {
        return renderizarFX(latex, 24);
    }
}