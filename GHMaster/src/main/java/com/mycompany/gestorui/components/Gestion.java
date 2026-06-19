/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/*package com.mycompany.gestorui.components;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


public class Gestion {
    private static Stage mainStage;
    
    public static void mostrarVentanaGestion() throws Exception {
        // Crear un nuevo Stage
        mainStage = new Stage();
        
        // Cargar el FXML (ruta corregida)
        Parent root = FXMLLoader.load(MainWindow.class.getResource("/com/mycompany/gestorui/views/Gestion/Gestion.fxml"));
        mainStage.initStyle(StageStyle.UNDECORATED);
        Scene scene = new Scene(root);
        mainStage.setScene(scene);
        mainStage.show();
    }
    
    
}*/


package com.mycompany.gestorui.components;

import com.mycompany.gestorui.model.utils.VentanaManager;
import javafx.stage.Stage;

public class Gestion {
    
    private static boolean ventanaAbierta = false;
    
    public static void mostrarVentanaGestion() {
        // Obtener la ventana actual (MainWindow)
        Stage stagePadre = MainWindow.getStagePrincipal();
        
        if (stagePadre == null) {
            System.err.println("❌ Error: No se encontró la ventana padre (MainWindow)");
            return;
        }
        
        // Usar VentanaManager para abrir la ventana (solo una instancia)
        boolean exito = VentanaManager.getInstance().abrirVentanaModal(
            VentanaManager.VENTANA_GESTION,
            "/com/mycompany/gestorui/views/Gestion/Gestion.fxml",
            "Gestión Hospitalaria",
            stagePadre
        );
        
        if (!exito) {
            System.out.println("ℹ️ La ventana de Gestión ya está abierta");
        }
    }
    
    public static void cerrarVentanaGestion() {
        VentanaManager.getInstance().cerrarVentana(VentanaManager.VENTANA_GESTION);
        ventanaAbierta = false;
    }
}