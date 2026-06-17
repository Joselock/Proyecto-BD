package com.mycompany.gestorui.controllers.Reporte;

import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;
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
import javafx.application.Platform;

import com.mycompany.gestorui.model.entidades.Consulta;
import com.mycompany.gestorui.model.entidades.Departamento;
import com.mycompany.gestorui.model.entidades.Hospital;
import com.mycompany.gestorui.model.entidades.Paciente;
import com.mycompany.gestorui.model.entidades.Turno;
import com.mycompany.gestorui.model.entidades.Unidad;
import com.mycompany.gestorui.model.services.reportes.PacienteService;

public class NoAtendidosController implements Initializable {

    @FXML
    private TableView<NoAtendidoRow> tablaPacientes;
    
    @FXML
    private TableColumn<NoAtendidoRow, String> colHospital;
    
    @FXML
    private TableColumn<NoAtendidoRow, String> colDepartamento;
    
    @FXML
    private TableColumn<NoAtendidoRow, String> colUnidad;
    
    @FXML
    private TableColumn<NoAtendidoRow, String> colNumTurno;
    
    @FXML
    private TableColumn<NoAtendidoRow, String> colNumHistoria;
    
    @FXML
    private TableColumn<NoAtendidoRow, String> colNombre;
    
    @FXML
    private TableColumn<NoAtendidoRow, String> colDireccion;
    
    @FXML
    private TableColumn<NoAtendidoRow, String> colCausa;
    
    @FXML
    private TableColumn<NoAtendidoRow, String> colCantNoAtendidos;
    
    @FXML
    private ComboBox<String> cmbTipoBusqueda;
    
    @FXML
    private TextField txtCodigo;
    
    @FXML
    private Label lblTotal;
    
    @FXML
    private Label lblInfo;
    
