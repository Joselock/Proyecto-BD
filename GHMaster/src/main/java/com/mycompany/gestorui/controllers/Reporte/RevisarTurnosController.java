package com.mycompany.gestorui.controllers.Reporte;

import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;

import com.mycompany.gestorui.model.entidades.Departamento;
import com.mycompany.gestorui.model.entidades.Hospital;
import com.mycompany.gestorui.model.entidades.Informe;
import com.mycompany.gestorui.model.entidades.Medico;
import com.mycompany.gestorui.model.entidades.Unidad;
import com.mycompany.gestorui.model.services.reportes.UnidadService;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;


public class RevisarTurnosController implements Initializable {

    @FXML
    private TableView<TurnoRow> tablaTurnos;
    
    @FXML
    private TableColumn<TurnoRow, String> colHospital;
    
    @FXML
    private TableColumn<TurnoRow, String> colDepartamento;
    
    @FXML
    private TableColumn<TurnoRow, String> colUnidad;
    
    @FXML
    private TableColumn<TurnoRow, String> colTotalPacientes;
    
    @FXML
    private TableColumn<TurnoRow, String> colMedico;
    
    @FXML
    private TableColumn<TurnoRow, String> colCantPacAten;
    
    @FXML
    private TableColumn<TurnoRow, String> colPorcentajeAten;
    
    @FXML
    private TableColumn<TurnoRow, String> colEstado;
    
    @FXML
    private Label lblTotal;
    
    @FXML
    private Label lblInfo;
    
    private ObservableList<TurnoRow> turnosData = FXCollections.observableArrayList();
    private UnidadService unidadService = new UnidadService();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            configurarColumnas();
            // Cargar datos en un hilo separado para no bloquear la UI
            Thread thread = new Thread(this::cargarDatos);
            thread.setDaemon(true);
            thread.start();
        } catch (Exception ex) {
            lblInfo.setText("❌ Error en inicialización: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    private void configurarColumnas() {
        colHospital.setCellValueFactory(cellData -> cellData.getValue().hospitalProperty());
        colDepartamento.setCellValueFactory(cellData -> cellData.getValue().departamentoProperty());
        colUnidad.setCellValueFactory(cellData -> cellData.getValue().unidadProperty());
        colTotalPacientes.setCellValueFactory(cellData -> cellData.getValue().totalPacientesProperty());
        colMedico.setCellValueFactory(cellData -> cellData.getValue().medicoProperty());
        colCantPacAten.setCellValueFactory(cellData -> cellData.getValue().cantPacAtenProperty());
        colPorcentajeAten.setCellValueFactory(cellData -> cellData.getValue().porcentajeAtenProperty());
        colEstado.setCellValueFactory(cellData -> cellData.getValue().estadoProperty());
        
        tablaTurnos.setItems(turnosData);
    }
    
    private void cargarDatos() {
        try {
            LinkedList<Hospital> hospitales = unidadService.listadoUnidades();
            if (hospitales == null) {
                javafx.application.Platform.runLater(() -> 
                    lblInfo.setText("❌ No se pudieron cargar las unidades")
                );
                return;
            }
            
            turnosData.clear();
            int total = 0;
            
            for (Hospital h : hospitales) {
                if (h == null) continue;
                for (Departamento d : h.getDepartamentos()) {
                    if (d == null) continue;
                    for (Unidad u : d.getUnidades()) {
                        if (u == null) continue;
                        int size = Math.min(u.getInformes().size(), u.getMedicos().size());
                        for (int i = 0; i < size; i++) {
                            Informe informe = u.getInformes().get(i);
                            Medico medico = u.getMedicos().get(i);
                            if (informe == null || medico == null) continue;
                            
                            float porcentaje = informe.getPorcentajePacAtend();
                            String estado = porcentaje >= 80 ? "🟢 OK" : "🔴 Revisar";
                            
                            turnosData.add(new TurnoRow(
                                h.getNombreHos() != null ? h.getNombreHos() : "",
                                d.getNombreDep() != null ? d.getNombreDep() : "",
                                u.getNombreUni() != null ? u.getNombreUni() : "",
                                String.valueOf(informe.getTotal()),
                                medico.getNombreMed() != null ? medico.getNombreMed() : "Sin asignar",
                                String.valueOf(informe.getPacAtend()),
                                String.format("%.1f%%", porcentaje),
                                estado
                            ));
                            total++;
                        }
                    }
                }
            }
            
            int finalTotal = total;
            javafx.application.Platform.runLater(() -> {
                lblTotal.setText("Total de unidades: " + finalTotal);
                lblInfo.setText("✅ " + finalTotal + " unidades revisadas");
            });
            
        } catch (Exception ex) {
            javafx.application.Platform.runLater(() -> 
                lblInfo.setText("❌ Error al revisar turnos: " + ex.getMessage())
            );
            ex.printStackTrace();
        }
    }
    
    public static class TurnoRow {
        private final SimpleStringProperty hospital;
        private final SimpleStringProperty departamento;
        private final SimpleStringProperty unidad;
        private final SimpleStringProperty totalPacientes;
        private final SimpleStringProperty medico;
        private final SimpleStringProperty cantPacAten;
        private final SimpleStringProperty porcentajeAten;
        private final SimpleStringProperty estado;
        
        public TurnoRow(String hospital, String departamento, String unidad, String totalPacientes,
                       String medico, String cantPacAten, String porcentajeAten, String estado) {
            this.hospital = new SimpleStringProperty(hospital != null ? hospital : "");
            this.departamento = new SimpleStringProperty(departamento != null ? departamento : "");
            this.unidad = new SimpleStringProperty(unidad != null ? unidad : "");
            this.totalPacientes = new SimpleStringProperty(totalPacientes != null ? totalPacientes : "0");
            this.medico = new SimpleStringProperty(medico != null ? medico : "");
            this.cantPacAten = new SimpleStringProperty(cantPacAten != null ? cantPacAten : "0");
            this.porcentajeAten = new SimpleStringProperty(porcentajeAten != null ? porcentajeAten : "0%");
            this.estado = new SimpleStringProperty(estado != null ? estado : "");
        }
        
        public SimpleStringProperty hospitalProperty() { return hospital; }
        public SimpleStringProperty departamentoProperty() { return departamento; }
        public SimpleStringProperty unidadProperty() { return unidad; }
        public SimpleStringProperty totalPacientesProperty() { return totalPacientes; }
        public SimpleStringProperty medicoProperty() { return medico; }
        public SimpleStringProperty cantPacAtenProperty() { return cantPacAten; }
        public SimpleStringProperty porcentajeAtenProperty() { return porcentajeAten; }
        public SimpleStringProperty estadoProperty() { return estado; }
    }
}