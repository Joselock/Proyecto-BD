package com.mycompany.gestorui.controllers.Reporte;

import java.net.URL;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
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

import com.mycompany.gestorui.model.entidades.Departamento;
import com.mycompany.gestorui.model.entidades.Hospital;
import com.mycompany.gestorui.model.entidades.Informe;
import com.mycompany.gestorui.model.entidades.Turno;
import com.mycompany.gestorui.model.entidades.Unidad;
import com.mycompany.gestorui.model.services.reportes.ConsultaService;

public class InformesConsultasController implements Initializable {

    @FXML
    private TableView<InformeRow> tablaInformes;
    
    @FXML
    private TableColumn<InformeRow, String> colHospital;
    
    @FXML
    private TableColumn<InformeRow, String> colDepartamento;
    
    @FXML
    private TableColumn<InformeRow, String> colUnidad;
    
    @FXML
    private TableColumn<InformeRow, String> colFecha;
    
    @FXML
    private TableColumn<InformeRow, String> colNumTurno;
    
    @FXML
    private TableColumn<InformeRow, String> colHora;
    
    @FXML
    private TableColumn<InformeRow, String> colNumInforme;
    
    @FXML
    private TableColumn<InformeRow, String> colCantInicial;
    
    @FXML
    private TableColumn<InformeRow, String> colCantAdmitida;
    
    @FXML
    private TableColumn<InformeRow, String> colCantAlta;
    
    @FXML
    private TableColumn<InformeRow, String> colCantAnterior;
    
    @FXML
    private TableColumn<InformeRow, String> colCantDia;
    
    @FXML
    private ComboBox<String> cmbTipoBusqueda;
    
    @FXML
    private TextField txtCodigo;
    
    @FXML
    private Label lblTotal;
    
    @FXML
    private Label lblInfo;
    
    private ObservableList<InformeRow> informesData = FXCollections.observableArrayList();
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
        colFecha.setCellValueFactory(cellData -> cellData.getValue().fechaProperty());
        colNumTurno.setCellValueFactory(cellData -> cellData.getValue().numTurnoProperty());
        colHora.setCellValueFactory(cellData -> cellData.getValue().horaProperty());
        colNumInforme.setCellValueFactory(cellData -> cellData.getValue().numInformeProperty());
        colCantInicial.setCellValueFactory(cellData -> cellData.getValue().cantInicialProperty());
        colCantAdmitida.setCellValueFactory(cellData -> cellData.getValue().cantAdmitidaProperty());
        colCantAlta.setCellValueFactory(cellData -> cellData.getValue().cantAltaProperty());
        colCantAnterior.setCellValueFactory(cellData -> cellData.getValue().cantAnteriorProperty());
        colCantDia.setCellValueFactory(cellData -> cellData.getValue().cantDiaProperty());
        
