package com.mycompany.gestorui.controllers.Gestion;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import com.jfoenix.controls.JFXTextField;
import com.mycompany.gestorui.controllers.MainWindow.Manager.DatosCambiadosManager;
import com.mycompany.gestorui.model.entidades.Hospital;
import com.mycompany.gestorui.model.services.crud.crudHospital;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

public class GestionHospitalController implements Initializable {

    @FXML
    private TableView<Hospital> tablaHospitales;

    @FXML
    private TableColumn<Hospital, Boolean> colSeleccion;

    @FXML
    private TableColumn<Hospital, String> colCodigo;

    @FXML
    private TableColumn<Hospital, String> colNombre;

    @FXML
    private JFXTextField txtCodigo, txtNombre;

    // Cambiado de Label a Button
    @FXML
    private Button btnAgregar, btnModificar, btnEliminar, btnLimpiar;

    @FXML
    private AnchorPane panelCrud;

    @FXML
    private Button btnTogglePanel; // Botón para ocultar/mostrar panel

    private ObservableList<Hospital> hospitales = FXCollections.observableArrayList();
    private Set<Hospital> seleccionados = new HashSet<>();

    private boolean panelVisible = true;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
        configurarCheckBoxSeleccionarTodos();
        cargarDatos();
        configurarEventos();
        configurarPanelToggle();
    }

    private void configurarTabla() {
        // Columna de selección con CheckBox
        colSeleccion.setCellFactory(col -> new CheckBoxTableCell<>() {
            private final CheckBox cb = new CheckBox();
            {
                cb.setOnAction(e -> {
                    Hospital h = getTableRow().getItem();
                    if (h != null) {
                        if (cb.isSelected())
                            seleccionados.add(h);
                        else
                            seleccionados.remove(h);
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
                Hospital h = getTableRow().getItem();
                cb.setSelected(seleccionados.contains(h));
                setGraphic(cb);
                setAlignment(Pos.CENTER);
            }
        });
        colSeleccion.setEditable(false);
        colSeleccion.setPrefWidth(60);
        colSeleccion.setText("");

        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigoHos"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombreHos"));
        tablaHospitales.setItems(hospitales);

        // Doble click para cargar datos en el panel
        tablaHospitales.setOnMouseClicked(ev -> {
            if (ev.getClickCount() == 2) {
                Hospital h = tablaHospitales.getSelectionModel().getSelectedItem();
                if (h != null) {
                    txtCodigo.setText(h.getCodigoHos());
                    txtNombre.setText(h.getNombreHos());
                    // Si el panel está oculto, mostrarlo automáticamente
                    if (!panelVisible) {
                        mostrarPanel();
                    }
                }
            }
        });
    }

    private void configurarCheckBoxSeleccionarTodos() {
        CheckBox cbTodos = new CheckBox("Seleccionar todos");
        cbTodos.setStyle("-fx-font-size: 11px;");
        cbTodos.selectedProperty().addListener((obs, old, val) -> {
            if (val) {
                seleccionados.addAll(hospitales);
            } else {
                seleccionados.clear();
            }
            tablaHospitales.refresh();
        });
        HBox header = new HBox(cbTodos);
        header.setAlignment(Pos.CENTER);
        colSeleccion.setGraphic(header);
        colSeleccion.setSortable(false);
    }

    private void cargarDatos() {
        List<Hospital> lista = new ArrayList<>(crudHospital.obtenerHospitales());

        for (Hospital h : lista) {
            hospitales.add(h);
        }

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
        if (panelVisible) {
            ocultarPanel();
        } else {
            mostrarPanel();
        }
    }

    private void ocultarPanel() {
        // Animación para ocultar el panel
        Timeline timeline = new Timeline();
        KeyFrame keyFrame = new KeyFrame(Duration.millis(300),
                new KeyValue(panelCrud.translateXProperty(), 305),
                new KeyValue(panelCrud.opacityProperty(), 0));
        timeline.getKeyFrames().add(keyFrame);
        timeline.setOnFinished(e -> {
            panelCrud.setVisible(false);
            panelCrud.setManaged(false);
            tablaHospitales.setPrefWidth(880);
        });
        timeline.play();
        //btnTogglePanel.setText("Mostrar Panel");
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
        tablaHospitales.setPrefWidth(562);
        //btnTogglePanel.setText("Ocultar Panel");
        panelVisible = true;
    }

    private void agregar() {
        String cod = txtCodigo.getText().trim();
        String nom = txtNombre.getText().trim();
        if (cod.isEmpty() || nom.isEmpty()) {
            mostrarAlerta("Complete código y nombre");
            return;
        }

        // Verificar si ya existe
        boolean existe = hospitales.stream().anyMatch(h -> h.getCodigoHos().equals(cod));
        if (existe) {
            mostrarAlerta("Ya existe un hospital con ese código");
            return;
        }

        Map<String, Object> result = new HashMap<>(crudHospital.insertarHospital(cod, nom));

        // Notificar cambio
        DatosCambiadosManager.getInstance().notificarCambios();

        limpiarCampos();
        if (!(result.get("existe").equals("false"))) {
            mostrarAlerta("Hospital agregado correctamente", Alert.AlertType.INFORMATION);
            hospitales.add(new Hospital(cod, nom, null));
        } else {
            mostrarAlerta("Hospital no agregado", Alert.AlertType.INFORMATION);
        }

    }

    private void modificar() {
        Hospital h = tablaHospitales.getSelectionModel().getSelectedItem();
        if (h == null) {
            mostrarAlerta("Seleccione un hospital de la tabla");
            return;
        }

        String nuevoCodigo = txtCodigo.getText().trim();
        String nuevoNombre = txtNombre.getText().trim();

        if (nuevoCodigo.isEmpty() || nuevoNombre.isEmpty()) {
            mostrarAlerta("Complete código y nombre");
            return;
        }

        // Verificar si el nuevo código ya existe (si es diferente al original)
        if (!nuevoCodigo.equals(h.getCodigoHos())) {
            boolean existe = hospitales.stream().anyMatch(hos -> hos.getCodigoHos().equals(nuevoCodigo));
            if (existe) {
                mostrarAlerta("Ya existe otro hospital con ese código");
                return;
            }
        }

        // Guardar valores originales por si falla la actualización
        String codigoOriginal = h.getCodigoHos();
        String nombreOriginal = h.getNombreHos();

        h.setCodigoHos(nuevoCodigo);
        h.setNombreHos(nuevoNombre);

        Map<String, Object> result = new HashMap<>(
                crudHospital.modificarHospital(codigoOriginal, nuevoCodigo, nuevoNombre));

        if (result != null && result.containsKey("existe") && Boolean.TRUE.equals(result.get("existe"))) {
            tablaHospitales.refresh();
            limpiarCampos();
            mostrarAlerta("Hospital modificado correctamente", Alert.AlertType.INFORMATION);
        } else {
            // Si falló, revertir los cambios en el objeto
            h.setCodigoHos(codigoOriginal);
            h.setNombreHos(nombreOriginal);
            mostrarAlerta("Error al modificar el hospital", Alert.AlertType.ERROR);
        }

    }

    private void eliminarSeleccionados() {
        if (seleccionados.isEmpty()) {
            mostrarAlerta("Seleccione al menos un hospital");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText(null);
        alert.setContentText("¿Eliminar " + seleccionados.size() + " hospital(es)?");

        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            List<Hospital> aEliminar = List.copyOf(seleccionados);
            boolean todosEliminados = true;
            StringBuilder errores = new StringBuilder();

            for (Hospital h : aEliminar) {
                String mensaje = crudHospital.eliminarHospital(h.getCodigoHos());
                if (mensaje == null || mensaje.toLowerCase().contains("error") || mensaje.contains("Error")) {
                    todosEliminados = false;
                    errores.append("• ").append(h.getCodigoHos()).append(": ").append(mensaje).append("\n");
                }
            }

            // Notificar cambio
            DatosCambiadosManager.getInstance().notificarCambios();

            if (todosEliminados) {
                // Eliminar de la tabla local y limpiar selección
                hospitales.removeAll(aEliminar);
                seleccionados.clear();
                mostrarAlerta("Hospital(es) eliminado(s) correctamente", Alert.AlertType.INFORMATION);
            } else {
                // Recargar datos para sincronizar
                cargarDatos();
                mostrarAlerta("Algunos hospitales no se eliminaron:\n" + errores.toString(), Alert.AlertType.WARNING);
            }
        }
    }

    private void limpiarCampos() {
        txtCodigo.clear();
        txtNombre.clear();
        tablaHospitales.getSelectionModel().clearSelection();
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