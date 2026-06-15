/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.gestorui.components;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author ignacio
 */
public class Principal extends Application {

    private static Stage stagePrincipal;

    @Override
    public void start(Stage stage) throws Exception {
        stagePrincipal = stage;
        // Cargar directamente el FXML
        Parent root = FXMLLoader.load(getClass().getResource("/com/mycompany/gestorui/views/PrincipalFXML.fxml"));
        stage.initStyle(StageStyle.UNDECORATED);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static void mostrarVentanaPrincipal() {
        try {
            Stage newStage = new Stage();
            Parent root = FXMLLoader.load(Principal.class.getResource("/com/mycompany/gestorui/views/PrincipalFXML.fxml"));
            newStage.initStyle(StageStyle.UNDECORATED);
            Scene scene = new Scene(root);
            newStage.setScene(scene);
            newStage.show();
        } catch (IOException e) {
            System.err.println("Error al abrir ventana principal: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void cerrarVentanaPrincipal() {
        if (stagePrincipal != null) {
            stagePrincipal.close();
            stagePrincipal = null;
        }
    }

}
