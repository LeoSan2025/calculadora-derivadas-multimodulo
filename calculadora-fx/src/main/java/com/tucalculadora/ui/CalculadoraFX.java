package com.tucalculadora.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Punto de entrada de la aplicación JavaFX.
 *
 * <p>Solo carga la vista definida en {@code calculadora.fxml}; la lógica de la
 * interfaz vive en {@link CalculadoraController} y la de negocio en
 * {@code com.tucalculadora.ServicioDerivadas}.</p>
 */
public class CalculadoraFX extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/calculadora.fxml"));
        Parent root = loader.load();

        primaryStage.setTitle("Calculadora de Derivadas");
        primaryStage.setMinWidth(850);
        primaryStage.setMinHeight(550);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
}
