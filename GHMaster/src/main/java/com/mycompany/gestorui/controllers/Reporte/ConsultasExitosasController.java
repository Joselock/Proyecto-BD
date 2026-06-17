package com.mycompany.gestorui.controllers.Reporte;

import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;

import com.mycompany.gestorui.model.entidades.Departamento;
import com.mycompany.gestorui.model.entidades.Hospital;
import com.mycompany.gestorui.model.entidades.Informe;
import com.mycompany.gestorui.model.entidades.Turno;
import com.mycompany.gestorui.model.entidades.Unidad;
import com.mycompany.gestorui.model.services.reportes.ConsultaService;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class ConsultasExitosasController implements Initializable {

    @FXML
    private TableView<ConsultaExitosaRow> tablaConsultas;
    
    @FXML
    private TableColumn<ConsultaExitosaRow, String> colHospital;
    
    @FXML
    private TableColumn<ConsultaExitosaRow, String> colDepartamento;
    
    @FXML
    private TableColumn<ConsultaExitosaRow, String> colUnidad;
    
    @FXML
    private TableColumn<ConsultaExitosaRow, String> colNumTurno;
    
    @FXML
    private TableColumn<ConsultaExitosaRow, String> colMedico;
    
    @FXML
    private TableColumn<ConsultaExitosaRow, String> colTotalPacAten;
    
    @FXML
    private TableColumn<ConsultaExitosaRow, String> colPacAtendTurnos;
    
    @FXML
    private ComboBox<String> cmbTipoBusqueda;
    
    @FXML
    private TextField txtCodigo;
    
    @FXML
    private Label lblTotal;
    
    @FXML
    private Label lblInfo;
    
    private ObservableList<ConsultaExitosaRow> consultasData = FXCollections.observableArrayList();
    private ConsultaService consultaService = new ConsultaService();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColumnas();
        configurarComboBox();
        cargarDatosPorDefecto();
    }
    
    private void configurarColumnas() {
        colHospital.setCellValueFactory(cellData -> cellData.getValue().hospitalProperty());
        colDepartamento.setCellValueFactory(cellData -> cellData.getValue().departamentoProperty());
        colUnidad.setCellValueFactory(cellData -> cellData.getValue().unidadProperty());
        colNumTurno.setCellValueFactory(cellData -> cellData.getValue().numTurnoProperty());
        colMedico.setCellValueFactory(cellData -> cellData.getValue().medicoProperty());
        colTotalPacAten.setCellValueFactory(cellData -> cellData.getValue().totalPacAtenProperty());
        colPacAtendTurnos.setCellValueFactory(cellData -> cellData.getValue().pacAtendTurnosProperty());
        
        tablaConsultas.setItems(consultasData);
    }
    
    private void configurarComboBox() {
        cmbTipoBusqueda.getItems().addAll("Hospital", "Unidad");
        cmbTipoBusqueda.setValue("Hospital");
        
        cmbTipoBusqueda.setOnAction(e -> buscarConsultas());
        txtCodigo.setOnAction(e -> buscarConsultas());
    }
    
    private void cargarDatosPorDefecto() {
        txtCodigo.setText("H001");
        buscarConsultas();
    }
    
    @FXML
    private void buscarConsultas() {
        String tipo = cmbTipoBusqueda.getValue();
        String codigo = txtCodigo.getText().trim();
        
        if (codigo.isEmpty()) {
            lblInfo.setText("⚠️ Ingrese un código válido");
            return;
        }
        
        try {
            LinkedList<Hospital> hospitales = consultaService.resumenConsultasExitosas(tipo, codigo);
            if (hospitales == null) {
                lblInfo.setText("❌ No se encontraron datos para " + tipo + ": " + codigo);
                return;
            }
            
            consultasData.clear();
            int total = 0;
            
            for (Hospital h : hospitales) {
                if (h == null) continue;
                for (Departamento d : h.getDepartamentos()) {
                    if (d == null) continue;
                    for (Unidad u : d.getUnidades()) {
                        if (u == null) continue;
                        int size = Math.min(u.getInformes().size(), u.getTurnos().size());
                        for (int i = 0; i < size; i++) {
                            Informe informe = u.getInformes().get(i);
                            Turno turno = u.getTurnos().get(i);
                            if (informe == null || turno == null) continue;
                            
                            String nombreMedico = "";
                            if (turno.getMedico() != null && turno.getMedico().getNombreMed() != null) {
                                nombreMedico = turno.getMedico().getNombreMed();
                            }
                            
                            consultasData.add(new ConsultaExitosaRow(
                                h.getNombreHos() != null ? h.getNombreHos() : "",
                                d.getNombreDep() != null ? d.getNombreDep() : "",
                                u.getNombreUni() != null ? u.getNombreUni() : "",
                                String.valueOf(turno.getNumTurn()),
                                nombreMedico,
                                String.valueOf(informe.getPacAtend()),
                                String.valueOf(turno.getCantAten())
                            ));
                            total++;
                        }
                    }
                }
            }
            
            lblTotal.setText("Total de consultas exitosas: " + total);
            lblInfo.setText("✅ " + total + " consultas exitosas en " + tipo + ": " + codigo);
            
        } catch (Exception ex) {
            lblInfo.setText("❌ Error al buscar consultas: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    public static class ConsultaExitosaRow {
        private final SimpleStringProperty hospital;
        private final SimpleStringProperty departamento;
        private final SimpleStringProperty unidad;
        private final SimpleStringProperty numTurno;
        private final SimpleStringProperty medico;
        private final SimpleStringProperty totalPacAten;
        private final SimpleStringProperty pacAtendTurnos;
        
        public ConsultaExitosaRow(String hospital, String departamento, String unidad, String numTurno,
                                 String medico, String totalPacAten, String pacAtendTurnos) {
            this.hospital = new SimpleStringProperty(hospital != null ? hospital : "");
            this.departamento = new SimpleStringProperty(departamento != null ? departamento : "");
            this.unidad = new SimpleStringProperty(unidad != null ? unidad : "");
            this.numTurno = new SimpleStringProperty(numTurno != null ? numTurno : "");
            this.medico = new SimpleStringProperty(medico != null ? medico : "");
            this.totalPacAten = new SimpleStringProperty(totalPacAten != null ? totalPacAten : "0");
            this.pacAtendTurnos = new SimpleStringProperty(pacAtendTurnos != null ? pacAtendTurnos : "0");
        }
        
        public SimpleStringProperty hospitalProperty() { return hospital; }
        public SimpleStringProperty departamentoProperty() { return departamento; }
        public SimpleStringProperty unidadProperty() { return unidad; }
        public SimpleStringProperty numTurnoProperty() { return numTurno; }
        public SimpleStringProperty medicoProperty() { return medico; }
        public SimpleStringProperty totalPacAtenProperty() { return totalPacAten; }
        public SimpleStringProperty pacAtendTurnosProperty() { return pacAtendTurnos; }
    }
}