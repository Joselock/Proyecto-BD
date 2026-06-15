package com.mycompany.gestorui.controllers.Gestion;

import com.jfoenix.controls.JFXTextField;
import com.mycompany.gestorui.model.entidades.Unidad;
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

public class GestionUnidadController implements Initializable {

    @FXML private TableView<Unidad> tablaUnidades;
    @FXML private TableColumn<Unidad, Boolean> colSeleccion;
    @FXML private TableColumn<Unidad, String> colCodigo, colNombre, colUbicacion;
    @FXML private JFXTextField txtCodigo, txtNombre, txtUbicacion;
    @FXML private Button btnAgregar, btnModificar, btnEliminar, btnLimpiar;
    @FXML private AnchorPane panelCrud;
    @FXML private Button btnTogglePanel;

    private ObservableList<Unidad> items = FXCollections.observableArrayList();
    private Set<Unidad> seleccionados = new HashSet<>();
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
                    Unidad u = getTableRow().getItem();
                    if (u != null) {
                        if (cb.isSelected()) seleccionados.add(u);
                        else seleccionados.remove(u);
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

        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigoUni"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombreUni"));
        colUbicacion.setCellValueFactory(new PropertyValueFactory<>("ubicacion"));
        
        tablaUnidades.setItems(items);
        
        tablaUnidades.setOnMouseClicked(ev -> {
            if (ev.getClickCount() == 2) {
                Unidad u = tablaUnidades.getSelectionModel().getSelectedItem();
                if (u != null) {
                    txtCodigo.setText(u.getCodigoUni());
                    txtNombre.setText(u.getNombreUni());
                    txtUbicacion.setText(u.getUbicacion());
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
            tablaUnidades.refresh();
        });
        HBox header = new HBox(cbTodos);
        header.setAlignment(Pos.CENTER);
        colSeleccion.setGraphic(header);
        colSeleccion.setSortable(false);
    }

    private void cargarEjemplos() {
        items.addAll(
            new Unidad("U001", "Unidad de Cuidados Intensivos", "Planta 3 - Ala Norte"),
            new Unidad("U002", "Consulta Externa", "Planta 1 - Ala Sur"),
            new Unidad("U003", "Laboratorio", "Planta 2 - Ala Este"),
            new Unidad("U004", "Radiología", "Planta 0 - Ala Oeste")
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
            tablaUnidades.setPrefWidth(880);
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
        tablaUnidades.setPrefWidth(562);
        btnTogglePanel.setText("Ocultar Panel");
        panelVisible = true;
    }

    private void agregar() {
        String cod = txtCodigo.getText().trim();
        String nom = txtNombre.getText().trim();
        
        if (cod.isEmpty() || nom.isEmpty()) {
            mostrarAlerta("Complete código y nombre");
            return;
        }
        
        boolean existe = items.stream().anyMatch(u -> u.getCodigoUni().equals(cod));
        if (existe) {
            mostrarAlerta("Ya existe una unidad con ese código");
            return;
        }
        
        Unidad u = new Unidad(cod, nom, txtUbicacion.getText().trim());
        items.add(u);
        limpiarCampos();
        mostrarAlerta("Unidad agregada correctamente", Alert.AlertType.INFORMATION);
    }

    private void modificar() {
        Unidad u = tablaUnidades.getSelectionModel().getSelectedItem();
        if (u == null) {
            mostrarAlerta("Seleccione una unidad de la tabla");
            return;
        }
        
        String nuevoCodigo = txtCodigo.getText().trim();
        String nuevoNombre = txtNombre.getText().trim();
        
        if (nuevoCodigo.isEmpty() || nuevoNombre.isEmpty()) {
            mostrarAlerta("Complete código y nombre");
            return;
        }
        
        if (!nuevoCodigo.equals(u.getCodigoUni())) {
            boolean existe = items.stream().anyMatch(uni -> uni.getCodigoUni().equals(nuevoCodigo));
            if (existe) {
                mostrarAlerta("Ya existe otra unidad con ese código");
                return;
            }
        }
        
        u.setCodigoUni(nuevoCodigo);
        u.setNombreUni(nuevoNombre);
        u.setUbicacion(txtUbicacion.getText().trim());
        
        tablaUnidades.refresh();
        limpiarCampos();
        mostrarAlerta("Unidad modificada correctamente", Alert.AlertType.INFORMATION);
    }

    private void eliminarSeleccionados() {
        if (seleccionados.isEmpty()) {
            mostrarAlerta("Seleccione al menos una unidad");
            return;
        }
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText(null);
        alert.setContentText("¿Eliminar " + seleccionados.size() + " unidad(es)?");
        
        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            items.removeAll(seleccionados);
            seleccionados.clear();
            mostrarAlerta("Unidad(es) eliminada(s) correctamente", Alert.AlertType.INFORMATION);
        }
    }

    private void limpiarCampos() {
        txtCodigo.clear();
        txtNombre.clear();
        txtUbicacion.clear();
        tablaUnidades.getSelectionModel().clearSelection();
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