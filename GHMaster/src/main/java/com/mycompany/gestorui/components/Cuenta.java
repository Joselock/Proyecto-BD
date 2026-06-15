/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.gestorui.components;

import com.mycompany.gestorui.model.login.loginSevice.crudUser;
import java.io.File;
import java.sql.SQLException;
import java.util.Optional;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

/**
 *
 * @author ignacio
 */
public class Cuenta {

    private static Stage mainStage;

    public static void mostrarVentanaCuenta() throws Exception {
        // Crear un nuevo Stage
        mainStage = new Stage();

        // Cargar el FXML 
        Parent root = FXMLLoader.load(MainWindow.class.getResource("/com/mycompany/gestorui/views/Cuenta/Cuenta.fxml"));
        mainStage.initStyle(StageStyle.UNDECORATED);
        Scene scene = new Scene(root);
        mainStage.setScene(scene);
        mainStage.show();
    }

    public static void eliminarCuenta(String username) throws SQLException {

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.initStyle(StageStyle.UNDECORATED);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Estás seguro de que deseas eliminar tu cuenta?\nEsta acción es irreversible y eliminará todos tus datos.\n\n¿Deseas continuar?");

        // Personalizar los botones
        ButtonType btnSi = new ButtonType("Confirmar", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnNo = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirmacion.getButtonTypes().setAll(btnSi, btnNo);

        // Esperar respuesta
        Optional<ButtonType> resultado = confirmacion.showAndWait();

        // Si el usuario NO confirma, salir del método
        if (resultado.isEmpty() || resultado.get() != btnSi) {
            return;
        }

        boolean eliminado = false;

        eliminado = crudUser.eliminarUsuario(username);

        if (eliminado) {
            eliminarAvatarUsuario(username);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.initStyle(StageStyle.UNDECORATED);
            alert.setTitle("Confirmacion de operacion");
            alert.setHeaderText(null); // Sin encabezado extra
            alert.setContentText("La cuenta ha sido eliminada.");
            alert.showAndWait();

            // Cerrar TODAS las ventanas abiertas
            cerrarTodasLasVentanas();

            // Abrir la ventana principal 
            abrirVentanaPrincipal();

        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initStyle(StageStyle.UNDECORATED);
            alert.setTitle("Error de operacion.");
            alert.setHeaderText(null); // Sin encabezado extra
            alert.setContentText("No se pudo realizar la operacion.\nSi persiste el problema contacte con la persona a cargo.");
            alert.showAndWait();
        }

    }

    private static void eliminarAvatarUsuario(String username) {
        try {
            // Ruta donde se guardan los avatares
            String avatarPath = System.getProperty("user.home") + "/.gestorui/avatars/avatar_" + username + ".png";
            File avatarFile = new File(avatarPath);

            if (avatarFile.exists()) {
                boolean borrado = avatarFile.delete();
                if (borrado) {
                    System.out.println("Avatar eliminado correctamente: " + avatarPath);
                } else {
                    System.out.println("No se pudo eliminar el avatar: " + avatarPath);
                }
            } else {
                System.out.println("No existe avatar para el usuario: " + username);
            }
        } catch (Exception e) {
            System.err.println("Error al eliminar el avatar: " + e.getMessage());
        }
    }

    private static void cerrarTodasLasVentanas() {
        Platform.runLater(() -> {
            // Crear una copia de las ventanas para evitar modificar la lista original
            java.util.List<Stage> ventanasParaCerrar = new java.util.ArrayList<>();

            for (Window window : Window.getWindows()) {
                if (window instanceof Stage) {
                    Stage stage = (Stage) window;
                    ventanasParaCerrar.add(stage);
                }
            }

            // Cerrar las ventanas de la copia
            for (Stage stage : ventanasParaCerrar) {
                if (stage.isShowing()) {
                    stage.close();
                }
            }
        });
    }

    private static void abrirVentanaPrincipal() {
        Platform.runLater(() -> {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Principal.mostrarVentanaPrincipal();
        });
    }
    
    public static void mostrarVentanaCambioContrasena() throws Exception {
        // Crear un nuevo Stage
        mainStage = new Stage();

        // Cargar el FXML 
        Parent root = FXMLLoader.load(MainWindow.class.getResource("/com/mycompany/gestorui/views/Cuenta/CambiarContraseña.fxml"));
        mainStage.initStyle(StageStyle.UNDECORATED);
        Scene scene = new Scene(root);
        mainStage.setScene(scene);
        mainStage.show();
    }
    
    public static void mostrarVentanaCambioDatos() throws Exception {
        // Crear un nuevo Stage
        mainStage = new Stage();

        // Cargar el FXML 
        Parent root = FXMLLoader.load(MainWindow.class.getResource("/com/mycompany/gestorui/views/Cuenta/CambiarDatos.fxml"));
        mainStage.initStyle(StageStyle.UNDECORATED);
        Scene scene = new Scene(root);
        mainStage.setScene(scene);
        mainStage.show();
    }

}
