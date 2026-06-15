package com.mycompany.gestorui.controllers.Gestion;

import com.jfoenix.controls.JFXTextField;
import com.mycompany.gestorui.model.entidades.Departamento;
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

public class GestionDepartamentoController implements Initializable {

    @FXML private TableView<Departamento> tablaDepartamentos;
    @FXML private TableColumn<Departamento, Boolean> colSeleccion;
    @FXML private TableColumn<Departamento, String> colCodigo, colNombre;
    @FXML private JFXTextField txtCodigo, txtNombre;
    @FXML private Button btnAgregar, btnModificar, btnEliminar, btnLimpiar;
    @FXML private AnchorPane panelCrud;
    @FXML private Button btnTogglePanel;

    private ObservableList<Departamento> items = FXCollections.observableArrayList();
    private Set<Departamento> seleccionados = new HashSet<>();
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
                    Departamento d = getTableRow().getItem();
                    if (d != null) {
                        if (cb.isSelected()) seleccionados.add(d);
                        else seleccionados.remove(d);
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

        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigoDep"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombreDep"));
        
        tablaDepartamentos.setItems(items);
        
        tablaDepartamentos.setOnMouseClicked(ev -> {
            if (ev.getClickCount() == 2) {
                Departamento d = tablaDepartamentos.getSelectionModel().getSelectedItem();
                if (d != null) {
                    txtCodigo.setText(d.getCodigoDep());
                    txtNombre.setText(d.getNombreDep());
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
            tablaDepartamentos.refresh();
        });
        HBox header = new HBox(cbTodos);
        header.setAlignment(Pos.CENTER);
        colSeleccion.setGraphic(header);
        colSeleccion.setSortable(false);
    }

    private void cargarEjemplos() {
        items.addAll(
            new Departamento("D001", "Cardiología"),
            new Departamento("D002", "Neurología"),
            new Departamento("D003", "Pediatría")
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
            tablaDepartamentos.setPrefWidth(880);
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
        tablaDepartamentos.setPrefWidth(562);
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
        
        boolean existe = items.stream().anyMatch(d -> d.getCodigoDep().equals(cod));
        if (existe) {
            mostrarAlerta("Ya existe un departamento con ese código");
            return;
        }
        
        items.add(new Departamento(cod, nom));
        limpiarCampos();
        mostrarAlerta("Departamento agregado correctamente", Alert.AlertType.INFORMATION);
    }

    private void modificar() {
        Departamento d = tablaDepartamentos.getSelectionModel().getSelectedItem();
        if (d == null) {
            mostrarAlerta("Seleccione un departamento de la tabla");
            return;
        }
        
        String nuevoCodigo = txtCodigo.getText().trim();
        String nuevoNombre = txtNombre.getText().trim();
        
        if (nuevoCodigo.isEmpty() || nuevoNombre.isEmpty()) {
            mostrarAlerta("Complete código y nombre");
            return;
        }
        
        if (!nuevoCodigo.equals(d.getCodigoDep())) {
            boolean existe = items.stream().anyMatch(dep -> dep.getCodigoDep().equals(nuevoCodigo));
            if (existe) {
                mostrarAlerta("Ya existe otro departamento con ese código");
                return;
            }
        }
        
        d.setCodigoDep(nuevoCodigo);
        d.setNombreDep(nuevoNombre);
        tablaDepartamentos.refresh();
        limpiarCampos();
        mostrarAlerta("Departamento modificado correctamente", Alert.AlertType.INFORMATION);
    }

    private void eliminarSeleccionados() {
        if (seleccionados.isEmpty()) {
            mostrarAlerta("Seleccione al menos un departamento");
            return;
        }
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText(null);
        alert.setContentText("¿Eliminar " + seleccionados.size() + " departamento(s)?");
        
        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            items.removeAll(seleccionados);
            seleccionados.clear();
            mostrarAlerta("Departamento(s) eliminado(s) correctamente", Alert.AlertType.INFORMATION);
        }
    }

    private void limpiarCampos() {
        txtCodigo.clear();
        txtNombre.clear();
        tablaDepartamentos.getSelectionModel().clearSelection();
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