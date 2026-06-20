
/*package com.mycompany.gestorui.controllers.Cuenta;

import com.jfoenix.controls.JFXButton;
import com.mycompany.gestorui.components.Cuenta;
import com.mycompany.gestorui.controllers.LoginController;
import com.mycompany.gestorui.model.login.entidad.User;
import com.mycompany.gestorui.model.login.loginSevice.Verificacion;
import com.mycompany.gestorui.model.utils.VentanaManager;

import java.io.File;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PopupControl;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


public class CuentaController implements Initializable {

    @FXML
    private Label nombre;

    @FXML
    private Label gmail;

    @FXML
    private Label especialidad;

    @FXML
    private Label direccion;

    @FXML
    private Label telefono;

    @FXML
    private Label cerrar;

    @FXML
    private Label minimizar;

    @FXML
    private Circle avatarCircle;

    @FXML
    private ImageView avatarImage;

    @FXML
    private JFXButton btnChangeAvatar;

    @FXML
    private Button btnMenu;

    @FXML
    private Label lblUsername;

    private PopupControl popupMenu;
    private VBox menuContent;

    private String usuarioActual;
    private boolean tieneImagen = false;

    private Circle clipCircle; // Círculo separado para el clip

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Obtener el usuario actual desde LoginController
        usuarioActual = LoginController.getUsuarioActual();

        // Mostrar el usuario en el Label
        if (lblUsername != null) {
            lblUsername.setText(usuarioActual != null ? usuarioActual : "Usuario");
        }

        // ✅ CREAR UN CÍRCULO NUEVO EXCLUSIVAMENTE PARA EL CLIP
        clipCircle = new Circle(84); // 84 es el radio del círculo
        clipCircle.setCenterX(84);
        clipCircle.setCenterY(84);

        // Aplicar el clip al ImageView (usando el círculo nuevo, no el de la UI)
        avatarImage.setClip(clipCircle);
        avatarImage.setPreserveRatio(false);
        avatarImage.setFitWidth(168);
        avatarImage.setFitHeight(168);

        cargarAvatarGuardado();
        try {
            cargarDatosUsuario();
        } catch (SQLException ex) {
            System.getLogger(CuentaController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
        crearMenu();
        actualizarBotonAvatar();
    }

    // Método para cambiar o borrar la imagen del avatar
    @FXML
    private void handleChangeAvatar() {
        if (tieneImagen) {
            borrarAvatar();
        } else {
            seleccionarNuevaImagen();
        }
    }

    private void seleccionarNuevaImagen() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar imagen de perfil");

        FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter(
                "Archivos de imagen", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp");
        fileChooser.getExtensionFilters().add(imageFilter);

        Stage stage = (Stage) btnChangeAvatar.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            try {
                Image image = new Image(selectedFile.toURI().toString(), 168, 168, false, true);
                avatarImage.setImage(image);

                String destino = System.getProperty("user.home") + "/.gestorui/avatars/";
                File destinoDir = new File(destino);
                if (!destinoDir.exists()) {
                    destinoDir.mkdirs();
                }

                String usuario = usuarioActual != null ? usuarioActual : "default";
                File destFile = new File(destino + "avatar_" + usuario + ".png");

                java.nio.file.Files.copy(
                        selectedFile.toPath(),
                        destFile.toPath(),
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING);

                tieneImagen = true;
                actualizarBotonAvatar();
                System.out.println("Avatar guardado en: " + destFile.getAbsolutePath());

            } catch (Exception e) {
                System.err.println("Error al cargar o guardar la imagen: " + e.getMessage());
            }
        }
    }

    private void borrarAvatar() {
        avatarImage.setImage(null);

        String usuario = usuarioActual != null ? usuarioActual : "default";
        String avatarPath = System.getProperty("user.home") + "/.gestorui/avatars/avatar_" + usuario + ".png";
        File avatarFile = new File(avatarPath);

        if (avatarFile.exists()) {
            avatarFile.delete();
            System.out.println("Avatar borrado: " + avatarPath);
        }

        tieneImagen = false;
        actualizarBotonAvatar();
    }

    private void actualizarBotonAvatar() {
        if (tieneImagen) {
            btnChangeAvatar.setText("🗑️ Borrar foto");
            btnChangeAvatar.setStyle(
                    "-fx-background-color: #d32f2f; -fx-text-fill: white; -fx-background-radius: 15px; -fx-cursor: hand;");
        } else {
            btnChangeAvatar.setText("📷 Cambiar foto");
            btnChangeAvatar.setStyle(
                    "-fx-background-color: #2d7a2d; -fx-text-fill: white; -fx-background-radius: 15px; -fx-cursor: hand;");
        }
    }

    private void cargarAvatarGuardado() {
        String usuario = usuarioActual != null ? usuarioActual : "default";
        String avatarPath = System.getProperty("user.home") + "/.gestorui/avatars/avatar_" + usuario + ".png";
        File avatarFile = new File(avatarPath);

        if (avatarFile.exists()) {
            try {
                Image image = new Image(avatarFile.toURI().toString(), 168, 168, false, true);
                avatarImage.setImage(image);
                tieneImagen = true;
            } catch (Exception e) {
                System.err.println("Error al cargar avatar guardado: " + e.getMessage());
                tieneImagen = false;
            }
        } else {
            tieneImagen = false;
        }
    }

    private void cargarDatosUsuario() throws SQLException {

        User u = Verificacion.obtenerUsuario(usuarioActual);

        String correo = u.getEmail();
        String nom = u.getDatosUsuario().getNombreCompleto();
        String esp = u.getDatosUsuario().getEspecialidad();
        String dir = u.getDatosUsuario().getDireccion();
        String tel = u.getDatosUsuario().getTelefono();

        System.out.println(correo);
        System.out.println(nom);
        System.out.println(esp);
        System.out.println(dir);
        System.out.println(tel);

        nombre.setText("Nombre y Apellidos: " + nom);
        gmail.setText("Correo electronico: " + correo);
        especialidad.setText("Especialidad :" + esp);
        direccion.setText("Direccion: " + dir);
        telefono.setText("Telefono: " + tel);

    }

    private void crearMenu() {
        menuContent = new VBox();
        menuContent.getStyleClass().add("menu-popup");

        String cssUrl = getClass().getResource("/com/mycompany/gestorui/styles/menu.css").toExternalForm();
        menuContent.getStylesheets().add(cssUrl);

        Button itemPassword = crearItemMenu("Cambiar Contraseña", this::mostrarPassword);
        Button itemProfile = crearItemMenu("Modificar Perfil", this::mostrarProfile);
        Button itemEliminar = crearItemMenu("Eliminar Cuenta", this::mostrarEliminar);

        menuContent.getChildren().addAll(itemPassword, itemProfile, itemEliminar);

        popupMenu = new PopupControl();
        popupMenu.getScene().setRoot(menuContent);
        popupMenu.setAutoHide(true);
    }

    private Button crearItemMenu(String texto, javafx.event.EventHandler<ActionEvent> accion) {
        Button item = new Button(texto);
        item.getStyleClass().add("menu-item-custom");
        item.setMaxWidth(Double.MAX_VALUE);
        item.setOnAction(e -> {
            accion.handle(e);
            if (popupMenu != null) {
                popupMenu.hide();
            }
        });
        return item;
    }

    @FXML
    private void mostrarMenu(ActionEvent event) {
        if (popupMenu == null) {
            crearMenu();
        }

        Point2D point = btnMenu.localToScreen(0, btnMenu.getHeight());
        double x = point.getX();
        double y = point.getY();

        Stage stage = (Stage) btnMenu.getScene().getWindow();
        double menuWidth = popupMenu.getWidth() > 0 ? popupMenu.getWidth() : 180;
        double windowRightEdge = stage.getX() + stage.getWidth();

        if (x + menuWidth > windowRightEdge) {
            x = windowRightEdge - menuWidth - 10;
        }

        popupMenu.show(btnMenu, x, y);
    }

    

    // En CuentaController, cuando se abren las ventanas secundarias
    @FXML
    private void mostrarPassword(ActionEvent event) {
        try {
            Stage stageActual = (Stage) btnMenu.getScene().getWindow();
            VentanaManager.getInstance().abrirVentanaModal(
                    VentanaManager.VENTANA_CAMBIO_PASSWORD,
                    "/com/mycompany/gestorui/views/Cuenta/CambiarContraseña.fxml",
                    "Cambiar Contraseña",
                    stageActual);
        } catch (Exception ex) {
            System.getLogger(CuentaController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }

    @FXML
    private void mostrarProfile(ActionEvent event) {
        try {
            Stage stageActual = (Stage) btnMenu.getScene().getWindow();
            VentanaManager.getInstance().abrirVentanaModal(
                    VentanaManager.VENTANA_MODIFICAR_PERFIL,
                    "/com/mycompany/gestorui/views/Cuenta/CambiarDatos.fxml",
                    "Modificar Perfil",
                    stageActual);
        } catch (Exception ex) {
            System.getLogger(CuentaController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }

    @FXML
    private void mostrarEliminar(ActionEvent event) {
        try {
            Cuenta.eliminarCuenta(usuarioActual);
        } catch (SQLException ex) {
            System.getLogger(CuentaController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }

    @FXML
    private void handleMinimize(MouseEvent event) {
        Stage stage = (Stage) ((Label) event.getSource()).getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    private void handleClose(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}*/

