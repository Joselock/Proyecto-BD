package com.mycompany.gestorui.controllers.Gestion;

import com.jfoenix.controls.JFXTextField;
import com.mycompany.gestorui.model.entidades.Paciente;
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
import java.sql.Date;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

public class GestionPacienteController implements Initializable {

    @FXML private TableView<Paciente> tablaPacientes;
    @FXML private TableColumn<Paciente, Boolean> colSeleccion;
    @FXML private TableColumn<Paciente, String> colNumHistoria, colNombre, colDireccion, colFechaNac, colEstado;
    @FXML private JFXTextField txtNumHistoria, txtNombre, txtDireccion, txtFechaNac, txtEstado;
    @FXML private Button btnAgregar, btnModificar, btnEliminar, btnLimpiar;
    @FXML private AnchorPane panelCrud;
    @FXML private Button btnTogglePanel;

    private ObservableList<Paciente> items = FXCollections.observableArrayList();
    private Set<Paciente> seleccionados = new HashSet<>();
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
                    Paciente p = getTableRow().getItem();
                    if (p != null) {
                        if (cb.isSelected()) seleccionados.add(p);
                        else seleccionados.remove(p);
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

        colNumHistoria.setCellValueFactory(new PropertyValueFactory<>("numHisCli"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombrePac"));
        colDireccion.setCellValueFactory(new PropertyValueFactory<>("direccionP"));
        colFechaNac.setCellValueFactory(new PropertyValueFactory<>("fechaN"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        
        tablaPacientes.setItems(items);
        
        tablaPacientes.setOnMouseClicked(ev -> {
            if (ev.getClickCount() == 2) {
                Paciente p = tablaPacientes.getSelectionModel().getSelectedItem();
                if (p != null) {
                    txtNumHistoria.setText(p.getNumHisCli());
                    txtNombre.setText(p.getNombrePac());
                    txtDireccion.setText(p.getDireccionP());
                    txtFechaNac.setText(p.getFechaN() != null ? p.getFechaN().toString() : "");
                    txtEstado.setText(p.getEstado());
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
            tablaPacientes.refresh();
        });
        HBox header = new HBox(cbTodos);
        header.setAlignment(Pos.CENTER);
        colSeleccion.setGraphic(header);
        colSeleccion.setSortable(false);
    }

    private void cargarEjemplos() {
        items.addAll(
            new Paciente("P001", "Ana López", Date.valueOf("1990-05-10"), "Calle 123", "Activo"),
            new Paciente("P002", "Luis García", Date.valueOf("1985-08-15"), "Avenida 456", "Activo"),
            new Paciente("P003", "Marta Fernández", Date.valueOf("1995-03-20"), "Plaza 789", "Inactivo")
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
            tablaPacientes.setPrefWidth(880);
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
        tablaPacientes.setPrefWidth(562);
        btnTogglePanel.setText("Ocultar Panel");
        panelVisible = true;
    }

    private void agregar() {
        String num = txtNumHistoria.getText().trim();
        String nom = txtNombre.getText().trim();
        
        if (num.isEmpty() || nom.isEmpty()) {
            mostrarAlerta("Nº Historia y nombre son obligatorios");
            return;
        }
        
        boolean existe = items.stream().anyMatch(p -> p.getNumHisCli().equals(num));
        if (existe) {
            mostrarAlerta("Ya existe un paciente con ese número de historia");
            return;
        }
        
        Date fecha = null;
        try {
            fecha = Date.valueOf(txtFechaNac.getText().trim());
        } catch (Exception e) {}
        
        Paciente p = new Paciente(num, nom, fecha, txtDireccion.getText().trim(), txtEstado.getText().trim());
        items.add(p);
        limpiarCampos();
        mostrarAlerta("Paciente agregado correctamente", Alert.AlertType.INFORMATION);
    }

    private void modificar() {
        Paciente p = tablaPacientes.getSelectionModel().getSelectedItem();
        if (p == null) {
            mostrarAlerta("Seleccione un paciente de la tabla");
            return;
        }
        
        String nuevoNum = txtNumHistoria.getText().trim();
        String nuevoNombre = txtNombre.getText().trim();
        
        if (nuevoNum.isEmpty() || nuevoNombre.isEmpty()) {
            mostrarAlerta("Nº Historia y nombre son obligatorios");
            return;
        }
        
        if (!nuevoNum.equals(p.getNumHisCli())) {
            boolean existe = items.stream().anyMatch(pac -> pac.getNumHisCli().equals(nuevoNum));
            if (existe) {
                mostrarAlerta("Ya existe otro paciente con ese número de historia");
                return;
            }
        }
        
        p.setNumHisCli(nuevoNum);
        p.setNombrePac(nuevoNombre);
        p.setDireccionP(txtDireccion.getText().trim());
        try {
            p.setFechaN(Date.valueOf(txtFechaNac.getText().trim()));
        } catch (Exception e) {}
        p.setEstado(txtEstado.getText().trim());
        
        tablaPacientes.refresh();
        limpiarCampos();
        mostrarAlerta("Paciente modificado correctamente", Alert.AlertType.INFORMATION);
    }

    private void eliminarSeleccionados() {
        if (seleccionados.isEmpty()) {
            mostrarAlerta("Seleccione al menos un paciente");
            return;
        }
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText(null);
        alert.setContentText("¿Eliminar " + seleccionados.size() + " paciente(s)?");
        
        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            items.removeAll(seleccionados);
            seleccionados.clear();
            mostrarAlerta("Paciente(s) eliminado(s) correctamente", Alert.AlertType.INFORMATION);
        }
    }

    private void limpiarCampos() {
        txtNumHistoria.clear();
        txtNombre.clear();
        txtDireccion.clear();
        txtFechaNac.clear();
        txtEstado.clear();
        tablaPacientes.getSelectionModel().clearSelection();
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