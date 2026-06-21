package com.mycompany.gestorui.controllers.Login;

import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.mycompany.gestorui.model.login.loginSevice.PasswordResetService;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ForgotPasswordController implements Initializable {

    @FXML
    private VBox step1Container;

    @FXML
    private VBox step2Container;

    @FXML
    private VBox step3Container;

    @FXML
    private JFXTextField emailField;

    @FXML
    private JFXTextField codigoField;

    @FXML
    private JFXPasswordField newPasswordField;

    @FXML
    private JFXPasswordField confirmPasswordField;

    @FXML
    private Label lblInfo;

    @FXML
    private JFXButton btnEnviarCodigo;

    @FXML
    private JFXButton btnVerificarCodigo;

    @FXML
    private JFXButton btnCambiarContraseña;

    @FXML
    private Label cerrar;

    private PasswordResetService resetService;
    private String emailActual;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        resetService = PasswordResetService.getInstance();
        
        step1Container.setVisible(true);
        step2Container.setVisible(false);
        step3Container.setVisible(false);
        
        lblInfo.setText("");
        
        emailField.setOnAction(e -> btnEnviarCodigo.requestFocus());
        codigoField.setOnAction(e -> btnVerificarCodigo.fire());
        newPasswordField.setOnAction(e -> confirmPasswordField.requestFocus());
        confirmPasswordField.setOnAction(e -> btnCambiarContraseña.fire());
    }

    @FXML
    private void handleEnviarCodigo() {
        String email = emailField.getText().trim();
        
        if (email.isEmpty()) {
            mostrarAlerta("Error", "Por favor ingresa tu correo electrónico.");
            return;
        }
        
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            mostrarAlerta("Error", "Por favor ingresa un correo electrónico válido.");
            return;
        }
        
        boolean enviado = resetService.solicitarRecuperacion(email);
        
        if (enviado) {
            emailActual = email;
            lblInfo.setText("✅ Se ha enviado un código de verificación a tu correo.");
            lblInfo.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
            
            step1Container.setVisible(false);
            step2Container.setVisible(true);
            step3Container.setVisible(false);
            codigoField.clear();
            
        } else {
            lblInfo.setText("❌ No se encontró un usuario con ese correo.");
            lblInfo.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
        }
    }

    @FXML
    private void handleVerificarCodigo() {
        String codigo = codigoField.getText().trim();
        
        if (codigo.isEmpty()) {
            mostrarAlerta("Error", "Por favor ingresa el código de verificación.");
            return;
        }
        
        if (codigo.length() != 6) {
            mostrarAlerta("Error", "El código debe tener 6 dígitos.");
            return;
        }
        
        boolean valido = resetService.verificarToken(emailActual, codigo);
        
        if (valido) {
            lblInfo.setText("✅ Código verificado correctamente.");
            lblInfo.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
            
            step2Container.setVisible(false);
            step3Container.setVisible(true);
            newPasswordField.clear();
            confirmPasswordField.clear();
            
        } else {
            lblInfo.setText("❌ Código inválido o expirado. Intenta nuevamente.");
            lblInfo.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
        }
    }

    @FXML
    private void handleCambiarContraseña() {
        String nuevaPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        
        if (nuevaPassword.isEmpty() || confirmPassword.isEmpty()) {
            mostrarAlerta("Error", "Por favor completa todos los campos.");
            return;
        }
        
        if (nuevaPassword.length() < 6) {
            mostrarAlerta("Error", "La contraseña debe tener al menos 6 caracteres.");
            return;
        }
        
        if (!nuevaPassword.equals(confirmPassword)) {
            mostrarAlerta("Error", "Las contraseñas no coinciden.");
            return;
        }
        
        boolean cambiada = resetService.cambiarContraseña(emailActual, nuevaPassword);
        
        if (cambiada) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.initStyle(StageStyle.UNDECORATED);
            alert.setTitle("Éxito");
            alert.setHeaderText(null);
            alert.setContentText("✅ Tu contraseña ha sido cambiada exitosamente.");
            alert.showAndWait();
            
            Stage stage = (Stage) btnCambiarContraseña.getScene().getWindow();
            stage.close();
            
        } else {
            mostrarAlerta("Error", "❌ Ocurrió un error al cambiar la contraseña. Intenta nuevamente.");
        }
    }

    @FXML
    private void handleVolverStep1() {
        step1Container.setVisible(true);
        step2Container.setVisible(false);
        step3Container.setVisible(false);
        lblInfo.setText("");
        emailField.clear();
        codigoField.clear();
        newPasswordField.clear();
        confirmPasswordField.clear();
    }

    @FXML
    private void handleVolverStep2() {
        step2Container.setVisible(true);
        step3Container.setVisible(false);
        lblInfo.setText("Ingresa el código que recibiste en tu correo.");
        lblInfo.setStyle("-fx-text-fill: #2c3e50;");
        codigoField.clear();
    }

    @FXML
    private void handleVolverLogin() {
        Stage stage = (Stage) emailField.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleClose(MouseEvent event) {
        Stage stage = (Stage) cerrar.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initStyle(StageStyle.UNDECORATED);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}