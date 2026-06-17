package com.mycompany.gestorui.controllers.Reporte;

import java.net.URL;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.ResourceBundle;

import com.mycompany.gestorui.model.entidades.Departamento;
import com.mycompany.gestorui.model.entidades.Hospital;
import com.mycompany.gestorui.model.entidades.Paciente;
import com.mycompany.gestorui.model.entidades.Unidad;
import com.mycompany.gestorui.model.services.reportes.PacienteService;

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
import javafx.scene.input.MouseEvent;

public class ListadoPacientesController implements Initializable {

    @FXML
    private TableView<PacienteRow> tablaPacientes;
    
    @FXML
    private TableColumn<PacienteRow, String> colNumHistoria;
    
    @FXML
    private TableColumn<PacienteRow, String> colNombre;
    
    @FXML
    private TableColumn<PacienteRow, String> colFechaNac;
    
    @FXML
    private TableColumn<PacienteRow, String> colEdad;
    
    @FXML
    private TableColumn<PacienteRow, String> colDireccion;
    
    @FXML
    private TableColumn<PacienteRow, String> colHospital;
    
    @FXML
    private TableColumn<PacienteRow, String> colDepartamento;
    
    @FXML
    private TableColumn<PacienteRow, String> colUnidad;
    
    @FXML
    private ComboBox<String> cmbTipoBusqueda;
    
    @FXML
    private TextField txtCodigo;
    
    @FXML
    private Label lblTotal;
    
    @FXML
    private Label lblInfo;
    
    private ObservableList<PacienteRow> pacientesData = FXCollections.observableArrayList();
    private PacienteService pacienteService = new PacienteService();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColumnas();
        configurarComboBox();
        cargarDatosPorDefecto();
    }
    
    private void configurarColumnas() {
        colNumHistoria.setCellValueFactory(cellData -> cellData.getValue().numHistoriaProperty());
        colNombre.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
        colFechaNac.setCellValueFactory(cellData -> cellData.getValue().fechaNacProperty());
        colEdad.setCellValueFactory(cellData -> cellData.getValue().edadProperty());
        colDireccion.setCellValueFactory(cellData -> cellData.getValue().direccionProperty());
        colHospital.setCellValueFactory(cellData -> cellData.getValue().hospitalProperty());
        colDepartamento.setCellValueFactory(cellData -> cellData.getValue().departamentoProperty());
        colUnidad.setCellValueFactory(cellData -> cellData.getValue().unidadProperty());
        
        tablaPacientes.setItems(pacientesData);
    }
    
    private void configurarComboBox() {
        cmbTipoBusqueda.getItems().addAll("Hospital", "Departamento", "Unidad");
        cmbTipoBusqueda.setValue("Hospital");
    }
    
    private void cargarDatosPorDefecto() {
        txtCodigo.setText("H001");
        buscarPacientes(null);
    }
    
    @FXML
    private void buscarPacientes(MouseEvent event) {
        String tipo = cmbTipoBusqueda.getValue();
        String codigo = txtCodigo.getText().trim();
        
        if (codigo.isEmpty()) {
            lblInfo.setText("⚠️ Ingrese un código válido");
            return;
        }
        
        try {
            LinkedList<Hospital> hospitales = pacienteService.obtenerPacientesDepartamento(tipo, codigo);
            if (hospitales == null) {
                lblInfo.setText("❌ No se encontraron datos para " + tipo + ": " + codigo);
                return;
            }
            
            pacientesData.clear();
            int total = 0;
            
            for (Hospital h : hospitales) {
                if (h == null) continue;
                for (Departamento d : h.getDepartamentos()) {
                    if (d == null) continue;
                    for (Unidad u : d.getUnidades()) {
                        if (u == null) continue;
                        if (u.getRegistro() == null || u.getRegistro().getPacientes() == null) continue;
                        for (Paciente p : u.getRegistro().getPacientes()) {
                            if (p == null) continue;
                            LocalDate fechaLocal = convertirADateLocal(p.getFechaN());
                            
                            pacientesData.add(new PacienteRow(
                                p.getNumHisCli() != null ? p.getNumHisCli() : "",
                                p.getNombrePac() != null ? p.getNombrePac() : "",
                                p.getFechaN() != null ? p.getFechaN().toString() : "",
                                fechaLocal != null ? calcularEdad(fechaLocal) : "",
                                p.getDireccionP() != null ? p.getDireccionP() : "",
                                h.getNombreHos() != null ? h.getNombreHos() : "",
                                d.getNombreDep() != null ? d.getNombreDep() : "",
                                u.getNombreUni() != null ? u.getNombreUni() : ""
                            ));
                            total++;
                        }
                    }
                }
            }
            
            lblTotal.setText("Total de pacientes: " + total);
            lblInfo.setText("✅ " + total + " pacientes encontrados en " + tipo + ": " + codigo);
            
        } catch (Exception ex) {
            lblInfo.setText("❌ Error al buscar pacientes: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    private LocalDate convertirADateLocal(java.sql.Date fechaSql) {
        if (fechaSql == null) return null;
        return fechaSql.toLocalDate();
    }
    
    private String calcularEdad(LocalDate fechaNac) {
        if (fechaNac == null) return "";
        LocalDate hoy = LocalDate.now();
        int edad = hoy.getYear() - fechaNac.getYear();
        if (hoy.getMonthValue() < fechaNac.getMonthValue() || 
            (hoy.getMonthValue() == fechaNac.getMonthValue() && hoy.getDayOfMonth() < fechaNac.getDayOfMonth())) {
            edad--;
        }
        return String.valueOf(edad);
    }
    
    public static class PacienteRow {
        private final SimpleStringProperty numHistoria;
        private final SimpleStringProperty nombre;
        private final SimpleStringProperty fechaNac;
        private final SimpleStringProperty edad;
        private final SimpleStringProperty direccion;
        private final SimpleStringProperty hospital;
        private final SimpleStringProperty departamento;
        private final SimpleStringProperty unidad;
        
        public PacienteRow(String numHistoria, String nombre, String fechaNac, String edad,
                          String direccion, String hospital, String departamento, String unidad) {
            this.numHistoria = new SimpleStringProperty(numHistoria != null ? numHistoria : "");
            this.nombre = new SimpleStringProperty(nombre != null ? nombre : "");
            this.fechaNac = new SimpleStringProperty(fechaNac != null ? fechaNac : "");
            this.edad = new SimpleStringProperty(edad != null ? edad : "");
            this.direccion = new SimpleStringProperty(direccion != null ? direccion : "");
            this.hospital = new SimpleStringProperty(hospital != null ? hospital : "");
            this.departamento = new SimpleStringProperty(departamento != null ? departamento : "");
            this.unidad = new SimpleStringProperty(unidad != null ? unidad : "");
        }
        
        public SimpleStringProperty numHistoriaProperty() { return numHistoria; }
        public SimpleStringProperty nombreProperty() { return nombre; }
        public SimpleStringProperty fechaNacProperty() { return fechaNac; }
        public SimpleStringProperty edadProperty() { return edad; }
        public SimpleStringProperty direccionProperty() { return direccion; }
        public SimpleStringProperty hospitalProperty() { return hospital; }
        public SimpleStringProperty departamentoProperty() { return departamento; }
        public SimpleStringProperty unidadProperty() { return unidad; }
    }
}