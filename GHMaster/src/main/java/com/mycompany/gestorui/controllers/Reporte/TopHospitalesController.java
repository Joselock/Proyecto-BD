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

public class TopHospitalesController implements Initializable {

    @FXML
    private BarChart<String, Number> chartTopHospitales;
    
    @FXML
    private Label lblInfo;
    
    // ELIMINA esta línea porque ya no existe en el FXML
    // @FXML
    // private Label lblTitulo;
    
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
}