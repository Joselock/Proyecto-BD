package com.mycompany.gestorui.controllers.Gestion;

import com.jfoenix.controls.JFXTextField;
import com.mycompany.gestorui.controllers.MainWindow.Manager.DatosCambiadosManager;
import com.mycompany.gestorui.model.entidades.Paciente;
import com.mycompany.gestorui.model.entidades.Unidad;
import com.mycompany.gestorui.model.services.crud.crudPaciente;
import com.mycompany.gestorui.model.services.crud.crudUnidad;

import javafx.animation.*;
import javafx.application.Platform;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

public class GestionPacienteController implements Initializable {

    @FXML
    private TableView<Paciente> tablaPacientes;
    @FXML
    private TableColumn<Paciente, Boolean> colSeleccion;
    @FXML
    private TableColumn<Paciente, String> colNumHistoria;
    @FXML
    private TableColumn<Paciente, String> colNombre;
    @FXML
    private TableColumn<Paciente, String> colDireccion;
    @FXML
    private TableColumn<Paciente, Date> colFechaNac;
    @FXML
    private TableColumn<Paciente, String> colEstado;
    @FXML
    private TableColumn<Paciente, String> colUnidad;

    @FXML
    private JFXTextField txtNumHistoria, txtNombre, txtDireccion, txtFechaNac, txtEstado;
    @FXML
    private ComboBox<String> cmbUnidad;
    @FXML
    private Button btnAgregar, btnModificar, btnEliminar, btnLimpiar;
    @FXML
    private AnchorPane panelCrud;
    @FXML
    private Button btnTogglePanel;

