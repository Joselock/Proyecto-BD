package com.mycompany.gestorui.controllers.Reporte;

import java.net.URL;
import java.time.format.DateTimeFormatter;
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
import javafx.application.Platform;

import com.mycompany.gestorui.model.entidades.Departamento;
import com.mycompany.gestorui.model.entidades.Hospital;
import com.mycompany.gestorui.model.entidades.Informe;
import com.mycompany.gestorui.model.entidades.Turno;
import com.mycompany.gestorui.model.entidades.Unidad;
import com.mycompany.gestorui.model.services.reportes.HospitalService;

public class ResumenProcesoController implements Initializable {

    @FXML
    private TableView<ProcesoRow> tablaProceso;
    
    @FXML
    private TableColumn<ProcesoRow, String> colHospital;
    
    @FXML
    private TableColumn<ProcesoRow, String> colDepartamento;
    
    @FXML
    private TableColumn<ProcesoRow, String> colUnidad;
    
    @FXML
    private TableColumn<ProcesoRow, String> colNumTurno;
    
    @FXML
    private TableColumn<ProcesoRow, String> colHoraInforme;
    
    @FXML
    private TableColumn<ProcesoRow, String> colPacInicio;
    
    @FXML
    private TableColumn<ProcesoRow, String> colPacAtendidos;
    
    @FXML
    private TableColumn<ProcesoRow, String> colPacTotal;
    
    @FXML
    private TableColumn<ProcesoRow, String> colPorcentajeAtend;
    
    @FXML
    private TableColumn<ProcesoRow, String> colPacNoAtendidos;
    
    @FXML
    private TableColumn<ProcesoRow, String> colPacAlta;
    
    @FXML
    private TableColumn<ProcesoRow, String> colPacExtranjero;
    
    @FXML
    private TableColumn<ProcesoRow, String> colPacProvincia;
    
    @FXML
    private TableColumn<ProcesoRow, String> colPacOtraUnidad;
    
    @FXML
    private TableColumn<ProcesoRow, String> colPacOtrasCausas;
    
    @FXML
    private TableColumn<ProcesoRow, String> colPacDesconoce;
    
    @FXML
    private Label lblTotal;
    
    @FXML
    private Label lblInfo;
    
    private ObservableList<ProcesoRow> procesoData = FXCollections.observableArrayList();
    private HospitalService hospitalService = new HospitalService();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.out.println("=== INICIALIZANDO ResumenProcesoController ===");
        try {
            configurarColumnas();
            cargarDatos();
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
        colHoraInforme.setCellValueFactory(cellData -> cellData.getValue().horaInformeProperty());
        colPacInicio.setCellValueFactory(cellData -> cellData.getValue().pacInicioProperty());
        colPacAtendidos.setCellValueFactory(cellData -> cellData.getValue().pacAtendidosProperty());
        colPacTotal.setCellValueFactory(cellData -> cellData.getValue().pacTotalProperty());
        colPorcentajeAtend.setCellValueFactory(cellData -> cellData.getValue().porcentajeAtendProperty());
        colPacNoAtendidos.setCellValueFactory(cellData -> cellData.getValue().pacNoAtendidosProperty());
        colPacAlta.setCellValueFactory(cellData -> cellData.getValue().pacAltaProperty());
        colPacExtranjero.setCellValueFactory(cellData -> cellData.getValue().pacExtranjeroProperty());
        colPacProvincia.setCellValueFactory(cellData -> cellData.getValue().pacProvinciaProperty());
        colPacOtraUnidad.setCellValueFactory(cellData -> cellData.getValue().pacOtraUnidadProperty());
        colPacOtrasCausas.setCellValueFactory(cellData -> cellData.getValue().pacOtrasCausasProperty());
        colPacDesconoce.setCellValueFactory(cellData -> cellData.getValue().pacDesconoceProperty());
        
        tablaProceso.setItems(procesoData);
        System.out.println("✅ Columnas configuradas correctamente");
    }
    
