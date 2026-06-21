/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.gestorui.controllers.Cuenta;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.mycompany.gestorui.controllers.Login.LoginController;
import com.mycompany.gestorui.model.login.loginSevice.Verificacion;
import com.mycompany.gestorui.model.login.loginSevice.crudUser;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author ignacio
 */
public class CambiarContrasenaController implements Initializable {

    private static String passwordActual;
    
    @FXML
    private JFXPasswordField actual;

    @FXML
    private JFXPasswordField nueva;

    @FXML
    private JFXPasswordField confirmacion;

    @FXML
    private JFXButton btnCancelar;

    @FXML
    private JFXButton btnCambiar;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    @FXML
    private void handleChange(ActionEvent event) throws SQLException {
        passwordActual = actual.getText();
        String passwordNueva = nueva.getText();
        String passwordCon = confirmacion.getText();
        boolean hecho = false;

        String usuario = LoginController.getUsuarioActual();

        if (passwordActual.isEmpty() || passwordNueva.isEmpty() || passwordCon.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initStyle(StageStyle.UNDECORATED);
            alert.setTitle("Error al completar los campos");
            alert.setHeaderText(null); // Sin encabezado extra
            alert.setContentText("Todos los campos son obligatorios.\nPor favor, vuelve a intentar.");
            alert.showAndWait(); // Muestra la ventana y espera a que la cierren
            return;

        } else if (!(Verificacion.verificarUsuario(usuario, passwordActual))) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initStyle(StageStyle.UNDECORATED);
            alert.setTitle("Error al completar los campos");
            alert.setHeaderText(null); // Sin encabezado extra
            alert.setContentText("Su contrasena actual ingresada no coincide con la registrada.\nPor favor, vuelve a intentar.");
            alert.showAndWait(); // Muestra la ventana y espera a que la cierren
            actual.clear();
            return;

        } else if (!(passwordNueva.equals(passwordCon))) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initStyle(StageStyle.UNDECORATED);
            alert.setTitle("Error al completar los campos");
            alert.setHeaderText(null); // Sin encabezado extra
            alert.setContentText("Su nueva contrasena no coincide con la del campo de confirmacion.\nPor favor, vuelve a intentar.");
            alert.showAndWait(); // Muestra la ventana y espera a que la cierren
            nueva.clear();
            confirmacion.clear();
            return;

        } else {
            hecho = crudUser.modificarPassword(usuario, passwordActual, passwordNueva);
        }

        if (hecho) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.initStyle(StageStyle.UNDECORATED);
            alert.setTitle("Operacion completada");
            alert.setHeaderText(null); // Sin encabezado extra
            alert.setContentText("La contrasena ha sido correctamente modificada.");
            alert.showAndWait(); // Muestra la ventana y espera a que la cierren

            actual.clear();
            nueva.clear();
            confirmacion.clear();

            //Cerrar la ventana despues de que la operacion haya sido un exito
            Stage stageCuenta = (Stage) btnCancelar.getScene().getWindow();
            stageCuenta.close();

        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initStyle(StageStyle.UNDECORATED);
            alert.setTitle("Error en la operacion");
            alert.setHeaderText(null); // Sin encabezado extra
            alert.setContentText("Su contrasena no pudo ser cambiada.\nSi sigue viendo este error contacte al encargado.");
            alert.showAndWait(); // Muestra la ventana y espera a que la cierren
        }

    }

    @FXML
    private void handleCancelar(ActionEvent event) {
        // Cerrar login y mostrar Principal nuevamente
        Stage stageCuenta = (Stage) btnCancelar.getScene().getWindow();
        stageCuenta.close();

    }
    
    public static String getPasswordActual() {
        return passwordActual;
    }

}
