package com.mycompany.gestorui.controllers.Registro;

import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.mycompany.gestorui.components.Principal;
import com.mycompany.gestorui.controllers.PrincipalController;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class RegistroPersonalController implements Initializable {

    // Almacenar datos personales estáticamente para pasarlos al siguiente paso
    private static String nombreCompleto;
    private static String especialidad;
    private static String direccion;
    private static String telefono;

    @FXML
    private JFXTextField txtNombreCompleto;
    @FXML
    private JFXTextField txtEspecialidad;
    @FXML
    private JFXTextField txtDireccion;
    @FXML
    private JFXTextField txtTelefono;
    @FXML
    private JFXButton btnContinuar;
    @FXML
    private JFXButton btnCancelar;
    @FXML
    private Label cerrar;
    @FXML
    private Label minimizar;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Limpiar datos anteriores
        nombreCompleto = null;
        especialidad = null;
        direccion = null;
        telefono = null;
    }

    @FXML
    private void handleContinuar(ActionEvent event) {
        // Validar campos
        if (txtNombreCompleto.getText().trim().isEmpty()) {
            mostrarError("El nombre completo es obligatorio");
            return;
        }
        if (txtEspecialidad.getText().trim().isEmpty()) {
            mostrarError("La especialidad es obligatoria");
            return;
        }
        if (txtDireccion.getText().trim().isEmpty()) {
            mostrarError("La dirección es obligatoria");
            return;
        }
        if (txtTelefono.getText().trim().isEmpty()) {
            mostrarError("El teléfono es obligatorio");
            return;
        }

        // Guardar datos personales
        nombreCompleto = txtNombreCompleto.getText().trim();
        especialidad = txtEspecialidad.getText().trim();
        direccion = txtDireccion.getText().trim();
        telefono = txtTelefono.getText().trim();

        // Abrir ventana de credenciales
        try {
            Stage stageActual = (Stage) btnContinuar.getScene().getWindow();
            stageActual.close();

            Stage stage = new Stage();
            Parent root = FXMLLoader.load(
                    getClass().getResource("/com/mycompany/gestorui/views/Registro/RegistroCredencialesFXML.fxml"));
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error al abrir siguiente paso");
        }
    }

    @FXML
    private void handleCancelar(ActionEvent event) {
        // Obtener la ventana actual
        Stage stageActual = (Stage) btnCancelar.getScene().getWindow();
        stageActual.close();

        // Mostrar ventana principal
        try {
            Stage stagePrincipal = PrincipalController.getStagePrincipal();
            if (stagePrincipal != null) {
                // Verificar si la ventana aún existe y no está cerrada
                if (!stagePrincipal.isShowing()) {
                    // Si está oculta, mostrarla
                    stagePrincipal.show();
                } else {
                    // Si ya está visible, asegurarnos de que esté al frente
                    stagePrincipal.toFront();
                }
            } else {
                // Si no hay referencia, crear una nueva ventana principal
                Principal.mostrarVentanaPrincipal();
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Si hay error, crear una nueva ventana principal
            Principal.mostrarVentanaPrincipal();
        }
    }

    @FXML
    private void handleMinimize(MouseEvent event) {
        Stage stage = (Stage) ((Label) event.getSource()).getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    private void handleClose(MouseEvent event) {
        Stage stage = (Stage) cerrar.getScene().getWindow();
        stage.close();

        // Mostrar ventana principal
        /*Stage stagePrincipal = PrincipalController.getStagePrincipal();
        if (stagePrincipal != null) {
            stagePrincipal.show();
        }*/
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initStyle(StageStyle.UNDECORATED);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    // Métodos estáticos para obtener los datos personales
    public static String getNombreCompleto() {
        return nombreCompleto;
    }

    public static String getEspecialidad() {
        return especialidad;
    }

    public static String getDireccion() {
        return direccion;
    }

    public static String getTelefono() {
        return telefono;
    }
}