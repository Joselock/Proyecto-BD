package com.mycompany.gestorui.controllers.Gestion;

import com.jfoenix.controls.JFXTextField;
import com.mycompany.gestorui.model.entidades.Turno;
import com.mycompany.gestorui.model.services.crud.crudMedico;
import com.mycompany.gestorui.model.services.crud.crudTurno;
import com.mycompany.gestorui.model.services.crud.crudUnidad;
import com.mycompany.gestorui.controllers.MainWindow.Manager.DatosCambiadosManager;
import com.mycompany.gestorui.model.entidades.Medico;
import com.mycompany.gestorui.model.entidades.Unidad;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

public class GestionTurnoController implements Initializable {

    @FXML
    private TableView<Turno> tablaTurnos;
    @FXML
    private TableColumn<Turno, Boolean> colSeleccion;
    @FXML
    private TableColumn<Turno, Integer> colNumero, colCantAtendidos;
    @FXML
    private TableColumn<Turno, String> colEstado, colUnidad, colMedico;
    @FXML
    private JFXTextField txtNumero, txtCantAtendidos, txtEstado;
    @FXML
    private ComboBox<String> cmbUnidad, cmbMedico;
    @FXML
    private Button btnAgregar, btnModificar, btnEliminar, btnLimpiar;
    @FXML
    private AnchorPane panelCrud;
    @FXML
    private Button btnTogglePanel;

    private ObservableList<Turno> items = FXCollections.observableArrayList();
    private ObservableList<String> unidadItems = FXCollections.observableArrayList();
    private ObservableList<String> medicoItems = FXCollections.observableArrayList();
    private Map<String, String> unidadMap = new HashMap<>();
    private Map<String, String> medicoMap = new HashMap<>();
    private Set<Turno> seleccionados = new HashSet<>();
    private boolean panelVisible = true;

    // Para simular médicos existentes (esto debería venir de la BD)
    private ObservableList<Medico> medicosDisponibles = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarUnidades();
        cargarMedicos();
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

