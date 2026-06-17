package com.mycompany.gestorui.controllers.Reporte;

import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;

import com.mycompany.gestorui.model.entidades.Departamento;
import com.mycompany.gestorui.model.entidades.Hospital;
import com.mycompany.gestorui.model.entidades.Medico;
import com.mycompany.gestorui.model.entidades.Unidad;
import com.mycompany.gestorui.model.services.reportes.MedicoService;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class ListadoMedicosController implements Initializable {

    @FXML
    private TableView<MedicoRow> tablaMedicos;

    @FXML
    private TableColumn<MedicoRow, String> colNombre;

    @FXML
    private TableColumn<MedicoRow, String> colLicencia;

    @FXML
    private TableColumn<MedicoRow, String> colEspecialidad;

    @FXML
    private TableColumn<MedicoRow, String> colTelefono;

    @FXML
    private TableColumn<MedicoRow, String> colExperiencia;

    @FXML
    private TableColumn<MedicoRow, String> colDatosCon;

    @FXML
    private TableColumn<MedicoRow, String> colHospital;

    @FXML
    private TableColumn<MedicoRow, String> colDepartamento;

    @FXML
    private TableColumn<MedicoRow, String> colUnidad;

    @FXML
    private Label lblTotal;

    @FXML
    private Label lblInfo;

    private ObservableList<MedicoRow> medicosData = FXCollections.observableArrayList();
    private MedicoService medicoService = new MedicoService();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColumnas();
        cargarDatos();
    }

    private void configurarColumnas() {
        colNombre.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
        colLicencia.setCellValueFactory(cellData -> cellData.getValue().licenciaProperty());
        colEspecialidad.setCellValueFactory(cellData -> cellData.getValue().especialidadProperty());
        colTelefono.setCellValueFactory(cellData -> cellData.getValue().telefonoProperty());
        colExperiencia.setCellValueFactory(cellData -> cellData.getValue().experienciaProperty());
        colDatosCon.setCellValueFactory(cellData -> cellData.getValue().datosConProperty());
        colHospital.setCellValueFactory(cellData -> cellData.getValue().hospitalProperty());
        colDepartamento.setCellValueFactory(cellData -> cellData.getValue().departamentoProperty());
        colUnidad.setCellValueFactory(cellData -> cellData.getValue().unidadProperty());

        tablaMedicos.setItems(medicosData);
    }

    private void cargarDatos() {
        try {
            LinkedList<Hospital> hospitales = medicoService.obtenerListadosMedicos();
            if (hospitales == null) {
                lblInfo.setText(" No se pudieron cargar los médicos (datos nulos)");
                return;
            }

            medicosData.clear();
            int total = 0;

            for (Hospital h : hospitales) {
                if (h == null) continue;
                String nombreHos = h.getNombreHos() != null ? h.getNombreHos() : "";
                for (Departamento d : h.getDepartamentos()) {
                    if (d == null) continue;
                    String nombreDep = d.getNombreDep() != null ? d.getNombreDep() : "";
                    for (Unidad u : d.getUnidades()) {
                        if (u == null) continue;
                        String nombreUni = u.getNombreUni() != null ? u.getNombreUni() : "";
                        // Asumimos que la lista de médicos está en la unidad
                        for (Medico m : u.getMedicos()) {
                            if (m == null) continue;
                            medicosData.add(new MedicoRow(
                                m.getNombreMed() != null ? m.getNombreMed() : "",
                                m.getNumeroLic() != null ? m.getNumeroLic() : "",
                                m.getEspecialidad() != null ? m.getEspecialidad() : "",
                                m.getTelefono() != null ? m.getTelefono() : "",
                                String.valueOf(m.getExperiencia()),
                                m.getDatosC() != null ? m.getDatosC() : "",
                                nombreHos,
                                nombreDep,
                                nombreUni
                            ));
                            total++;
                        }
                    }
                }
            }

            lblTotal.setText("Total de médicos: " + total);
            lblInfo.setText(" Médicos encontrados");

        } catch (Exception ex) {
            lblInfo.setText("Error al cargar médicos: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public static class MedicoRow {
        private final SimpleStringProperty nombre;
        private final SimpleStringProperty licencia;
        private final SimpleStringProperty especialidad;
        private final SimpleStringProperty telefono;
        private final SimpleStringProperty experiencia;
        private final SimpleStringProperty datosCon;
        private final SimpleStringProperty hospital;
        private final SimpleStringProperty departamento;
        private final SimpleStringProperty unidad;

        public MedicoRow(String nombre, String licencia, String especialidad, String telefono,
                         String experiencia, String datosCon, String hospital, String departamento, String unidad) {
            this.nombre = new SimpleStringProperty(nombre != null ? nombre : "");
            this.licencia = new SimpleStringProperty(licencia != null ? licencia : "");
            this.especialidad = new SimpleStringProperty(especialidad != null ? especialidad : "");
            this.telefono = new SimpleStringProperty(telefono != null ? telefono : "");
            this.experiencia = new SimpleStringProperty(experiencia != null ? experiencia : "0");
            this.datosCon = new SimpleStringProperty(datosCon != null ? datosCon : "");
            this.hospital = new SimpleStringProperty(hospital != null ? hospital : "");
            this.departamento = new SimpleStringProperty(departamento != null ? departamento : "");
            this.unidad = new SimpleStringProperty(unidad != null ? unidad : "");
        }

        public SimpleStringProperty nombreProperty() { return nombre; }
        public SimpleStringProperty licenciaProperty() { return licencia; }
        public SimpleStringProperty especialidadProperty() { return especialidad; }
        public SimpleStringProperty telefonoProperty() { return telefono; }
        public SimpleStringProperty experienciaProperty() { return experiencia; }
        public SimpleStringProperty datosConProperty() { return datosCon; }
        public SimpleStringProperty hospitalProperty() { return hospital; }
        public SimpleStringProperty departamentoProperty() { return departamento; }
        public SimpleStringProperty unidadProperty() { return unidad; }
    }
}