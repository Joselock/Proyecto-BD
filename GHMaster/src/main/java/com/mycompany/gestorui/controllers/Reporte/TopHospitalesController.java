package com.mycompany.gestorui.controllers.Reporte;

import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;

import com.mycompany.gestorui.model.entidades.Hospital;
import com.mycompany.gestorui.model.services.reportes.HospitalService;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class TopHospitalesController implements Initializable {

    @FXML
    private TableView<TopHospitalRow> tablaHospitales;
    
    @FXML
    private TableColumn<TopHospitalRow, String> colPosicion;
    
    @FXML
    private TableColumn<TopHospitalRow, String> colNombre;
    
    @FXML
    private TableColumn<TopHospitalRow, String> colPacientes;
    
    @FXML
    private Label lblInfo;
    
    private ObservableList<TopHospitalRow> hospitalesData = FXCollections.observableArrayList();
    private HospitalService hospitalService = new HospitalService();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColumnas();
        cargarDatos();
    }
    
    private void configurarColumnas() {
        colPosicion.setCellValueFactory(cellData -> cellData.getValue().posicionProperty());
        colNombre.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
        colPacientes.setCellValueFactory(cellData -> cellData.getValue().pacientesProperty());
        
        tablaHospitales.setItems(hospitalesData);
    }
    
    private void cargarDatos() {
        LinkedList<Hospital> hospitales = hospitalService.hospitalesMayorCantidadPacientes();
        
        hospitalesData.clear();
        int posicion = 1;
        
        for (Hospital h : hospitales) {
            hospitalesData.add(new TopHospitalRow(
                "#" + posicion,
                h.getNombreHos(),
                String.valueOf(h.getCantPac())
            ));
            posicion++;
        }
        
        lblInfo.setText("🏆 Top " + hospitales.size() + " hospitales con más de 100 pacientes");
    }
    
    public static class TopHospitalRow {
        private final SimpleStringProperty posicion;
        private final SimpleStringProperty nombre;
        private final SimpleStringProperty pacientes;
        
        public TopHospitalRow(String posicion, String nombre, String pacientes) {
            this.posicion = new SimpleStringProperty(posicion != null ? posicion : "");
            this.nombre = new SimpleStringProperty(nombre != null ? nombre : "");
            this.pacientes = new SimpleStringProperty(pacientes != null ? pacientes : "0");
        }
        
        public SimpleStringProperty posicionProperty() { return posicion; }
        public SimpleStringProperty nombreProperty() { return nombre; }
        public SimpleStringProperty pacientesProperty() { return pacientes; }
    }
}