    private void cargarMedicos() {
        new Thread(() -> {
            try {
                List<Medico> lista = crudMedico.obtenerMedicos();
                Platform.runLater(() -> {
                    medicosDisponibles.clear();
                    medicosDisponibles.addAll(lista);

                    medicoItems.clear();
                    medicoMap.clear();
                    for (Medico m : lista) {
                        String display = m.getCodigoMed() + " - " + m.getNombreMed();
                        medicoItems.add(display);
                        medicoMap.put(display, m.getCodigoMed());
                    }
                    cmbMedico.setItems(medicoItems);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private String obtenerCodigoMedicoSeleccionado() {
        String selected = cmbMedico.getValue();
        if (selected != null && medicoMap.containsKey(selected)) {
            return medicoMap.get(selected);
        }
        return null;
    }

    private void configurarTabla() {
        colSeleccion.setCellFactory(col -> new CheckBoxTableCell<>() {
            private final CheckBox cb = new CheckBox();
            {
                cb.setOnAction(e -> {
                    Turno t = getTableRow().getItem();
                    if (t != null) {
                        if (cb.isSelected())
                            seleccionados.add(t);
                        else
                            seleccionados.remove(t);
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

        colNumero.setCellValueFactory(new PropertyValueFactory<>("numTurn"));
        colCantAtendidos.setCellValueFactory(new PropertyValueFactory<>("cantAten"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estTur"));
        colUnidad.setCellValueFactory(new PropertyValueFactory<>("codUni"));
        colMedico.setCellValueFactory(cellData -> {
            Turno turno = cellData.getValue();
            if (turno != null && turno.getMedico() != null) {
                return new SimpleStringProperty(turno.getMedico().getCodigoMed());
            }
            return new SimpleStringProperty("");
        });

        tablaTurnos.setItems(items);

        tablaTurnos.setOnMouseClicked(ev -> {
            if (ev.getClickCount() == 2) {
                Turno t = tablaTurnos.getSelectionModel().getSelectedItem();
                if (t != null) {
                    txtNumero.setText(String.valueOf(t.getNumTurn()));
                    txtCantAtendidos.setText(String.valueOf(t.getCantAten()));
                    txtEstado.setText(t.getEstTur());

                    // Set ComboBox values by finding the display string
                    for (String display : unidadItems) {
                        if (unidadMap.get(display).equals(t.getCodUni())) {
                            cmbUnidad.setValue(display);
                            break;
                        }
                    }

                    if (t.getMedico() != null) {
                        String codMed = t.getMedico().getCodigoMed();
                        for (String display : medicoItems) {
                            if (medicoMap.get(display).equals(codMed)) {
                                cmbMedico.setValue(display);
                                break;
                            }
                        }
                    }

                    if (!panelVisible)
                        mostrarPanel();
                }
            }
        });
    }

    private void configurarCheckBoxSeleccionarTodos() {
        CheckBox cbTodos = new CheckBox("Seleccionar todos");
        cbTodos.setStyle("-fx-font-size: 11px;");
        cbTodos.selectedProperty().addListener((obs, old, val) -> {
            if (val)
                seleccionados.addAll(items);
            else
                seleccionados.clear();
            tablaTurnos.refresh();
        });
        HBox header = new HBox(cbTodos);
        header.setAlignment(Pos.CENTER);
        colSeleccion.setGraphic(header);
        colSeleccion.setSortable(false);
    }

    private void cargarDatos() {
        tablaTurnos.setPlaceholder(new ProgressIndicator());

        new Thread(() -> {
            try {
                List<Turno> lista = crudTurno.obtenerTurnos();

                Platform.runLater(() -> {
                    items.clear();
                    items.addAll(lista);
                    tablaTurnos.setPlaceholder(new Label("No hay turnos"));
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    mostrarAlerta("Error al cargar datos: " + e.getMessage(), Alert.AlertType.ERROR);
                    tablaTurnos.setPlaceholder(new Label("Error al cargar datos"));
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
            tablaTurnos.setPrefWidth(880);
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
        tablaTurnos.setPrefWidth(562);
        btnTogglePanel.setText("Ocultar Panel");
        panelVisible = true;
    }

    private void agregar() {
        try {
            String numStr = txtNumero.getText().trim();
            if (numStr.isEmpty()) {
                mostrarAlerta("Número de turno es obligatorio");
                return;
            }
            int num = Integer.parseInt(numStr);

            boolean existe = items.stream().anyMatch(t -> t.getNumTurn() == num);
            if (existe) {
                mostrarAlerta("Ya existe un turno con ese número");
                return;
            }

            int cant = 0;
            if (!txtCantAtendidos.getText().trim().isEmpty()) {
                cant = Integer.parseInt(txtCantAtendidos.getText().trim());
            }

            String codUni = obtenerCodigoUnidadSeleccionado();
            String codMed = obtenerCodigoMedicoSeleccionado();

            Map<String, Object> resultado = crudTurno.insertarTurno(
                    num,
                    cant,
                    txtEstado.getText().trim(),
                    codUni,
                    codMed);

            if (resultado != null && Boolean.TRUE.equals(resultado.get("existe"))) {
                Medico medico = null;
                if (codMed != null) {
                    medico = medicosDisponibles.stream()
                            .filter(m -> m.getCodigoMed().equals(codMed))
                            .findFirst()
                            .orElse(null);
                }
                Turno t = new Turno(num, cant, txtEstado.getText().trim(), codUni, medico);
                items.add(t);
                limpiarCampos();
                mostrarAlerta("Turno agregado correctamente", Alert.AlertType.INFORMATION);
                // Notificar cambio
                DatosCambiadosManager.getInstance().notificarCambios();
            } else {
                String mensaje = resultado != null ? (String) resultado.get("mensaje") : "Error desconocido";
                mostrarAlerta("Error al agregar: " + mensaje, Alert.AlertType.ERROR);
            }
        } catch (NumberFormatException e) {
            mostrarAlerta("Los valores numéricos deben ser válidos");
        }
    }

    private void modificar() {
        Turno t = tablaTurnos.getSelectionModel().getSelectedItem();
        if (t == null) {
            mostrarAlerta("Seleccione un turno de la tabla");
            return;
        }

        try {
            int nuevoNum = Integer.parseInt(txtNumero.getText().trim());

            if (nuevoNum != t.getNumTurn()) {
                boolean existe = items.stream().anyMatch(turno -> turno.getNumTurn() == nuevoNum);
                if (existe) {
                    mostrarAlerta("Ya existe otro turno con ese número");
                    return;
                }
            }

            int numOriginal = t.getNumTurn();
            int cantOriginal = t.getCantAten();
            String estadoOriginal = t.getEstTur();
            String uniOriginal = t.getCodUni();
            Medico medOriginal = t.getMedico();

            int cant = 0;
            if (!txtCantAtendidos.getText().trim().isEmpty()) {
                cant = Integer.parseInt(txtCantAtendidos.getText().trim());
            }

            String codUni = obtenerCodigoUnidadSeleccionado();
            String codMed = obtenerCodigoMedicoSeleccionado();

            Map<String, Object> resultado = crudTurno.modificarTurno(
                    numOriginal,
                    nuevoNum,
                    cant,
                    txtEstado.getText().trim(),
                    codUni,
                    codMed);

            if (resultado != null && Boolean.TRUE.equals(resultado.get("existe"))) {
                Medico medico = null;
                if (codMed != null) {
                    medico = medicosDisponibles.stream()
                            .filter(m -> m.getCodigoMed().equals(codMed))
                            .findFirst()
                            .orElse(null);
                }
                t.setNumTurn(nuevoNum);
                t.setCantAten(cant);
                t.setEstTur(txtEstado.getText().trim());
                t.setCodUni(codUni);
                t.setMedico(medico);
                tablaTurnos.refresh();
                limpiarCampos();
                mostrarAlerta("Turno modificado correctamente", Alert.AlertType.INFORMATION);
            } else {
                t.setNumTurn(numOriginal);
                t.setCantAten(cantOriginal);
                t.setEstTur(estadoOriginal);
                t.setCodUni(uniOriginal);
                t.setMedico(medOriginal);
                String mensaje = resultado != null ? (String) resultado.get("mensaje") : "Error desconocido";
                mostrarAlerta("Error al modificar: " + mensaje, Alert.AlertType.ERROR);
            }
        } catch (NumberFormatException e) {
            mostrarAlerta("Los valores numéricos deben ser válidos");
        }
    }

    private void eliminarSeleccionados() {
        if (seleccionados.isEmpty()) {
            mostrarAlerta("Seleccione al menos un turno");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText(null);
        alert.setContentText("¿Eliminar " + seleccionados.size() + " turno(s)?");

        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            List<Turno> aEliminar = List.copyOf(seleccionados);
            boolean todosEliminados = true;
            StringBuilder errores = new StringBuilder();

            for (Turno t : aEliminar) {
                String resultado = crudTurno.eliminarTurno(t.getNumTurn());

                if (resultado != null && !resultado.toLowerCase().contains("error")) {
                    items.remove(t);
                    seleccionados.remove(t);
                } else {
                    todosEliminados = false;
                    errores.append("• Turno ").append(t.getNumTurn()).append(": ").append(resultado).append("\n");
                }
            }

            if (todosEliminados) {
                mostrarAlerta("Turno(s) eliminado(s) correctamente", Alert.AlertType.INFORMATION);
                // Notificar cambio
                DatosCambiadosManager.getInstance().notificarCambios();
            } else {
                mostrarAlerta("Algunos turnos no se eliminaron:\n" + errores.toString(), Alert.AlertType.WARNING);
                cargarDatos();
            }
        }
    }

    private void limpiarCampos() {
        txtNumero.clear();
        txtCantAtendidos.clear();
        txtEstado.clear();
        cmbUnidad.setValue(null);
        cmbMedico.setValue(null);
        tablaTurnos.getSelectionModel().clearSelection();
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