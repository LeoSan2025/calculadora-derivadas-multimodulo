package com.tucalculadora.ui;

import com.tucalculadora.nodos.NodoExpresion;
import com.tucalculadora.parser.ParseError;
import com.tucalculadora.parser.ParserInterno;
import com.tucalculadora.util.LaTeXRendererSwing;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.awt.image.BufferedImage;

public class CalculadoraFX extends Application {
    // ========== CONFIGURACIÓN DE CALIDAD ==========
    private static final int IMAGE_WIDTH = 360;
    private static final int IMAGE_HEIGHT = 90;
    private static final int FONT_SIZE = 24;
    private static final double SCALE_FACTOR = 2.0;

    private TextField entradaField;
    private ImageView fImgView;
    private ImageView fPrimaImgView;
    private Label resultadoLabel;
    private Label errorLabel;
    private TextArea historialArea;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Calculadora de Derivadas");
        primaryStage.setMinWidth(850);
        primaryStage.setMinHeight(550);

        // ========== CONTENEDOR PRINCIPAL ==========
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));

        // ========== PARTE SUPERIOR: ENTRADA ==========
        VBox topBox = new VBox(10);
        topBox.setPadding(new Insets(0, 0, 15, 0));

        Label titulo = new Label("Calculadora de Derivadas");
        titulo.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        HBox entradaBox = new HBox(10);
        entradaBox.setAlignment(Pos.CENTER_LEFT);

        Label entradaLabel = new Label("f(x) = ");
        entradaLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        //entradaField = new TextField("(x+2)*(x-1)");
        entradaField = new TextField("x^2 + 5 - 3*x^2 ");
        //  TEMPORAL          x^2 + 5 - 3*x^2
        entradaField.setPrefWidth(400);
        entradaField.setStyle("-fx-font-size: 14px; -fx-font-family: monospace;");

        Button calcularBtn = new Button("Derivar");
        calcularBtn.setStyle("-fx-font-size: 14px; -fx-padding: 8 20; -fx-background-color: #3498db; -fx-text-fill: white;");
        calcularBtn.setOnAction(e -> calcularDerivada());

        entradaBox.getChildren().addAll(entradaLabel, entradaField, calcularBtn);
        topBox.getChildren().addAll(titulo, entradaBox);

        // ========== PARTE CENTRAL: IMÁGENES CON TAMAÑO FIJO ==========
        HBox imagesBox = new HBox(40);
        imagesBox.setAlignment(Pos.CENTER);
        imagesBox.setPadding(new Insets(20, 0, 20, 0));

        // f(x)
        VBox fBox = new VBox(10);
        fBox.setAlignment(Pos.CENTER);
        fBox.setPrefWidth(IMAGE_WIDTH + 40);
        fBox.setStyle("-fx-border-color: #bdc3c7; -fx-border-radius: 10; -fx-background-color: #f8f9fa; -fx-background-radius: 10; -fx-padding: 15;");

        Label fLabel = new Label("f(x) = ");
        fLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        fImgView = new ImageView();
        fImgView.setPreserveRatio(false);
        fImgView.setFitWidth(IMAGE_WIDTH);
        fImgView.setFitHeight(IMAGE_HEIGHT);

        fBox.getChildren().addAll(fLabel, fImgView);

        // f'(x)
        VBox fPrimaBox = new VBox(10);
        fPrimaBox.setAlignment(Pos.CENTER);
        fPrimaBox.setPrefWidth(IMAGE_WIDTH + 40);
        fPrimaBox.setStyle("-fx-border-color: #bdc3c7; -fx-border-radius: 10; -fx-background-color: #f8f9fa; -fx-background-radius: 10; -fx-padding: 15;");

        Label fPrimaLabel = new Label("f'(x) = ");
        fPrimaLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        fPrimaImgView = new ImageView();
        fPrimaImgView.setPreserveRatio(false);
        fPrimaImgView.setFitWidth(IMAGE_WIDTH);
        fPrimaImgView.setFitHeight(IMAGE_HEIGHT);

        fPrimaBox.getChildren().addAll(fPrimaLabel, fPrimaImgView);

        imagesBox.getChildren().addAll(fBox, fPrimaBox);

        // ========== RESULTADO ==========
        HBox resultadoBox = new HBox(10);
        resultadoBox.setAlignment(Pos.CENTER);
        resultadoLabel = new Label("f'(2) = ?");
        resultadoLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        // ========== ERRORES ==========
        errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 13px;");
        errorLabel.setVisible(false);

        VBox centerBox = new VBox(10);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.getChildren().addAll(imagesBox, resultadoBox, errorLabel);

        // ========== HISTORIAL ==========
        VBox bottomBox = new VBox(10);
        bottomBox.setPadding(new Insets(15, 0, 0, 0));

        Label historialLabel = new Label("Historial:");
        historialLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        historialArea = new TextArea();
        historialArea.setPrefHeight(80);
        historialArea.setEditable(false);
        historialArea.setStyle("-fx-font-family: monospace; -fx-font-size: 12px;");

        bottomBox.getChildren().addAll(historialLabel, historialArea);

        // ========== ARMAR LA VENTANA ==========
        root.setTop(topBox);
        root.setCenter(centerBox);
        root.setBottom(bottomBox);

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();

        // Calcular ejemplo al iniciar
        calcularDerivada();
    }

    private void calcularDerivada() {
        String expresion = entradaField.getText().trim();

        if (expresion.isEmpty()) {
            errorLabel.setText("Por favor, escribe una expresión.");
            errorLabel.setVisible(true);
            return;
        }

        // Validar paréntesis
        int balance = 0;
        for (char c : expresion.toCharArray()) {
            if (c == '(') balance++;
            if (c == ')') balance--;
        }
        if (balance != 0) {
            errorLabel.setText("Error: Paréntesis desbalanceados.");
            errorLabel.setVisible(true);
            return;
        }

        errorLabel.setVisible(false);

        try {
            ParserInterno parser = new ParserInterno(expresion);
            NodoExpresion arbol = parser.expressionTree().simplificar();

            NodoExpresion derivada = arbol.derivar();
            NodoExpresion derivadaSimplificada = derivada.simplificar();

            String fLaTeX = arbol.toLaTeX();
            String fPrimaLaTeX = derivadaSimplificada.toLaTeX();

            // Renderizar con alta calidad usando LaTeXRendererSwing
            BufferedImage fImg = LaTeXRendererSwing.renderizarConAnchoFijo(
                    fLaTeX, FONT_SIZE, SCALE_FACTOR, IMAGE_WIDTH
            );

            BufferedImage fPrimaImg = LaTeXRendererSwing.renderizarConAnchoFijo(
                    fPrimaLaTeX, FONT_SIZE, SCALE_FACTOR, IMAGE_WIDTH
            );

            // Mostrar imágenes con tamaño fijo
            mostrarImagen(fImgView, fImg);
            mostrarImagen(fPrimaImgView, fPrimaImg);

            double resultado = derivada.value(2);
            resultadoLabel.setText("f'(2) = " + resultado);

            historialArea.appendText("f(x) = " + expresion + "\n");
            historialArea.appendText("  f'(x) = " + fPrimaLaTeX + "\n");
            historialArea.appendText("  f'(2) = " + resultado + "\n\n");

        } catch (ParseError e) {
            errorLabel.setText("Error de sintaxis: " + e.getMessage());
            errorLabel.setVisible(true);
            System.out.println("Error de sintaxis: " + e.getMessage());;
        } catch (Exception e) {
            errorLabel.setText("Error: " + e.getMessage());
            errorLabel.setVisible(true);
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Muestra una imagen en un ImageView con tamaño fijo.
     * Escala la imagen para que quepa exactamente en el tamaño definido.
     */
    private void mostrarImagen(ImageView imageView, BufferedImage bufferedImage) {
        if (bufferedImage != null) {
            Image fxImage = SwingFXUtils.toFXImage(bufferedImage, null);
            imageView.setImage(fxImage);
            imageView.setFitWidth(IMAGE_WIDTH);
            imageView.setFitHeight(IMAGE_HEIGHT);
        } else {
            imageView.setImage(null);
        }
    }
}