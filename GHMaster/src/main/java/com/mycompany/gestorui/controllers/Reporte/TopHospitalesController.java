/*package com.mycompany.gestorui.controllers.Reporte;

import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;

import com.mycompany.gestorui.model.entidades.Hospital;
import com.mycompany.gestorui.model.services.reportes.HospitalService;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;

public class TopHospitalesController implements Initializable {

    @FXML
    private BarChart<String, Number> chartTopHospitales;
    
    @FXML
    private Label lblInfo;
    
    private HospitalService hospitalService = new HospitalService();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarDatos();
    }
    
    private void cargarDatos() {
        try {
            LinkedList<Hospital> hospitales = hospitalService.hospitalesMayorCantidadPacientes();
            
            if (hospitales == null || hospitales.isEmpty()) {
                lblInfo.setText("⚠️ No hay hospitales con más de 100 pacientes");
                return;
            }
            
            chartTopHospitales.getData().clear();
            
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Pacientes");
            
            String[] colores = {"#2d7a2d", "#4a9e4a", "#6bb86b", "#8ccd8c", "#ade0ad"};
            
            for (int i = 0; i < hospitales.size(); i++) {
                Hospital h = hospitales.get(i);
                String nombre = h.getNombreHos() != null ? h.getNombreHos() : "Sin nombre";
                int cantidad = h.getCantPac();
                
                XYChart.Data<String, Number> data = new XYChart.Data<>(nombre, cantidad);
                series.getData().add(data);
                
                final int index = i;
                data.nodeProperty().addListener((obs, oldNode, newNode) -> {
                    if (newNode != null && index < colores.length) {
                        newNode.setStyle("-fx-bar-fill: " + colores[index] + ";");
                    }
                });
            }
            
            chartTopHospitales.getData().add(series);
            lblInfo.setText("🏆 Mostrando " + hospitales.size() + " hospitales con más de 100 pacientes");
            
        } catch (Exception ex) {
            lblInfo.setText("❌ Error al cargar datos: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}*/


package com.mycompany.gestorui.controllers.Reporte;

import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;

import com.mycompany.gestorui.model.entidades.Hospital;
import com.mycompany.gestorui.model.services.reportes.HospitalService;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.text.Font;

public class TopHospitalesController implements Initializable {

    @FXML
    private BarChart<String, Number> chartTopHospitales;
    
    @FXML
    private Label lblInfo;
    
    private HospitalService hospitalService = new HospitalService();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarDatos();
    }
    
    private void cargarDatos() {
        try {
            LinkedList<Hospital> hospitales = hospitalService.hospitalesMayorCantidadPacientes();
            
            if (hospitales == null || hospitales.isEmpty()) {
                lblInfo.setText("⚠️ No hay hospitales con más de 100 pacientes");
                return;
            }
            
            chartTopHospitales.getData().clear();
            
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Pacientes");
            
            String[] colores = {"#2d7a2d", "#4a9e4a", "#6bb86b", "#8ccd8c", "#ade0ad"};
            
            for (int i = 0; i < hospitales.size(); i++) {
                Hospital h = hospitales.get(i);
                // === SOLUCIÓN: Acortar nombres ===
                String nombreCompleto = h.getNombreHos() != null ? h.getNombreHos() : "Sin nombre";
                String nombreCorto = acortarNombre(nombreCompleto);
                int cantidad = h.getCantPac();
                
                XYChart.Data<String, Number> data = new XYChart.Data<>(nombreCorto, cantidad);
                series.getData().add(data);
                
                // === Agregar tooltip con nombre completo ===
                Tooltip tooltip = new Tooltip(nombreCompleto + " (" + cantidad + " pacientes)");
                tooltip.setFont(new Font(12));
                Tooltip.install(data.getNode(), tooltip);
                
                final int index = i;
                data.nodeProperty().addListener((obs, oldNode, newNode) -> {
                    if (newNode != null && index < colores.length) {
                        newNode.setStyle("-fx-bar-fill: " + colores[index] + ";" +
                                       "-fx-cursor: hand;");
                    }
                });
            }
            
            chartTopHospitales.getData().add(series);
            
            // === Mejorar la visualización del eje X ===
            chartTopHospitales.getXAxis().setTickLabelRotation(-30); // Rotar etiquetas
            chartTopHospitales.getXAxis().setTickLabelFont(new Font(11));
            
            lblInfo.setText("🏆 Mostrando " + hospitales.size() + " hospitales con más de 100 pacientes");
            
        } catch (Exception ex) {
            lblInfo.setText("❌ Error al cargar datos: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    /**
     * Método para acortar nombres largos de hospitales
     */
    private String acortarNombre(String nombreCompleto) {
        if (nombreCompleto == null || nombreCompleto.length() <= 20) {
            return nombreCompleto;
        }
        
        // Reemplazar palabras comunes por abreviaturas
        String abreviado = nombreCompleto
            .replace("Hospital", "Hosp.")
            .replace("General", "Gral.")
            .replace("Universitario", "Univ.")
            .replace("Internacional", "Intl.")
            .replace("Nacional", "Nac.")
            .replace("Centro", "Ctro.")
            .replace("Regional", "Reg.")
            .replace("Especialidades", "Esp.");
        
        // Si sigue siendo muy largo, truncar
        if (abreviado.length() > 25) {
            abreviado = abreviado.substring(0, 25) + "...";
        }
        
        return abreviado;
    }
}