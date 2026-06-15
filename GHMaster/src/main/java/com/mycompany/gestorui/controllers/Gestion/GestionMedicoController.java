package com.mycompany.gestorui.controllers.Gestion;

import com.jfoenix.controls.JFXTextField;
import com.mycompany.gestorui.model.entidades.Medico;
import javafx.animation.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import java.net.URL;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

public class GestionMedicoController implements Initializable {

    @FXML private TableView<Medico> tablaMedicos;
    @FXML private TableColumn<Medico, Boolean> colSeleccion;
    @FXML private TableColumn<Medico, String> colCodigo, colNombre, colEspecialidad, colLicencia, colTelefono;
    @FXML private JFXTextField txtCodigo, txtNombre, txtEspecialidad, txtLicencia, txtTelefono, txtDatosContacto, txtExperiencia;
    @FXML private Button btnAgregar, btnModificar, btnEliminar, btnLimpiar;
    @FXML private AnchorPane panelCrud;
    @FXML private Button btnTogglePanel;

    private ObservableList<Medico> items = FXCollections.observableArrayList();
    private Set<Medico> seleccionados = new HashSet<>();
    private boolean panelVisible = true;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
        configurarCheckBoxSeleccionarTodos();
        cargarEjemplos();
        configurarEventos();
        configurarPanelToggle();
    }

    private void configurarTabla() {
        colSeleccion.setCellFactory(col -> new CheckBoxTableCell<>() {
            private final CheckBox cb = new CheckBox();
            {
                cb.setOnAction(e -> {
                    Medico m = getTableRow().getItem();
                    if (m != null) {
                        if (cb.isSelected()) seleccionados.add(m);
                        else seleccionados.remove(m);
                    }
                });
            }
            @Override
            public void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                    return;
                }
                cb.setSelected(seleccionados.contains(getTableRow().getItem()));
                setGraphic(cb);
                setAlignment(Pos.CENTER);
            }
        });
        colSeleccion.setEditable(false);
        colSeleccion.setPrefWidth(60);
        colSeleccion.setText("");

        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigoMed"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombreMed"));
        colEspecialidad.setCellValueFactory(new PropertyValueFactory<>("especialidad"));
        colLicencia.setCellValueFactory(new PropertyValueFactory<>("numeroLic"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        
        tablaMedicos.setItems(items);
        
        tablaMedicos.setOnMouseClicked(ev -> {
            if (ev.getClickCount() == 2) {
                Medico m = tablaMedicos.getSelectionModel().getSelectedItem();
                if (m != null) {
                    txtCodigo.setText(m.getCodigoMed());
                    txtNombre.setText(m.getNombreMed());
                    txtEspecialidad.setText(m.getEspecialidad());
                    txtLicencia.setText(m.getNumeroLic());
                    txtTelefono.setText(m.getTelefono());
                    txtDatosContacto.setText(m.getDatosC());
                    txtExperiencia.setText(String.valueOf(m.getExperiencia()));
                    if (!panelVisible) mostrarPanel();
                }
            }
        });
    }

    private void configurarCheckBoxSeleccionarTodos() {
        CheckBox cbTodos = new CheckBox("Seleccionar todos");
        cbTodos.setStyle("-fx-font-size: 11px;");
        cbTodos.selectedProperty().addListener((obs, old, val) -> {
            if (val) seleccionados.addAll(items);
            else seleccionados.clear();
            tablaMedicos.refresh();
        });
        HBox header = new HBox(cbTodos);
        header.setAlignment(Pos.CENTER);
        colSeleccion.setGraphic(header);
        colSeleccion.setSortable(false);
    }

    private void cargarEjemplos() {
        items.addAll(
            new Medico("M001", "Dr. Juan Pérez", "Cardiología", "LIC123", "juan@mail.com", 10, "555-1234"),
            new Medico("M002", "Dra. María López", "Neurología", "LIC456", "maria@mail.com", 8, "555-5678"),
            new Medico("M003", "Dr. Carlos Ruiz", "Pediatría", "LIC789", "carlos@mail.com", 5, "555-9012")
        );
    }

    private void configurarEventos() {
        btnAgregar.setOnAction(e -> agregar());
        btnModificar.setOnAction(e -> modificar());
        btnEliminar.setOnAction(e -> eliminarSeleccionados());
        btnLimpiar.setOnAction(e -> limpiarCampos());
    }

    private void configurarPanelToggle() {
        if (btnTogglePanel != null) {
            btnTogglePanel.setOnAction(e -> togglePanel());
        }
    }

    private void togglePanel() {
        if (panelVisible) ocultarPanel();
        else mostrarPanel();
    }

    private void ocultarPanel() {
        Timeline timeline = new Timeline();
        KeyFrame keyFrame = new KeyFrame(Duration.millis(300),
            new KeyValue(panelCrud.translateXProperty(), 305),
            new KeyValue(panelCrud.opacityProperty(), 0)
        );
        timeline.getKeyFrames().add(keyFrame);
        timeline.setOnFinished(e -> {
            panelCrud.setVisible(false);
            panelCrud.setManaged(false);
            tablaMedicos.setPrefWidth(880);
        });
        timeline.play();
        btnTogglePanel.setText("Mostrar Panel");
        panelVisible = false;
    }

    private void mostrarPanel() {
        panelCrud.setVisible(true);
        panelCrud.setManaged(true);
        Timeline timeline = new Timeline();
        KeyFrame keyFrame = new KeyFrame(Duration.millis(300),
            new KeyValue(panelCrud.translateXProperty(), 0),
            new KeyValue(panelCrud.opacityProperty(), 1)
        );
        timeline.getKeyFrames().add(keyFrame);
        timeline.play();
        tablaMedicos.setPrefWidth(562);
        btnTogglePanel.setText("Ocultar Panel");
        panelVisible = true;
    }

    private void agregar() {
        String cod = txtCodigo.getText().trim();
        String nom = txtNombre.getText().trim();
        
        if (cod.isEmpty() || nom.isEmpty()) {
            mostrarAlerta("Código y nombre son obligatorios");
            return;
        }
        
        boolean existe = items.stream().anyMatch(m -> m.getCodigoMed().equals(cod));
        if (existe) {
            mostrarAlerta("Ya existe un médico con ese código");
            return;
        }
        
        int exp = 0;
        try {
            exp = Integer.parseInt(txtExperiencia.getText().trim());
        } catch (NumberFormatException e) {}
        
        Medico m = new Medico(cod, nom, txtEspecialidad.getText().trim(), 
                              txtLicencia.getText().trim(), txtDatosContacto.getText().trim(), 
                              exp, txtTelefono.getText().trim());
        items.add(m);
        limpiarCampos();
        mostrarAlerta("Médico agregado correctamente", Alert.AlertType.INFORMATION);
    }

    private void modificar() {
        Medico m = tablaMedicos.getSelectionModel().getSelectedItem();
        if (m == null) {
            mostrarAlerta("Seleccione un médico de la tabla");
            return;
        }
        
        String nuevoCodigo = txtCodigo.getText().trim();
        String nuevoNombre = txtNombre.getText().trim();
        
        if (nuevoCodigo.isEmpty() || nuevoNombre.isEmpty()) {
            mostrarAlerta("Código y nombre son obligatorios");
            return;
        }
        
        if (!nuevoCodigo.equals(m.getCodigoMed())) {
            boolean existe = items.stream().anyMatch(med -> med.getCodigoMed().equals(nuevoCodigo));
            if (existe) {
                mostrarAlerta("Ya existe otro médico con ese código");
                return;
            }
        }
        
        m.setCodigoMed(nuevoCodigo);
        m.setNombreMed(nuevoNombre);
        m.setEspecialidad(txtEspecialidad.getText().trim());
        m.setNumeroLic(txtLicencia.getText().trim());
        m.setTelefono(txtTelefono.getText().trim());
        m.setDatosC(txtDatosContacto.getText().trim());
        try {
            m.setExperiencia(Integer.parseInt(txtExperiencia.getText().trim()));
        } catch (NumberFormatException e) {}
        
        tablaMedicos.refresh();
        limpiarCampos();
        mostrarAlerta("Médico modificado correctamente", Alert.AlertType.INFORMATION);
    }

    private void eliminarSeleccionados() {
        if (seleccionados.isEmpty()) {
            mostrarAlerta("Seleccione al menos un médico");
            return;
        }
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText(null);
        alert.setContentText("¿Eliminar " + seleccionados.size() + " médico(s)?");
        
        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            items.removeAll(seleccionados);
            seleccionados.clear();
            mostrarAlerta("Médico(s) eliminado(s) correctamente", Alert.AlertType.INFORMATION);
        }
    }

    private void limpiarCampos() {
        txtCodigo.clear();
        txtNombre.clear();
        txtEspecialidad.clear();
        txtLicencia.clear();
        txtTelefono.clear();
        txtDatosContacto.clear();
        txtExperiencia.clear();
        tablaMedicos.getSelectionModel().clearSelection();
    }

    private void mostrarAlerta(String msg) {
        mostrarAlerta(msg, Alert.AlertType.WARNING);
    }
    
    private void mostrarAlerta(String msg, Alert.AlertType tipo) {
        Alert a = new Alert(tipo);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}