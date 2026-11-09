package com.tucalculadora.util;

import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class LaTeXRendererSwing {

    /**
     * Renderiza una fórmula LaTeX con ancho fijo.
     * Esta es la versión recomendada para tu calculadora.
     *
     * @param latex Código LaTeX
     * @param fontSize Tamaño de fuente (recomendado: 22-28)
     * @param scaleFactor Factor de calidad (recomendado: 2.0)
     * @param targetWidth Ancho deseado en píxeles
     * @return BufferedImage con el ancho fijo
     */
    public static BufferedImage renderizarConAnchoFijo(String latex, int fontSize,
                                                       double scaleFactor, int targetWidth) {
        try {
            if (latex == null || latex.isEmpty()) {
                return crearImagenVacia(targetWidth, 60);
            }

            // 1. Crear la fórmula
            TeXFormula formula = new TeXFormula(latex);
            TeXIcon icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY, fontSize);
            icon.setInsets(new Insets(5, 5, 5, 5));

            // 2. Calcular escala para alcanzar el ancho deseado
            double currentWidth = icon.getIconWidth();
            if (currentWidth <= 0) currentWidth = 1;

            double scale = (targetWidth * scaleFactor) / currentWidth;

            // Limitar escala para que no sea demasiado grande ni pequeña
            scale = Math.max(scale, 0.5);
            scale = Math.min(scale, 3.0);

            // 3. Calcular dimensiones escaladas
            int scaledWidth = (int) (icon.getIconWidth() * scale);
            int scaledHeight = (int) (icon.getIconHeight() * scale);

            // 4. Crear imagen de alta calidad
            BufferedImage image = new BufferedImage(
                    Math.max(scaledWidth, 50),
                    Math.max(scaledHeight, 30),
                    BufferedImage.TYPE_INT_ARGB
            );

            // 5. Configurar gráficos de alta calidad
            Graphics2D g2 = image.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            // 6. Fondo blanco
            g2.setColor(Color.WHITE);
            g2.fillRect(0, 0, image.getWidth(), image.getHeight());

            // 7. Escalar y pintar
            g2.scale(scale, scale);
            icon.paintIcon(new JLabel(), g2, 0, 0);
            g2.dispose();

            return image;

        } catch (Exception e) {
            System.err.println("Error al renderizar LaTeX: " + e.getMessage());
            return crearImagenVacia(targetWidth, 60);
        }
    }

    /**
     * Versión con valores por defecto (calidad media-alta).
     * Usa fontSize=24, scaleFactor=2.0, targetWidth=350
     */
    public static BufferedImage renderizar(String latex) {
        return renderizarConAnchoFijo(latex, 24, 2.0, 350);
    }

    /**
     * Versión con tamaño de fuente personalizado.
     */
    public static BufferedImage renderizar(String latex, int fontSize) {
        return renderizarConAnchoFijo(latex, fontSize, 2.0, 350);
    }

    /**
     * Versión con ancho personalizado.
     */
    public static BufferedImage renderizar(String latex, int fontSize, int targetWidth) {
        return renderizarConAnchoFijo(latex, fontSize, 2.0, targetWidth);
    }

    /**
     * Crea una imagen de error.
     */
    private static BufferedImage crearImagenVacia(int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, width, height);
        g2.setColor(Color.RED);
        g2.setFont(new Font("Arial", Font.PLAIN, 14));
        g2.drawString("Error al renderizar", 20, 30);
        g2.dispose();
        return image;
    }
}