    private ObservableList<Paciente> items = FXCollections.observableArrayList();
    private ObservableList<String> unidadItems = FXCollections.observableArrayList();
    private Map<String, String> unidadMap = new HashMap<>();
    private Set<Paciente> seleccionados = new HashSet<>();
    private boolean panelVisible = true;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarUnidades();
        configurarTabla();
        configurarCheckBoxSeleccionarTodos();
        cargarDatos();
        configurarEventos();
        configurarPanelToggle();
    }

    private void cargarUnidades() {
        new Thread(() -> {
            try {
                List<Unidad> unidades = crudUnidad.obtenerUnidades();
                Platform.runLater(() -> {
                    unidadItems.clear();
                    unidadMap.clear();
                    for (Unidad u : unidades) {
                        String display = u.getCodigoUni() + " - " + u.getNombreUni();
                        unidadItems.add(display);
                        unidadMap.put(display, u.getCodigoUni());
                    }
                    cmbUnidad.setItems(unidadItems);
                });
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> mostrarAlerta("Error al cargar unidades: " + e.getMessage()));
            }
        }).start();
    }

    private String obtenerCodigoUnidadSeleccionado() {
        String selected = cmbUnidad.getValue();
        if (selected != null && unidadMap.containsKey(selected)) {
            return unidadMap.get(selected);
        }
        return null;
    }

    private void configurarTabla() {
        colSeleccion.setCellFactory(col -> new CheckBoxTableCell<>() {
            private final CheckBox cb = new CheckBox();
            {
                cb.setOnAction(e -> {
                    Paciente p = getTableRow().getItem();
                    if (p != null) {
                        if (cb.isSelected())
                            seleccionados.add(p);
                        else
                            seleccionados.remove(p);
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
        colUnidad.setCellValueFactory(new PropertyValueFactory<>("codigoUni"));

        tablaPacientes.setItems(items);

        tablaPacientes.setOnMouseClicked(ev -> {
            if (ev.getClickCount() == 2) {
                Paciente p = tablaPacientes.getSelectionModel().getSelectedItem();
                if (p != null) {
                    cargarDatosEnPanel(p);
                    if (!panelVisible)
                        mostrarPanel();
                }
            }
        });
    }

    private void cargarDatosEnPanel(Paciente p) {
        txtNumHistoria.setText(p.getNumHisCli());
        txtNombre.setText(p.getNombrePac());
        txtDireccion.setText(p.getDireccionP());
        txtFechaNac.setText(p.getFechaN() != null ? p.getFechaN().toString() : "");
        txtEstado.setText(p.getEstado());

        // Cargar la unidad seleccionada en el ComboBox
        if (p.getCodigoUni() != null && !p.getCodigoUni().isEmpty()) {
            for (Map.Entry<String, String> entry : unidadMap.entrySet()) {
                if (entry.getValue().equals(p.getCodigoUni())) {
                    cmbUnidad.setValue(entry.getKey());
                    break;
                }
            }
        }
    }

    private void configurarCheckBoxSeleccionarTodos() {
        CheckBox cbTodos = new CheckBox("Seleccionar todos");
        cbTodos.setStyle("-fx-font-size: 11px;");
        cbTodos.selectedProperty().addListener((obs, old, val) -> {
            if (val)
                seleccionados.addAll(items);
            else
                seleccionados.clear();
            tablaPacientes.refresh();
        });
        HBox header = new HBox(cbTodos);
        header.setAlignment(Pos.CENTER);
        colSeleccion.setGraphic(header);
        colSeleccion.setSortable(false);
    }

    private void cargarDatos() {
        tablaPacientes.setPlaceholder(new ProgressIndicator());

        new Thread(() -> {
            try {
                List<Paciente> lista = crudPaciente.obtenerPacientes();

                Platform.runLater(() -> {
                    items.clear();
                    items.addAll(lista);
                    tablaPacientes.setPlaceholder(new Label("No hay pacientes"));
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    mostrarAlerta("Error al cargar datos: " + e.getMessage(), Alert.AlertType.ERROR);
                    tablaPacientes.setPlaceholder(new Label("Error al cargar datos"));
                });
                e.printStackTrace();
            }
        }).start();
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
        if (panelVisible)
            ocultarPanel();
        else
            mostrarPanel();
    }

    private void ocultarPanel() {
        Timeline timeline = new Timeline();
        KeyFrame keyFrame = new KeyFrame(Duration.millis(300),
                new KeyValue(panelCrud.translateXProperty(), 305),
                new KeyValue(panelCrud.opacityProperty(), 0));
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
                new KeyValue(panelCrud.opacityProperty(), 1));
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
            String fechaStr = txtFechaNac.getText().trim();
            if (!fechaStr.isEmpty()) {
                fecha = Date.valueOf(fechaStr);
            }
        } catch (Exception e) {
            mostrarAlerta("Formato de fecha inválido. Use YYYY-MM-DD");
            return;
        }

        String codUni = obtenerCodigoUnidadSeleccionado();

        Map<String, Object> resultado = crudPaciente.insertarPaciente(
                num,
                nom,
                txtDireccion.getText().trim(),
                fecha != null ? fecha.toLocalDate() : null,
                txtEstado.getText().trim(),
                codUni);

        if (resultado != null && Boolean.TRUE.equals(resultado.get("existe"))) {
            Paciente p = new Paciente(num, nom, fecha, txtDireccion.getText().trim(), txtEstado.getText().trim(),
                    codUni);
            items.add(p);
            limpiarCampos();
            mostrarAlerta("Paciente agregado correctamente", Alert.AlertType.INFORMATION);
            // Notificar cambio
            DatosCambiadosManager.getInstance().notificarCambios();
        } else {
            String mensaje = resultado != null ? (String) resultado.get("mensaje") : "Error desconocido";
            mostrarAlerta("Error al agregar: " + mensaje, Alert.AlertType.ERROR);
        }
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

        Date fecha = null;
        try {
            String fechaStr = txtFechaNac.getText().trim();
            if (!fechaStr.isEmpty()) {
                fecha = Date.valueOf(fechaStr);
            }
        } catch (Exception e) {
            mostrarAlerta("Formato de fecha inválido. Use YYYY-MM-DD");
            return;
        }

        String codUni = obtenerCodigoUnidadSeleccionado();
        String numOriginal = p.getNumHisCli();

        Map<String, Object> resultado = crudPaciente.modificarPaciente(
                numOriginal,
                nuevoNum,
                txtEstado.getText().trim(),
                nuevoNombre,
                txtDireccion.getText().trim(),
                fecha != null ? fecha.toLocalDate() : null,
                codUni);

        if (resultado != null && Boolean.TRUE.equals(resultado.get("existe"))) {
            p.setNumHisCli(nuevoNum);
            p.setNombrePac(nuevoNombre);
            p.setDireccionP(txtDireccion.getText().trim());
            p.setFechaN(fecha);
            p.setEstado(txtEstado.getText().trim());
            p.setCodigoUni(codUni);
            tablaPacientes.refresh();
            limpiarCampos();
            mostrarAlerta("Paciente modificado correctamente", Alert.AlertType.INFORMATION);
        } else {
            String mensaje = resultado != null ? (String) resultado.get("mensaje") : "Error desconocido";
            mostrarAlerta("Error al modificar: " + mensaje, Alert.AlertType.ERROR);
        }
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
            List<Paciente> aEliminar = List.copyOf(seleccionados);
            boolean todosEliminados = true;
            StringBuilder errores = new StringBuilder();

            for (Paciente p : aEliminar) {
                String resultado = crudPaciente.eliminarPaciente(p.getNumHisCli());

                if (resultado != null && !resultado.toLowerCase().contains("error")) {
                    items.remove(p);
                    seleccionados.remove(p);
                } else {
                    todosEliminados = false;
                    errores.append("• ").append(p.getNumHisCli()).append(": ").append(resultado).append("\n");
                }
            }

            if (todosEliminados) {
                mostrarAlerta("Paciente(s) eliminado(s) correctamente", Alert.AlertType.INFORMATION);
                // Notificar cambio
                DatosCambiadosManager.getInstance().notificarCambios();
            } else {
                mostrarAlerta("Algunos pacientes no se eliminaron:\n" + errores.toString(), Alert.AlertType.WARNING);
                cargarDatos();
            }
        }
    }

    private void limpiarCampos() {
        txtNumHistoria.clear();
        txtNombre.clear();
        txtDireccion.clear();
        txtFechaNac.clear();
        txtEstado.clear();
        cmbUnidad.setValue(null);
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