    private ObservableList<NoAtendidoRow> pacientesData = FXCollections.observableArrayList();
    private PacienteService pacienteService = new PacienteService();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            configurarColumnas();
            configurarComboBox();
            cargarDatosPorDefecto();
        } catch (Exception ex) {
            lblInfo.setText("Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    private void configurarColumnas() {
        colHospital.setCellValueFactory(cellData -> cellData.getValue().hospitalProperty());
        colDepartamento.setCellValueFactory(cellData -> cellData.getValue().departamentoProperty());
        colUnidad.setCellValueFactory(cellData -> cellData.getValue().unidadProperty());
        colNumTurno.setCellValueFactory(cellData -> cellData.getValue().numTurnoProperty());
        colNumHistoria.setCellValueFactory(cellData -> cellData.getValue().numHistoriaProperty());
        colNombre.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
        colDireccion.setCellValueFactory(cellData -> cellData.getValue().direccionProperty());
        colCausa.setCellValueFactory(cellData -> cellData.getValue().causaProperty());
        colCantNoAtendidos.setCellValueFactory(cellData -> cellData.getValue().cantNoAtendidosProperty());
        
        tablaPacientes.setItems(pacientesData);
    }
    
    private void configurarComboBox() {
        cmbTipoBusqueda.getItems().addAll("Hospital", "Departamento", "Unidad");
        cmbTipoBusqueda.setValue("Hospital");
    }
    
    private void cargarDatosPorDefecto() {
        txtCodigo.setText("H001");
        buscarPacientes();
    }
    
    @FXML
    public void buscarPacientes() {
        String tipo = cmbTipoBusqueda.getValue();
        String codigo = txtCodigo.getText().trim();
        
        System.out.println("🔍 Buscando pacientes no atendidos - Tipo: " + tipo + ", Código: " + codigo);
        
        if (codigo.isEmpty()) {
            lblInfo.setText("⚠️ Ingrese un código válido");
            return;
        }
        
        lblInfo.setText("Buscando...");
        pacientesData.clear();
        
        Thread thread = new Thread(() -> {
            try {
                LinkedList<Hospital> hospitales = pacienteService.listadoNoAtendidos(tipo, codigo);
                
                System.out.println("📦 Hospitales recibidos: " + (hospitales != null ? hospitales.size() : "null"));
                
                if (hospitales == null) {
                    Platform.runLater(() -> {
                        lblInfo.setText("❌ El servicio devolvió null");
                        lblTotal.setText("Error: datos nulos");
                    });
                    return;
                }
                
                if (hospitales.isEmpty()) {
                    Platform.runLater(() -> {
                        lblInfo.setText("⚠️ No hay pacientes no atendidos");
                        lblTotal.setText("Total: 0 pacientes");
                    });
                    return;
                }
                
                int total = 0;
                
                // 🔥 RECORRER TODA LA ESTRUCTURA PARA EXTRAER TODOS LOS DATOS
                for (Hospital h : hospitales) {
                    if (h == null) continue;
                    String nombreHospital = h.getNombreHos() != null ? h.getNombreHos() : "";
                    System.out.println("🏥 Procesando hospital: " + nombreHospital);
                    
                    for (Departamento d : h.getDepartamentos()) {
                        if (d == null) continue;
                        String nombreDep = d.getNombreDep() != null ? d.getNombreDep() : "";
                        
                        for (Unidad u : d.getUnidades()) {
                            if (u == null) continue;
                            String nombreUnidad = u.getNombreUni() != null ? u.getNombreUni() : "";
                            
                            for (Turno t : u.getTurnos()) {
                                if (t == null) continue;
                                
                                Consulta c = t.getConsulta();
                                if (c != null) {
                                    Paciente p = c.getPaciente();
                                    
                                    NoAtendidoRow row = new NoAtendidoRow(
                                        nombreHospital,
                                        nombreDep,
                                        nombreUnidad,
                                        String.valueOf(t.getNumTurn()),
                                        p != null && p.getNumHisCli() != null ? p.getNumHisCli() : "",
                                        p != null && p.getNombrePac() != null ? p.getNombrePac() : "",
                                        p != null && p.getDireccionP() != null ? p.getDireccionP() : "",
                                        c.getCausa() != null ? c.getCausa() : "",
                                        String.valueOf(t.getCantNoAten())
                                    );
                                    pacientesData.add(row);
                                    total++;
                                }
                            }
                        }
                    }
                }
                
                System.out.println("✅ Total pacientes no atendidos: " + total);
                final int totalFinal = total;
                
                Platform.runLater(() -> {
                    lblInfo.setText("✅ " + totalFinal + " pacientes no atendidos en " + tipo + ": " + codigo);
                    lblTotal.setText("Total de pacientes no atendidos: " + totalFinal);
                });
                
            } catch (Exception ex) {
                System.err.println("❌ Error en el hilo: " + ex.getMessage());
                ex.printStackTrace();
                Platform.runLater(() -> {
                    lblInfo.setText("❌ Error: " + ex.getMessage());
                });
            }
        });
        
        thread.setDaemon(true);
        thread.start();
    }
    
    public static class NoAtendidoRow {
        private final SimpleStringProperty hospital;
        private final SimpleStringProperty departamento;
        private final SimpleStringProperty unidad;
        private final SimpleStringProperty numTurno;
        private final SimpleStringProperty numHistoria;
        private final SimpleStringProperty nombre;
        private final SimpleStringProperty direccion;
        private final SimpleStringProperty causa;
        private final SimpleStringProperty cantNoAtendidos;
        
        public NoAtendidoRow(String hospital, String departamento, String unidad,
                            String numTurno, String numHistoria, String nombre,
                            String direccion, String causa, String cantNoAtendidos) {
            this.hospital = new SimpleStringProperty(hospital != null ? hospital : "");
            this.departamento = new SimpleStringProperty(departamento != null ? departamento : "");
            this.unidad = new SimpleStringProperty(unidad != null ? unidad : "");
            this.numTurno = new SimpleStringProperty(numTurno != null ? numTurno : "");
            this.numHistoria = new SimpleStringProperty(numHistoria != null ? numHistoria : "");
            this.nombre = new SimpleStringProperty(nombre != null ? nombre : "");
            this.direccion = new SimpleStringProperty(direccion != null ? direccion : "");
            this.causa = new SimpleStringProperty(causa != null ? causa : "");
            this.cantNoAtendidos = new SimpleStringProperty(cantNoAtendidos != null ? cantNoAtendidos : "0");
        }
        
        public SimpleStringProperty hospitalProperty() { return hospital; }
        public SimpleStringProperty departamentoProperty() { return departamento; }
        public SimpleStringProperty unidadProperty() { return unidad; }
        public SimpleStringProperty numTurnoProperty() { return numTurno; }
        public SimpleStringProperty numHistoriaProperty() { return numHistoria; }
        public SimpleStringProperty nombreProperty() { return nombre; }
        public SimpleStringProperty direccionProperty() { return direccion; }
        public SimpleStringProperty causaProperty() { return causa; }
        public SimpleStringProperty cantNoAtendidosProperty() { return cantNoAtendidos; }
    }
}