    private void cargarDatos() {
        System.out.println("📊 Cargando datos de ResumenProceso...");
        lblInfo.setText("Cargando datos...");
        procesoData.clear();
        
        Thread thread = new Thread(() -> {
            try {
                System.out.println("🔄 Llamando a hospitalService.resumenProceso()...");
                LinkedList<Hospital> hospitales = hospitalService.resumenProceso();
                
                System.out.println("📦 Datos recibidos: " + (hospitales != null ? hospitales.size() : "null"));
                
                if (hospitales == null) {
                    Platform.runLater(() -> {
                        lblInfo.setText("❌ El servicio devolvió null");
                        lblTotal.setText("Error: datos nulos");
                    });
                    return;
                }
                
                if (hospitales.isEmpty()) {
                    Platform.runLater(() -> {
                        lblInfo.setText("⚠️ No hay datos disponibles");
                        lblTotal.setText("Total: 0 registros");
                    });
                    return;
                }
                
                int totalRegistros = 0;
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                
                for (Hospital h : hospitales) {
                    if (h == null) continue;
                    String nombreHospital = h.getNombreHos() != null ? h.getNombreHos() : "Sin nombre";
                    System.out.println("🏥 Procesando hospital: " + nombreHospital);
                    
                    for (Departamento d : h.getDepartamentos()) {
                        if (d == null) continue;
                        String nombreDep = d.getNombreDep() != null ? d.getNombreDep() : "Sin nombre";
                        
                        for (Unidad u : d.getUnidades()) {
                            if (u == null) continue;
                            String nombreUnidad = u.getNombreUni() != null ? u.getNombreUni() : "Sin nombre";
                            
                            int size = Math.min(u.getInformes().size(), u.getTurnos().size());
                            System.out.println("   📋 Unidad: " + nombreUnidad + " - Informes: " + u.getInformes().size() + ", Turnos: " + u.getTurnos().size());
                            
                            for (int i = 0; i < size; i++) {
                                Informe informe = u.getInformes().get(i);
                                Turno turno = u.getTurnos().get(i);
                                if (informe == null || turno == null) continue;
                                
                                String hora = "";
                                if (informe.getHora() != null) {
                                    hora = informe.getHora().toLocalTime().format(formatter);
                                }
                                
                                float porcentaje = informe.getPorcentajePacAtend();
                                String porcentajeStr = String.format("%.1f%%", porcentaje);
                                
                                ProcesoRow procesoRow = new ProcesoRow(
                                    nombreHospital,
                                    nombreDep,
                                    nombreUnidad,
                                    String.valueOf(turno.getNumTurn()),
                                    hora,
                                    String.valueOf(informe.getCantIni()),
                                    String.valueOf(informe.getPacAtend()),
                                    String.valueOf(informe.getTotal()),
                                    porcentajeStr,
                                    String.valueOf(turno.getCantNoAten()),
                                    String.valueOf(informe.getPacAlta()),
                                    String.valueOf(informe.getCantPacExtranjero()),
                                    String.valueOf(informe.getCantPacProvincia()),
                                    String.valueOf(informe.getCantPacOtraUnidad()),
                                    String.valueOf(informe.getCantPacOtrasCausas()),
                                    String.valueOf(informe.getCantPacDesconoce())
                                );
                                procesoData.add(procesoRow);
                                totalRegistros++;
                            }
                        }
                    }
                }
                
                System.out.println("✅ Total de registros procesados: " + totalRegistros);
                
                final int totalFinal = totalRegistros;
                Platform.runLater(() -> {
                    lblInfo.setText("✅ " + totalFinal + " registros cargados");
                    lblTotal.setText("Total de registros de proceso: " + totalFinal);
                    System.out.println("🖥️ UI actualizada con " + totalFinal + " registros");
                });
                
            } catch (Exception ex) {
                System.err.println("❌ Error en el hilo: " + ex.getMessage());
                ex.printStackTrace();
                Platform.runLater(() -> {
                    lblInfo.setText("❌ Error al cargar: " + ex.getMessage());
                });
            }
        });
        
        thread.setDaemon(true);
        thread.start();
    }
    
