/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.gestorui.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.mycompany.gestorui.components.MainWindow;
import com.mycompany.gestorui.model.login.loginSevice.Verificacion;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class LoginController implements Initializable {
    
    private static String usuarioActual;
    
    @FXML
    private Label X;
    
    @FXML
    private Label errorLabel;
    
    @FXML
    private JFXTextField usernameField;
    
    @FXML
    private JFXPasswordField passwordField;
    
    @FXML
    private JFXTextField visiblePasswordField;
    
    @FXML
    private Label togglePasswordLabel;
    
    @FXML
    private JFXButton btnIniciar;
    
    @FXML
    private JFXButton btnCancelar;
    
    @FXML
    private ImageView toggleIcon;  // ← Referencia a la imagen dentro del Label    
    private boolean passwordVisible = false;
    
    // Cargar las imágenes una sola vez
    private final Image EYE_OPEN = new Image(getClass().getResourceAsStream("/com/mycompany/gestorui/images/eye-open.png"));
    private final Image EYE_CLOSED = new Image(getClass().getResourceAsStream("/com/mycompany/gestorui/images/eye-closed.png"));
    
    @FXML
    private void togglePasswordVisibility(MouseEvent event) {
        if (passwordVisible) {
            // Cambiar a modo oculto
            String currentPassword = visiblePasswordField.getText();
            passwordField.setText(currentPassword);
            passwordField.setVisible(true);
            passwordField.setManaged(true);
            visiblePasswordField.setVisible(false);
            visiblePasswordField.setManaged(false);
            toggleIcon.setImage(EYE_OPEN);  
            passwordVisible = false;
        } else {
            // Cambiar a modo visible
            String currentPassword = passwordField.getText();
            visiblePasswordField.setText(currentPassword);
            visiblePasswordField.setVisible(true);
            visiblePasswordField.setManaged(true);
            passwordField.setVisible(false);
            passwordField.setManaged(false);
            toggleIcon.setImage(EYE_CLOSED);  
            passwordVisible = true;
        }
    }
    
    @FXML
    private void handleLogin(ActionEvent event) throws SQLException {
        String username = usernameField.getText();
        String password = passwordField.getText();
        errorLabel.setVisible(false);
        
        if (username.isEmpty() || password.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initStyle(StageStyle.UNDECORATED);
            alert.setTitle("Error de inicio de sesión");
            alert.setHeaderText(null); // Sin encabezado extra
            alert.setContentText("Todos los campos son obligatorios.\nPor favor, completa tu usuario y contraseña.");
            alert.showAndWait(); // Muestra la ventana y espera a que la cierren
            return;
            
        }else if (Verificacion.verificarUsuario(username,password)) {
            try {
                
                usuarioActual = username;
                // 1. Cerrar ventana de login
                Stage stageLogin = (Stage) usernameField.getScene().getWindow();
                stageLogin.close();
                
                // 2. Cerrar la ventana Principal 
                PrincipalController.cerrarVentanaPrincipal();
                
                // 3. Abrir MainWindow (ventana principal de la app)
                MainWindow.mostrarVentanaPrincipal();
                
            } catch (Exception e) {
                errorLabel.setText("Error al abrir ventana principal");
                errorLabel.setVisible(true);
            }
        } else {
            errorLabel.setVisible(true);
        }
    }
    
    @FXML
    private void handleCancelar(ActionEvent event) {
        // Cerrar login y mostrar Principal nuevamente
        Stage stageLogin = (Stage) btnCancelar.getScene().getWindow();
        stageLogin.close();
        
        // Mostrar Principal (que estaba oculta)
        Stage stagePrincipal = PrincipalController.getStagePrincipal();
        if (stagePrincipal != null) {
            stagePrincipal.show();
        }
    }
    
    @FXML
    private void handleMinimize(MouseEvent event){
        // Obtener el Stage (ventana) desde el elemento que disparó el evento
        Stage stage = (Stage) ((Label) event.getSource()).getScene().getWindow();
        // Minimizar la ventana
        stage.setIconified(true);
    }
    
    
    @FXML
    private void handleClose(MouseEvent event) {
        Stage stage = (Stage) X.getScene().getWindow();
        stage.close();
    }
    
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Sincronizar los textos entre ambos campos
        visiblePasswordField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (passwordVisible) {
                passwordField.setText(newVal);
            }
        });
        
        passwordField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!passwordVisible) {
                visiblePasswordField.setText(newVal);
            }
        });
        
        // Ocultar mensaje de error cuando el usuario empieza a escribir en el campo de usuario
        usernameField.textProperty().addListener((obs, oldVal, newVal) -> {
            errorLabel.setVisible(false);
        });
        
        // Ocultar mensaje de error cuando el usuario empieza a escribir en el campo de contraseña
        passwordField.textProperty().addListener((obs, oldVal, newVal) -> {
            errorLabel.setVisible(false);
        });
        
        // Asegurar que el mensaje de error comienza oculto
        errorLabel.setVisible(false);
        
        System.out.println("LoginController inicializado correctamente");
    }
    
    public static String getUsuarioActual() {
        return usuarioActual;
    }

    public static void setUsuarioActual(String usuarioActual) {
        LoginController.usuarioActual = usuarioActual;
    }
    
    
    
}