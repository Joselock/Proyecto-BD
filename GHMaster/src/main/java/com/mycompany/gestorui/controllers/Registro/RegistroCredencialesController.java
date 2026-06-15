package com.mycompany.gestorui.controllers.Registro;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.mycompany.gestorui.controllers.PrincipalController;
import com.mycompany.gestorui.model.login.loginSevice.crudUser;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class RegistroCredencialesController implements Initializable {

    @FXML
    private JFXTextField txtNombreUsuario;
    @FXML
    private JFXTextField txtGmail;
    @FXML
    private JFXPasswordField txtPassword;
    @FXML
    private JFXTextField txtPasswordVisible;
    @FXML
    private JFXPasswordField txtConfirmPassword;
    @FXML
    private JFXTextField txtConfirmPasswordVisible;
    @FXML
    private Label btnTogglePassword1;
    @FXML
    private ImageView eyeIcon1;
    @FXML
    private Label btnTogglePassword2;
    @FXML
    private ImageView eyeIcon2;
    @FXML
    private JFXButton btnRegistrar;
    @FXML
    private JFXButton btnAtras;
    @FXML
    private JFXButton btnCancelar;
    @FXML
    private Label cerrar;
    @FXML
    private Label minimizar;

    private boolean password1Visible = false;
    private boolean password2Visible = false;

    private final Image EYE_OPEN = new Image(getClass().getResourceAsStream("/com/mycompany/gestorui/images/eye-open.png"));
    private final Image EYE_CLOSED = new Image(getClass().getResourceAsStream("/com/mycompany/gestorui/images/eye-closed.png"));

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Sincronizar textos
        txtPasswordVisible.textProperty().addListener((obs, oldVal, newVal) -> {
            if (password1Visible) {
                txtPassword.setText(newVal);
            }
        });
        txtPassword.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!password1Visible) {
                txtPasswordVisible.setText(newVal);
            }
        });
        txtConfirmPasswordVisible.textProperty().addListener((obs, oldVal, newVal) -> {
            if (password2Visible) {
                txtConfirmPassword.setText(newVal);
            }
        });
        txtConfirmPassword.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!password2Visible) {
                txtConfirmPasswordVisible.setText(newVal);
            }
        });
    }

    @FXML
    private void togglePasswordVisibility1(MouseEvent event) {
        if (password1Visible) {
            txtPassword.setText(txtPasswordVisible.getText());
            txtPassword.setVisible(true);
            txtPassword.setManaged(true);
            txtPasswordVisible.setVisible(false);
            txtPasswordVisible.setManaged(false);
            eyeIcon1.setImage(EYE_CLOSED);
            password1Visible = false;
        } else {
            txtPasswordVisible.setText(txtPassword.getText());
            txtPasswordVisible.setVisible(true);
            txtPasswordVisible.setManaged(true);
            txtPassword.setVisible(false);
            txtPassword.setManaged(false);
            eyeIcon1.setImage(EYE_OPEN);
            password1Visible = true;
        }
    }

    @FXML
    private void togglePasswordVisibility2(MouseEvent event) {
        if (password2Visible) {
            txtConfirmPassword.setText(txtConfirmPasswordVisible.getText());
            txtConfirmPassword.setVisible(true);
            txtConfirmPassword.setManaged(true);
            txtConfirmPasswordVisible.setVisible(false);
            txtConfirmPasswordVisible.setManaged(false);
            eyeIcon2.setImage(EYE_CLOSED);
            password2Visible = false;
        } else {
            txtConfirmPasswordVisible.setText(txtConfirmPassword.getText());
            txtConfirmPasswordVisible.setVisible(true);
            txtConfirmPasswordVisible.setManaged(true);
            txtConfirmPassword.setVisible(false);
            txtConfirmPassword.setManaged(false);
            eyeIcon2.setImage(EYE_OPEN);
            password2Visible = true;
        }
    }

    @FXML
    private void handleRegistrar(ActionEvent event) throws SQLException {
        String username = txtNombreUsuario.getText().trim();
        String email = txtGmail.getText().trim();
        String password = password1Visible ? txtPasswordVisible.getText() : txtPassword.getText();
        String confirmPassword = password2Visible ? txtConfirmPasswordVisible.getText() : txtConfirmPassword.getText();

        // Validaciones
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            mostrarAlerta("Verificacion de campos", "Todos los campos son obligatorios");
            return;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            mostrarAlerta("Formato inválido", "Ingrese un correo electrónico válido");
            return;
        }

        if (password.length() < 6) {
            mostrarAlerta("Contraseña débil", "La contraseña debe tener al menos 6 caracteres");
            return;
        }

        if (!password.equals(confirmPassword)) {
            mostrarAlerta("Verificacion de contraseñas", "Las contraseñas no coinciden");
            return;
        }

        // Obtener datos personales del paso anterior
        String nombreCompleto = RegistroPersonalController.getNombreCompleto();
        String especialidad = RegistroPersonalController.getEspecialidad();
        String direccion = RegistroPersonalController.getDireccion();
        String telefono = RegistroPersonalController.getTelefono();

        // Registrar con todos los datos
        boolean hecho = crudUser.registrarUsuario(
                username, email, password, nombreCompleto, especialidad, direccion, telefono);
        
        System.out.println(hecho);
        System.out.println("Estoy AQUIIIIIIIIIIIIIIIIIIIIIIIIIImelisa");

        if (hecho) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.initStyle(StageStyle.UNDECORATED);
            alert.setTitle("Registro exitoso");
            alert.setHeaderText(null);
            alert.setContentText("Cuenta creada correctamente");
            alert.showAndWait();

            // Cerrar ventana
            Stage stage = (Stage) btnRegistrar.getScene().getWindow();
            stage.close();

            // Mostrar Principal
            Stage stagePrincipal = PrincipalController.getStagePrincipal();
            if (stagePrincipal != null) {
                System.out.println("valor del stage:" + stagePrincipal);
                stagePrincipal.show();
            } else {
                System.err.println("stagePrincipal es NULL");
                // Si es null, crear una nueva ventana principal
                try {
                    Parent root = FXMLLoader.load(getClass().getResource("/com/mycompany/gestorui/views/PrincipalFXML.fxml"));
                    Stage newStage = new Stage();
                    newStage.initStyle(StageStyle.UNDECORATED);
                    newStage.setScene(new Scene(root));
                    newStage.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @FXML
    private void handleAtras(ActionEvent event) {
        try {
            Stage stageActual = (Stage) btnAtras.getScene().getWindow();
            stageActual.close();

            Stage stage = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("/com/mycompany/gestorui/views/Registro/RegistroPersonalFXML.fxml"));
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancelar(ActionEvent event) {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();

        Stage stagePrincipal = PrincipalController.getStagePrincipal();
        if (stagePrincipal != null) {
            stagePrincipal.show();
        }
    }

    @FXML
    private void handleClose(MouseEvent event) {
        Stage stage = (Stage) cerrar.getScene().getWindow();
        stage.close();

        Stage stagePrincipal = PrincipalController.getStagePrincipal();
        if (stagePrincipal != null) {
            stagePrincipal.show();
        }
    }

    @FXML
    private void handleMinimize(MouseEvent event) {
        Stage stage = (Stage) minimizar.getScene().getWindow();
        stage.setIconified(true);
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.initStyle(StageStyle.UNDECORATED);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