        tablaInformes.setItems(informesData);
    }
    
    private void configurarComboBox() {
        cmbTipoBusqueda.getItems().addAll("Hospital", "Departamento", "Unidad");
        cmbTipoBusqueda.setValue("Hospital");
        
        cmbTipoBusqueda.setOnAction(e -> buscarInformes());
        txtCodigo.setOnAction(e -> buscarInformes());
    }
    
    private void cargarDatosPorDefecto() {
        txtCodigo.setText("H001");
        buscarInformes();
    }
    
    @FXML
    private void buscarInformes() {
        String tipo = cmbTipoBusqueda.getValue();
        String codigo = txtCodigo.getText().trim();
        
        if (codigo.isEmpty()) {
            lblInfo.setText("⚠️ Ingrese un código válido");
            return;
        }
        
        try {
            LinkedList<Hospital> hospitales = consultaService.obtenerInformeHospital(tipo, codigo);
            if (hospitales == null) {
                lblInfo.setText("❌ No se encontraron datos para " + tipo + ": " + codigo);
                return;
            }
            
            informesData.clear();
            int total = 0;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            
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
                            
                            String hora = "";
                            if (informe.getHora() != null) {
                                hora = informe.getHora().toLocalTime().format(formatter);
                            }
                            
                            informesData.add(new InformeRow(
                                h.getNombreHos() != null ? h.getNombreHos() : "",
                                d.getNombreDep() != null ? d.getNombreDep() : "",
                                u.getNombreUni() != null ? u.getNombreUni() : "",
                                informe.getFecha() != null ? informe.getFecha().toString() : "",
                                String.valueOf(turno.getNumTurn()),
                                hora,
                                informe.getNumIn() != null ? informe.getNumIn() : "",
                                String.valueOf(informe.getCantIni()),
                                String.valueOf(informe.getCantAdm()),
                                String.valueOf(informe.getPacAlta()),
                                String.valueOf(informe.getCantAnterior()),
                                String.valueOf(informe.getTotal())
                            ));
                            total++;
                        }
                    }
                }
            }
            
            lblTotal.setText("Total de informes: " + total);
            lblInfo.setText("✅ " + total + " informes encontrados en " + tipo + ": " + codigo);
            
        } catch (SQLException ex) {
            lblInfo.setText("❌ Error al cargar informes: " + ex.getMessage());
            ex.printStackTrace();
        } catch (Exception ex) {
            lblInfo.setText("❌ Error inesperado: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    public static class InformeRow {
        private final SimpleStringProperty hospital;
        private final SimpleStringProperty departamento;
        private final SimpleStringProperty unidad;
        private final SimpleStringProperty fecha;
        private final SimpleStringProperty numTurno;
        private final SimpleStringProperty hora;
        private final SimpleStringProperty numInforme;
        private final SimpleStringProperty cantInicial;
        private final SimpleStringProperty cantAdmitida;
        private final SimpleStringProperty cantAlta;
        private final SimpleStringProperty cantAnterior;
        private final SimpleStringProperty cantDia;
        
        public InformeRow(String hospital, String departamento, String unidad, String fecha, 
                         String numTurno, String hora, String numInforme, String cantInicial,
                         String cantAdmitida, String cantAlta, String cantAnterior, String cantDia) {
            this.hospital = new SimpleStringProperty(hospital != null ? hospital : "");
            this.departamento = new SimpleStringProperty(departamento != null ? departamento : "");
            this.unidad = new SimpleStringProperty(unidad != null ? unidad : "");
            this.fecha = new SimpleStringProperty(fecha != null ? fecha : "");
            this.numTurno = new SimpleStringProperty(numTurno != null ? numTurno : "");
            this.hora = new SimpleStringProperty(hora != null ? hora : "");
            this.numInforme = new SimpleStringProperty(numInforme != null ? numInforme : "");
            this.cantInicial = new SimpleStringProperty(cantInicial != null ? cantInicial : "0");
            this.cantAdmitida = new SimpleStringProperty(cantAdmitida != null ? cantAdmitida : "0");
            this.cantAlta = new SimpleStringProperty(cantAlta != null ? cantAlta : "0");
            this.cantAnterior = new SimpleStringProperty(cantAnterior != null ? cantAnterior : "0");
            this.cantDia = new SimpleStringProperty(cantDia != null ? cantDia : "0");
        }
        
        public SimpleStringProperty hospitalProperty() { return hospital; }
        public SimpleStringProperty departamentoProperty() { return departamento; }
        public SimpleStringProperty unidadProperty() { return unidad; }
        public SimpleStringProperty fechaProperty() { return fecha; }
        public SimpleStringProperty numTurnoProperty() { return numTurno; }
        public SimpleStringProperty horaProperty() { return hora; }
        public SimpleStringProperty numInformeProperty() { return numInforme; }
        public SimpleStringProperty cantInicialProperty() { return cantInicial; }
        public SimpleStringProperty cantAdmitidaProperty() { return cantAdmitida; }
        public SimpleStringProperty cantAltaProperty() { return cantAlta; }
        public SimpleStringProperty cantAnteriorProperty() { return cantAnterior; }
        public SimpleStringProperty cantDiaProperty() { return cantDia; }
    }
}