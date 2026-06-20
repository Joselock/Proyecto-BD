package com.mycompany.gestorui.controllers.Reporte;

import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;

import com.mycompany.gestorui.controllers.Reporte.Manager.TurnosRevisadosManager;
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
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.application.Platform;

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
    private TurnosRevisadosManager manager = TurnosRevisadosManager.getInstance();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            configurarColumnas();
            configurarDobleClick();
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
    
    private void configurarDobleClick() {
        tablaTurnos.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                TurnoRow selectedRow = tablaTurnos.getSelectionModel().getSelectedItem();
                if (selectedRow != null) {
                    mostrarVentanaDetalle(selectedRow);
                }
            }
        });
    }
    
    private void mostrarVentanaDetalle(TurnoRow turno) {
        Stage ventana = new Stage();
        ventana.initModality(Modality.APPLICATION_MODAL);
        ventana.setTitle("Detalle del Turno - Revisión");
        ventana.setWidth(500);
        ventana.setHeight(500);
        ventana.setResizable(false);
        
        VBox root = new VBox(15);
        root.setPadding(new Insets(25));
        root.setStyle("-fx-background-color: #f5f5f5;");
        
        Label titulo = new Label("Detalle del Turno");
        titulo.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        VBox infoBox = new VBox(8);
        infoBox.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-background-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 2);");
        
        Label lblHospital = new Label("Hospital: " + turno.getHospital());
        Label lblDepartamento = new Label("Departamento: " + turno.getDepartamento());
        Label lblUnidad = new Label("Unidad: " + turno.getUnidad());
        Label lblMedico = new Label("Médico: " + turno.getMedico());
        Label lblTotalPac = new Label("Total Pacientes: " + turno.getTotalPacientes());
        Label lblPacAtend = new Label("Pacientes Atendidos: " + turno.getCantPacAten());
        Label lblPorcentaje = new Label("Porcentaje Atención: " + turno.getPorcentajeAten());
        Label lblEstado = new Label("Estado Actual: " + turno.getEstado());
        
        lblHospital.setStyle("-fx-font-size: 14px;");
        lblDepartamento.setStyle("-fx-font-size: 14px;");
        lblUnidad.setStyle("-fx-font-size: 14px;");
        lblMedico.setStyle("-fx-font-size: 14px;");
        lblTotalPac.setStyle("-fx-font-size: 14px;");
        lblPacAtend.setStyle("-fx-font-size: 14px;");
        lblPorcentaje.setStyle("-fx-font-size: 14px;");
        lblEstado.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        
        infoBox.getChildren().addAll(
            lblHospital, lblDepartamento, lblUnidad, lblMedico,
            lblTotalPac, lblPacAtend, lblPorcentaje, lblEstado
        );
        
        Button btnRevisar = new Button("✅ Marcar como Revisado");
        btnRevisar.setStyle(
            "-fx-background-color: #27ae60; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 10 20; " +
            "-fx-background-radius: 5; " +
            "-fx-cursor: hand;"
        );
        btnRevisar.setMaxWidth(Double.MAX_VALUE);
        
        boolean yaRevisado = manager.esRevisado(
            turno.getHospital(),
            turno.getDepartamento(),
            turno.getUnidad(),
            turno.getMedico()
        );
        
        if (yaRevisado || turno.getEstado().contains("OK") || turno.getEstado().contains("Extioso")) {
            btnRevisar.setText("✅ Ya revisado");
            btnRevisar.setStyle(
                "-fx-background-color: #95a5a6; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 14px; " +
                "-fx-font-weight: bold; " +
                "-fx-padding: 10 20; " +
                "-fx-background-radius: 5;"
            );
            btnRevisar.setDisable(true);
        }
        
        btnRevisar.setOnAction(e -> {
            String hospital = turno.getHospital();
            String departamento = turno.getDepartamento();
            String unidad = turno.getUnidad();
            String medico = turno.getMedico();
            
            // Marcar como revisado en el manager global (actualiza BD)
            manager.marcarComoRevisado(hospital, departamento, unidad, medico);
            
            // Cambiar el estado
            turno.setEstado("✅ Revisado");
            
            // Eliminar el registro de la tabla
            turnosData.remove(turno);
            
            // Actualizar contador
            lblTotal.setText("Total de unidades: " + turnosData.size());
            lblInfo.setText("✅ Turno marcado como revisado. Quedan " + turnosData.size() + " por revisar.");
            
            ventana.close();
        });
        
        Button btnCerrar = new Button("✖ Cerrar");
        btnCerrar.setStyle(
            "-fx-background-color: #e74c3c; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 10 20; " +
            "-fx-background-radius: 5; " +
            "-fx-cursor: hand;"
        );
        btnCerrar.setMaxWidth(Double.MAX_VALUE);
        btnCerrar.setOnAction(e -> ventana.close());
        
        root.getChildren().addAll(titulo, infoBox, btnRevisar, btnCerrar);
        
        Scene scene = new Scene(root);
        ventana.setScene(scene);
        ventana.showAndWait();
    }
    
    private void cargarDatos() {
        try {
            LinkedList<Hospital> hospitales = unidadService.listadoUnidades();
            if (hospitales == null) {
                Platform.runLater(() -> 
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
                            
                            // Verificar si ya fue revisado
                            boolean yaRevisado = manager.esRevisado(
                                h.getNombreHos(),
                                d.getNombreDep(),
                                u.getNombreUni(),
                                medico.getNombreMed()
                            );
                            
                            if (yaRevisado) {
                                continue;
                            }
                            
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
            Platform.runLater(() -> {
                lblTotal.setText("Total de unidades: " + finalTotal);
                lblInfo.setText("✅ " + finalTotal + " unidades por revisar");
            });
            
        } catch (Exception ex) {
            Platform.runLater(() -> 
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
        
        public String getHospital() { return hospital.get(); }
        public String getDepartamento() { return departamento.get(); }
        public String getUnidad() { return unidad.get(); }
        public String getTotalPacientes() { return totalPacientes.get(); }
        public String getMedico() { return medico.get(); }
        public String getCantPacAten() { return cantPacAten.get(); }
        public String getPorcentajeAten() { return porcentajeAten.get(); }
        public String getEstado() { return estado.get(); }
        
        public void setEstado(String nuevoEstado) { 
            estado.set(nuevoEstado); 
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