package com.mycompany.gestorui.controllers.Cuenta;

import com.jfoenix.controls.JFXButton;
import com.mycompany.gestorui.components.Cuenta;
import com.mycompany.gestorui.controllers.LoginController;
import com.mycompany.gestorui.controllers.Cuenta.Listener.PerfilListener;
import com.mycompany.gestorui.model.login.entidad.User;
import com.mycompany.gestorui.model.login.loginSevice.Verificacion;
import com.mycompany.gestorui.model.utils.VentanaManager;

import java.io.File;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PopupControl;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class CuentaController implements Initializable {

    @FXML
    private Label nombre;

    @FXML
    private Label gmail;

    @FXML
    private Label especialidad;

    @FXML
    private Label direccion;

    @FXML
    private Label telefono;

    @FXML
    private Label cerrar;

    @FXML
    private Label minimizar;

    @FXML
    private Circle avatarCircle;

    @FXML
    private ImageView avatarImage;

    @FXML
    private JFXButton btnChangeAvatar;

    @FXML
    private Button btnMenu;

    @FXML
    private Label lblUsername;

    private PopupControl popupMenu;
    private VBox menuContent;

    private String usuarioActual;
    private boolean tieneImagen = false;
    private Circle clipCircle;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        usuarioActual = LoginController.getUsuarioActual();

        if (lblUsername != null) {
            lblUsername.setText(usuarioActual != null ? usuarioActual : "Usuario");
        }

        // Configurar el avatar
        clipCircle = new Circle(84);
        clipCircle.setCenterX(84);
        clipCircle.setCenterY(84);
        avatarImage.setClip(clipCircle);
        avatarImage.setPreserveRatio(false);
        avatarImage.setFitWidth(168);
        avatarImage.setFitHeight(168);

        cargarAvatarGuardado();
        
        try {
            cargarDatosUsuario();
        } catch (SQLException ex) {
            System.getLogger(CuentaController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
        
        crearMenu();
        actualizarBotonAvatar();

        // Registrar listener para actualizar cuando cambien los datos desde otra ventana
        PerfilListener.agregarListener(this::actualizarDatosDesdePerfil);
    }
    
    /**
     * Método que se ejecuta cuando el perfil cambia desde otra ventana
     */
    private void actualizarDatosDesdePerfil() {
        Platform.runLater(() -> {
            System.out.println("🔄 Actualizando datos de perfil...");
            try {
                // Actualizar usuario actual
                usuarioActual = LoginController.getUsuarioActual();
                if (lblUsername != null) {
                    lblUsername.setText(usuarioActual != null ? usuarioActual : "Usuario");
                }
                
                // Recargar datos del usuario
                cargarDatosUsuario();
                
                // Recargar avatar (por si cambió el nombre)
                cargarAvatarGuardado();
                
                System.out.println("✅ Datos de perfil actualizados");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }

    private void seleccionarNuevaImagen() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar imagen de perfil");

        FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter(
                "Archivos de imagen", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp");
        fileChooser.getExtensionFilters().add(imageFilter);

        Stage stage = (Stage) btnChangeAvatar.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            try {
                Image image = new Image(selectedFile.toURI().toString(), 168, 168, false, true);
                avatarImage.setImage(image);

                // Guardar con el nombre de usuario ACTUAL (no el original)
                String destino = System.getProperty("user.home") + "/.gestorui/avatars/";
                File destinoDir = new File(destino);
                if (!destinoDir.exists()) {
                    destinoDir.mkdirs();
                }

                // Usar el usuario actual como identificador para el avatar
                String usuario = LoginController.getUsuarioActual();
                if (usuario == null || usuario.isEmpty()) {
                    usuario = "default";
                }
                File destFile = new File(destino + "avatar_" + usuario + ".png");

                java.nio.file.Files.copy(
                        selectedFile.toPath(),
                        destFile.toPath(),
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING);

                tieneImagen = true;
                actualizarBotonAvatar();
                System.out.println("Avatar guardado en: " + destFile.getAbsolutePath());

            } catch (Exception e) {
                System.err.println("Error al cargar o guardar la imagen: " + e.getMessage());
            }
        }
    }

    private void borrarAvatar() {
        avatarImage.setImage(null);

        String usuario = LoginController.getUsuarioActual();
        if (usuario == null || usuario.isEmpty()) {
            usuario = "default";
        }
        String avatarPath = System.getProperty("user.home") + "/.gestorui/avatars/avatar_" + usuario + ".png";
        File avatarFile = new File(avatarPath);

        if (avatarFile.exists()) {
            avatarFile.delete();
            System.out.println("Avatar borrado: " + avatarPath);
        }

        tieneImagen = false;
        actualizarBotonAvatar();
    }

    private void actualizarBotonAvatar() {
        if (tieneImagen) {
            btnChangeAvatar.setText("🗑️ Borrar foto");
            btnChangeAvatar.setStyle(
                    "-fx-background-color: #d32f2f; -fx-text-fill: white; -fx-background-radius: 15px; -fx-cursor: hand;");
        } else {
            btnChangeAvatar.setText("📷 Cambiar foto");
            btnChangeAvatar.setStyle(
                    "-fx-background-color: #2d7a2d; -fx-text-fill: white; -fx-background-radius: 15px; -fx-cursor: hand;");
        }
    }

    private void cargarAvatarGuardado() {
        String usuario = LoginController.getUsuarioActual();
        if (usuario == null || usuario.isEmpty()) {
            usuario = "default";
        }
        String avatarPath = System.getProperty("user.home") + "/.gestorui/avatars/avatar_" + usuario + ".png";
        File avatarFile = new File(avatarPath);

        if (avatarFile.exists()) {
            try {
                Image image = new Image(avatarFile.toURI().toString(), 168, 168, false, true);
                avatarImage.setImage(image);
                tieneImagen = true;
            } catch (Exception e) {
                System.err.println("Error al cargar avatar guardado: " + e.getMessage());
                tieneImagen = false;
            }
        } else {
            tieneImagen = false;
        }
    }

    private void cargarDatosUsuario() throws SQLException {
        String usuario = LoginController.getUsuarioActual();
        if (usuario == null || usuario.isEmpty()) {
            return;
        }
        
        User u = Verificacion.obtenerUsuario(usuario);
        if (u == null) return;

        String correo = u.getEmail();
        String nom = u.getDatosUsuario().getNombreCompleto();
        String esp = u.getDatosUsuario().getEspecialidad();
        String dir = u.getDatosUsuario().getDireccion();
        String tel = u.getDatosUsuario().getTelefono();

        nombre.setText("Nombre y Apellidos: " + (nom != null ? nom : ""));
        gmail.setText("Correo electrónico: " + (correo != null ? correo : ""));
        especialidad.setText("Especialidad: " + (esp != null ? esp : ""));
        direccion.setText("Dirección: " + (dir != null ? dir : ""));
        telefono.setText("Teléfono: " + (tel != null ? tel : ""));
    }

    private void crearMenu() {
        menuContent = new VBox();
        menuContent.getStyleClass().add("menu-popup");

        String cssUrl = getClass().getResource("/com/mycompany/gestorui/styles/menu.css").toExternalForm();
        menuContent.getStylesheets().add(cssUrl);

        Button itemPassword = crearItemMenu("Cambiar Contraseña", this::mostrarPassword);
        Button itemProfile = crearItemMenu("Modificar Perfil", this::mostrarProfile);
        Button itemEliminar = crearItemMenu("Eliminar Cuenta", this::mostrarEliminar);

        menuContent.getChildren().addAll(itemPassword, itemProfile, itemEliminar);

        popupMenu = new PopupControl();
        popupMenu.getScene().setRoot(menuContent);
        popupMenu.setAutoHide(true);
    }

    private Button crearItemMenu(String texto, javafx.event.EventHandler<ActionEvent> accion) {
        Button item = new Button(texto);
        item.getStyleClass().add("menu-item-custom");
        item.setMaxWidth(Double.MAX_VALUE);
        item.setOnAction(e -> {
            accion.handle(e);
            if (popupMenu != null) {
                popupMenu.hide();
            }
        });
        return item;
    }

    @FXML
    private void mostrarMenu(ActionEvent event) {
        if (popupMenu == null) {
            crearMenu();
        }

        Point2D point = btnMenu.localToScreen(0, btnMenu.getHeight());
        double x = point.getX();
        double y = point.getY();

        Stage stage = (Stage) btnMenu.getScene().getWindow();
        double menuWidth = popupMenu.getWidth() > 0 ? popupMenu.getWidth() : 180;
        double windowRightEdge = stage.getX() + stage.getWidth();

        if (x + menuWidth > windowRightEdge) {
            x = windowRightEdge - menuWidth - 10;
        }

        popupMenu.show(btnMenu, x, y);
    }

    @FXML
    private void mostrarPassword(ActionEvent event) {
        try {
            Stage stageActual = (Stage) btnMenu.getScene().getWindow();
            VentanaManager.getInstance().abrirVentanaModal(
                    VentanaManager.VENTANA_CAMBIO_PASSWORD,
                    "/com/mycompany/gestorui/views/Cuenta/CambiarContraseña.fxml",
                    "Cambiar Contraseña",
                    stageActual);
        } catch (Exception ex) {
            System.getLogger(CuentaController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }

    @FXML
    private void mostrarProfile(ActionEvent event) {
        try {
            Stage stageActual = (Stage) btnMenu.getScene().getWindow();
            VentanaManager.getInstance().abrirVentanaModal(
                    VentanaManager.VENTANA_MODIFICAR_PERFIL,
                    "/com/mycompany/gestorui/views/Cuenta/CambiarDatos.fxml",
                    "Modificar Perfil",
                    stageActual);
        } catch (Exception ex) {
            System.getLogger(CuentaController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }

    @FXML
    private void mostrarEliminar(ActionEvent event) {
        try {
            Cuenta.eliminarCuenta(LoginController.getUsuarioActual());
        } catch (SQLException ex) {
            System.getLogger(CuentaController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }

    @FXML
    private void handleChangeAvatar() {
        if (tieneImagen) {
            borrarAvatar();
        } else {
            seleccionarNuevaImagen();
        }
    }

    @FXML
    private void handleMinimize(MouseEvent event) {
        Stage stage = (Stage) ((Label) event.getSource()).getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    private void handleClose(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}