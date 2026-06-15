/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.gestorui.controllers.Cuenta;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.mycompany.gestorui.controllers.LoginController;
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
public class CambiarDatosController implements Initializable {

    @FXML
    private JFXTextField username;
    
    @FXML
    private JFXTextField gmail;
    
    @FXML
    private JFXTextField nombre;

    @FXML
    private JFXTextField especialidad;

    @FXML
    private JFXTextField direccion;

    @FXML
    private JFXTextField telefono;

    @FXML
    private JFXButton btnCambiar;

    @FXML
    private JFXButton btnCancelar;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }
    
    @FXML
    private void handleChange(ActionEvent event) throws SQLException {
        String user = username.getText();
        String correo = gmail.getText();
        String nombreC = nombre.getText();
        String direccionP = direccion.getText();
        String espe = especialidad.getText();
        String tele = telefono.getText();
        boolean hecho = false;

        String usuario = LoginController.getUsuarioActual();

        if (user.isEmpty() || correo.isEmpty() || nombreC.isEmpty() || direccionP.isEmpty() || espe.isEmpty() || tele.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initStyle(StageStyle.UNDECORATED);
            alert.setTitle("Error al completar los campos");
            alert.setHeaderText(null); // Sin encabezado extra
            alert.setContentText("Todos los campos son obligatorios.\nPor favor, vuelve a intentar.");
            alert.showAndWait(); // Muestra la ventana y espera a que la cierren
            return;

        }else {
            hecho = crudUser.modificarDatos(usuario, user, correo, nombreC, espe, direccionP, tele);
        }

   
        if (hecho) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.initStyle(StageStyle.UNDECORATED);
            alert.setTitle("Operacion completada");
            alert.setHeaderText(null); // Sin encabezado extra
            alert.setContentText("Los datos del usuario han sido correctamente modificados.");
            alert.showAndWait(); // Muestra la ventana y espera a que la cierren
            
            LoginController.setUsuarioActual(user);

            username.clear();
            gmail.clear();
            nombre.clear();
            direccion.clear();
            especialidad.clear();
            telefono.clear();

            //Cerrar la ventana despues de que la operacion haya sido un exito
            Stage stageCuenta = (Stage) btnCancelar.getScene().getWindow();
            stageCuenta.close();

        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initStyle(StageStyle.UNDECORATED);
            alert.setTitle("Error en la operacion");
            alert.setHeaderText(null); // Sin encabezado extra
            alert.setContentText("Los datos no pudieron ser modificados.\nSi sigue viendo este error contacte al encargado.");
            alert.showAndWait(); // Muestra la ventana y espera a que la cierren
        }

    }

    @FXML
    private void handleCancelar(ActionEvent event) {
        // Cerrar login y mostrar Principal nuevamente
        Stage stageCuenta = (Stage) btnCancelar.getScene().getWindow();
        stageCuenta.close();

    }

}
