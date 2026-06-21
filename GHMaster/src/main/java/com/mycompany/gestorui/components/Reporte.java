

package com.mycompany.gestorui.components;

import com.mycompany.gestorui.model.utils.VentanaManager;
import javafx.stage.Stage;

public class Reporte {
    
    public static void mostrarVentanaReportes() {
        // Obtener la ventana actual (MainWindow)
        Stage stagePadre = MainWindow.getStagePrincipal();
        
        if (stagePadre == null) {
            System.err.println("❌ Error: No se encontró la ventana padre (MainWindow)");
            return;
        }
        
        // Usar VentanaManager para abrir la ventana (solo una instancia)
        boolean exito = VentanaManager.getInstance().abrirVentanaModal(
            VentanaManager.VENTANA_REPORTES,
            "/com/mycompany/gestorui/views/Reportes/Reporte.fxml",
            "Reportes",
            stagePadre
        );
        
        if (!exito) {
            System.out.println("ℹ️ La ventana de Reportes ya está abierta");
        }
    }
    
    public static void cerrarVentanaReportes() {
        VentanaManager.getInstance().cerrarVentana(VentanaManager.VENTANA_REPORTES);
    }
}
