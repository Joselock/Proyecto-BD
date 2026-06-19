package com.mycompany.gestorui.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.mycompany.gestorui.components.MainWindow;
import com.mycompany.gestorui.model.login.loginSevice.Verificacion;
import com.mycompany.gestorui.model.utils.VentanaManager;
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
    private ImageView toggleIcon;
    
    private boolean passwordVisible = false;
    
    private final Image EYE_OPEN = new Image(getClass().getResourceAsStream("/com/mycompany/gestorui/images/eye-open.png"));
    private final Image EYE_CLOSED = new Image(getClass().getResourceAsStream("/com/mycompany/gestorui/images/eye-closed.png"));
    
    @FXML
    private void togglePasswordVisibility(MouseEvent event) {
        if (passwordVisible) {
            String currentPassword = visiblePasswordField.getText();
            passwordField.setText(currentPassword);
            passwordField.setVisible(true);
            passwordField.setManaged(true);
            visiblePasswordField.setVisible(false);
            visiblePasswordField.setManaged(false);
            toggleIcon.setImage(EYE_OPEN);  
            passwordVisible = false;
        } else {
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
            alert.setHeaderText(null);
            alert.setContentText("Todos los campos son obligatorios.\nPor favor, completa tu usuario y contraseña.");
            alert.showAndWait();
            return;
            
        } else if (Verificacion.verificarUsuario(username, password)) {
            try {
                usuarioActual = username;
                Stage stageLogin = (Stage) usernameField.getScene().getWindow();
                stageLogin.close();
                
                PrincipalController.cerrarVentanaPrincipal();
                
                // Cerrar cualquier ventana abierta antes de abrir MainWindow
                VentanaManager.getInstance().cerrarTodas();
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
        Stage stageLogin = (Stage) btnCancelar.getScene().getWindow();
        stageLogin.close();
        
        Stage stagePrincipal = PrincipalController.getStagePrincipal();
        if (stagePrincipal != null) {
            stagePrincipal.show();
        }
    }
    
    @FXML
    private void handleMinimize(MouseEvent event){
        Stage stage = (Stage) ((Label) event.getSource()).getScene().getWindow();
        stage.setIconified(true);
    }
    
    @FXML
    private void handleClose(MouseEvent event) {
        Stage stage = (Stage) X.getScene().getWindow();
        stage.close();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
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
        
        usernameField.textProperty().addListener((obs, oldVal, newVal) -> {
            errorLabel.setVisible(false);
        });
        
        passwordField.textProperty().addListener((obs, oldVal, newVal) -> {
            errorLabel.setVisible(false);
        });
        
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