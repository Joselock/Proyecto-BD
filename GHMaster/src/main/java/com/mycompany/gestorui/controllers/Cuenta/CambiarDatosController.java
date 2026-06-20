package com.mycompany.gestorui.controllers.Cuenta;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.mycompany.gestorui.controllers.LoginController;
import com.mycompany.gestorui.controllers.Cuenta.Listener.PerfilListener;
import com.mycompany.gestorui.controllers.Cuenta.Manager.PerfilManager;
import com.mycompany.gestorui.model.login.loginSevice.crudUser;
import com.mycompany.gestorui.model.login.entidad.User;
import com.mycompany.gestorui.model.login.loginSevice.Verificacion;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

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

    private String usuarioOriginal;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Cargar datos actuales del usuario
        usuarioOriginal = LoginController.getUsuarioActual();
        cargarDatosActuales();
    }
    
    private void cargarDatosActuales() {
        try {
            User user = Verificacion.obtenerUsuario(usuarioOriginal);
            if (user != null) {
                username.setText(user.getUsername());
                gmail.setText(user.getEmail());
                nombre.setText(user.getDatosUsuario().getNombreCompleto());
                especialidad.setText(user.getDatosUsuario().getEspecialidad());
                direccion.setText(user.getDatosUsuario().getDireccion());
                telefono.setText(user.getDatosUsuario().getTelefono());
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    @FXML
    private void handleChange(ActionEvent event) throws SQLException {
        String nuevoUser = username.getText().trim();
        String correo = gmail.getText().trim();
        String nombreC = nombre.getText().trim();
        String direccionP = direccion.getText().trim();
        String espe = especialidad.getText().trim();
        String tele = telefono.getText().trim();
        boolean hecho = false;

        if (nuevoUser.isEmpty() || correo.isEmpty() || nombreC.isEmpty() || 
            direccionP.isEmpty() || espe.isEmpty() || tele.isEmpty()) {
            mostrarAlertaError("Todos los campos son obligatorios.\nPor favor, vuelve a intentar.");
            return;
        }

        hecho = crudUser.modificarDatos(usuarioOriginal, nuevoUser, correo, nombreC, espe, direccionP, tele);

        if (hecho) {
            // Actualizar el usuario en LoginController
            LoginController.setUsuarioActual(nuevoUser);
            
            // Actualizar los datos en PerfilManager
            PerfilManager.getInstance().setDatosUsuarioActual(
                Verificacion.obtenerUsuario(nuevoUser)
            );
            
            // NOTIFICAR A TODOS QUE EL PERFIL CAMBIÓ
            PerfilListener.notificarCambio();
            
            // Mostrar mensaje de éxito
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.initStyle(StageStyle.UNDECORATED);
            alert.setTitle("Operación completada");
            alert.setHeaderText(null);
            alert.setContentText("Los datos del usuario han sido correctamente modificados.");
            alert.showAndWait();

            // Cerrar la ventana
            Stage stageCuenta = (Stage) btnCancelar.getScene().getWindow();
            stageCuenta.close();

        } else {
            mostrarAlertaError("Los datos no pudieron ser modificados.\nSi sigue viendo este error contacte al encargado.");
        }
    }
    
    private void mostrarAlertaError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initStyle(StageStyle.UNDECORATED);
        alert.setTitle("Error al completar los campos");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    private void handleCancelar(ActionEvent event) {
        Stage stageCuenta = (Stage) btnCancelar.getScene().getWindow();
        stageCuenta.close();
    }
}