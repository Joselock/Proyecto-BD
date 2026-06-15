/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.gestorui.controllers;

import com.jfoenix.controls.JFXButton;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * FXML Controller class
 *
 * @author ignacio
 */
public class PrincipalController implements Initializable {

    @FXML
    private Label X;

    @FXML
    private Label minimizar;

    @FXML
    private JFXButton iniciar;

    @FXML
    private JFXButton registrar;

    private static Stage stagePrincipal;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }

    @FXML
    private void handleMinimize(MouseEvent event) {
        Stage stage = (Stage) ((Label) event.getSource()).getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    private void handleClose(MouseEvent event) {
        System.exit(0);
    }

    @FXML
    private void handleIniciar(ActionEvent event) {
        try {
            // Guardar la ventana principal
            stagePrincipal = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stagePrincipal.hide();

            // Abrir login
            Parent root = FXMLLoader.load(getClass().getResource("/com/mycompany/gestorui/views/LoginFXML.fxml"));
            Stage stageLogin = new Stage();
            stageLogin.initStyle(StageStyle.UNDECORATED);
            stageLogin.setScene(new Scene(root));
            stageLogin.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRegistro(ActionEvent event) {
        try {
            Stage stagePrincipal = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stagePrincipal.hide();

            // Abrir primer paso (datos personales)
            Parent root = FXMLLoader.load(getClass().getResource("/com/mycompany/gestorui/views/Registro/RegistroPersonalFXML.fxml"));
            Stage stageRegistro = new Stage();
            stageRegistro.initStyle(StageStyle.UNDECORATED);
            stageRegistro.setScene(new Scene(root));
            stageRegistro.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Método para cerrar definitivamente la ventana principal (después del login)
    public static void cerrarVentanaPrincipal() {
        if (stagePrincipal != null) {
            stagePrincipal.close();
        }
    }

    public static Stage getStagePrincipal() {
        return stagePrincipal;
    }

}
