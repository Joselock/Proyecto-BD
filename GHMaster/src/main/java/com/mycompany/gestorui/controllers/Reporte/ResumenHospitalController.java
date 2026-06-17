package com.mycompany.gestorui.controllers.Reporte;

import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import com.mycompany.gestorui.model.entidades.Hospital;
import com.mycompany.gestorui.model.services.reportes.HospitalService;

public class ResumenHospitalController implements Initializable {

    @FXML
    private TableView<HospitalRow> tablaHospitales;
    
    @FXML
    private TableColumn<HospitalRow, String> colNombre;
    
    @FXML
    private TableColumn<HospitalRow, String> colDepartamentos;
    
    @FXML
    private TableColumn<HospitalRow, String> colUnidades;
    
    @FXML
    private TableColumn<HospitalRow, String> colMedicos;
    
    @FXML
    private TableColumn<HospitalRow, String> colPacientes;
    
    @FXML
    private Label lblTotal;
    
    @FXML
    private Label lblInfo;
    
    private ObservableList<HospitalRow> hospitalesData = FXCollections.observableArrayList();
    private HospitalService hospitalService = new HospitalService();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColumnas();
        cargarDatos();
    }
    
    private void configurarColumnas() {
        colNombre.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
        colDepartamentos.setCellValueFactory(cellData -> cellData.getValue().departamentosProperty());
        colUnidades.setCellValueFactory(cellData -> cellData.getValue().unidadesProperty());
        colMedicos.setCellValueFactory(cellData -> cellData.getValue().medicosProperty());
        colPacientes.setCellValueFactory(cellData -> cellData.getValue().pacientesProperty());
        
        tablaHospitales.setItems(hospitalesData);
    }
    
    private void cargarDatos() {
        LinkedList<Hospital> hospitales = hospitalService.resumenHospitales();
        
        hospitalesData.clear();
        int totalDepartamentos = 0;
        int totalUnidades = 0;
        int totalMedicos = 0;
        int totalPacientes = 0;
        
        for (Hospital h : hospitales) {
            hospitalesData.add(new HospitalRow(
                h.getNombreHos(),
                String.valueOf(h.getCantDep()),
                String.valueOf(h.getCantUni()),
                String.valueOf(h.getCantMed()),
                String.valueOf(h.getCantPac())
            ));
            totalDepartamentos += h.getCantDep();
            totalUnidades += h.getCantUni();
            totalMedicos += h.getCantMed();
            totalPacientes += h.getCantPac();
        }
        
        lblTotal.setText(String.format("Totales: %d hospitales | %d departamentos | %d unidades | %d médicos | %d pacientes",
            hospitales.size(), totalDepartamentos, totalUnidades, totalMedicos, totalPacientes));
        lblInfo.setText("✅ Resumen de " + hospitales.size() + " hospitales");
    }
    
    public static class HospitalRow {
        private final SimpleStringProperty nombre;
        private final SimpleStringProperty departamentos;
        private final SimpleStringProperty unidades;
        private final SimpleStringProperty medicos;
        private final SimpleStringProperty pacientes;
        
        public HospitalRow(String nombre, String departamentos, String unidades, String medicos, String pacientes) {
            this.nombre = new SimpleStringProperty(nombre != null ? nombre : "");
            this.departamentos = new SimpleStringProperty(departamentos != null ? departamentos : "0");
            this.unidades = new SimpleStringProperty(unidades != null ? unidades : "0");
            this.medicos = new SimpleStringProperty(medicos != null ? medicos : "0");
            this.pacientes = new SimpleStringProperty(pacientes != null ? pacientes : "0");
        }
        
        public SimpleStringProperty nombreProperty() { return nombre; }
        public SimpleStringProperty departamentosProperty() { return departamentos; }
        public SimpleStringProperty unidadesProperty() { return unidades; }
        public SimpleStringProperty medicosProperty() { return medicos; }
        public SimpleStringProperty pacientesProperty() { return pacientes; }
    }
}