    public static class ProcesoRow {
        private final SimpleStringProperty hospital;
        private final SimpleStringProperty departamento;
        private final SimpleStringProperty unidad;
        private final SimpleStringProperty numTurno;
        private final SimpleStringProperty horaInforme;
        private final SimpleStringProperty pacInicio;
        private final SimpleStringProperty pacAtendidos;
        private final SimpleStringProperty pacTotal;
        private final SimpleStringProperty porcentajeAtend;
        private final SimpleStringProperty pacNoAtendidos;
        private final SimpleStringProperty pacAlta;
        private final SimpleStringProperty pacExtranjero;
        private final SimpleStringProperty pacProvincia;
        private final SimpleStringProperty pacOtraUnidad;
        private final SimpleStringProperty pacOtrasCausas;
        private final SimpleStringProperty pacDesconoce;
        
        public ProcesoRow(String hospital, String departamento, String unidad, 
                         String numTurno, String horaInforme, String pacInicio,
                         String pacAtendidos, String pacTotal, String porcentajeAtend,
                         String pacNoAtendidos, String pacAlta, String pacExtranjero,
                         String pacProvincia, String pacOtraUnidad, String pacOtrasCausas,
                         String pacDesconoce) {
            this.hospital = new SimpleStringProperty(hospital != null ? hospital : "");
            this.departamento = new SimpleStringProperty(departamento != null ? departamento : "");
            this.unidad = new SimpleStringProperty(unidad != null ? unidad : "");
            this.numTurno = new SimpleStringProperty(numTurno != null ? numTurno : "");
            this.horaInforme = new SimpleStringProperty(horaInforme != null ? horaInforme : "");
            this.pacInicio = new SimpleStringProperty(pacInicio != null ? pacInicio : "0");
            this.pacAtendidos = new SimpleStringProperty(pacAtendidos != null ? pacAtendidos : "0");
            this.pacTotal = new SimpleStringProperty(pacTotal != null ? pacTotal : "0");
            this.porcentajeAtend = new SimpleStringProperty(porcentajeAtend != null ? porcentajeAtend : "0%");
            this.pacNoAtendidos = new SimpleStringProperty(pacNoAtendidos != null ? pacNoAtendidos : "0");
            this.pacAlta = new SimpleStringProperty(pacAlta != null ? pacAlta : "0");
            this.pacExtranjero = new SimpleStringProperty(pacExtranjero != null ? pacExtranjero : "0");
            this.pacProvincia = new SimpleStringProperty(pacProvincia != null ? pacProvincia : "0");
            this.pacOtraUnidad = new SimpleStringProperty(pacOtraUnidad != null ? pacOtraUnidad : "0");
            this.pacOtrasCausas = new SimpleStringProperty(pacOtrasCausas != null ? pacOtrasCausas : "0");
            this.pacDesconoce = new SimpleStringProperty(pacDesconoce != null ? pacDesconoce : "0");
        }
        
        public SimpleStringProperty hospitalProperty() { return hospital; }
        public SimpleStringProperty departamentoProperty() { return departamento; }
        public SimpleStringProperty unidadProperty() { return unidad; }
        public SimpleStringProperty numTurnoProperty() { return numTurno; }
        public SimpleStringProperty horaInformeProperty() { return horaInforme; }
        public SimpleStringProperty pacInicioProperty() { return pacInicio; }
        public SimpleStringProperty pacAtendidosProperty() { return pacAtendidos; }
        public SimpleStringProperty pacTotalProperty() { return pacTotal; }
        public SimpleStringProperty porcentajeAtendProperty() { return porcentajeAtend; }
        public SimpleStringProperty pacNoAtendidosProperty() { return pacNoAtendidos; }
        public SimpleStringProperty pacAltaProperty() { return pacAlta; }
        public SimpleStringProperty pacExtranjeroProperty() { return pacExtranjero; }
        public SimpleStringProperty pacProvinciaProperty() { return pacProvincia; }
        public SimpleStringProperty pacOtraUnidadProperty() { return pacOtraUnidad; }
        public SimpleStringProperty pacOtrasCausasProperty() { return pacOtrasCausas; }
        public SimpleStringProperty pacDesconoceProperty() { return pacDesconoce; }
    }
}