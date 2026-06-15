package com.mycompany.gestorui.controllers.Gestion;

import com.jfoenix.controls.JFXTextField;
import com.mycompany.gestorui.model.entidades.Hospital;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import java.net.URL;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

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

    @FXML 
    private Label agregar, modificar, eliminar, limpiar;

    private ObservableList<Hospital> hospitales = FXCollections.observableArrayList();
    private Set<Hospital> seleccionados = new HashSet<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
        configurarCheckBoxSeleccionarTodos();
        cargarDatos(); // ← luego cambias por carga real desde BD
        configurarEventos();
    }

    private void configurarTabla() {
        // Columna de selección con CheckBox (sin PropertyValueFactory)
        colSeleccion.setCellFactory(col -> new CheckBoxTableCell<>() {
            private final CheckBox cb = new CheckBox();
            {
                cb.setOnAction(e -> {
                    Hospital h = getTableRow().getItem();
                    if (h != null) {
                        if (cb.isSelected()) seleccionados.add(h);
                        else seleccionados.remove(h);
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
        tablaHospitales.setOnMouseClicked(ev -> {
            if (ev.getClickCount() == 2) {
                Hospital h = tablaHospitales.getSelectionModel().getSelectedItem();
                if (h != null) {
                    txtCodigo.setText(h.getCodigoHos());
                    txtNombre.setText(h.getNombreHos());
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
        hospitales.addAll(
            new Hospital("H001", "Hospital Central", null),
            new Hospital("H002", "Hospital Norte", null)
        );
    }

    private void configurarEventos() {
        agregar.setOnMouseClicked(e -> agregar());
        modificar.setOnMouseClicked(e -> modificar());
        eliminar.setOnMouseClicked(e -> eliminarSeleccionados());
        limpiar.setOnMouseClicked(e -> limpiarCampos());
    }

    private void agregar() {
        String cod = txtCodigo.getText().trim();
        String nom = txtNombre.getText().trim();
        if (cod.isEmpty() || nom.isEmpty()) {
            mostrarAlerta("Complete código y nombre");
            return;
        }
        hospitales.add(new Hospital(cod, nom, null));
        limpiarCampos();
    }

    private void modificar() {
        Hospital h = tablaHospitales.getSelectionModel().getSelectedItem();
        if (h == null) {
            mostrarAlerta("Seleccione un hospital");
            return;
        }
        h.setCodigoHos(txtCodigo.getText().trim());
        h.setNombreHos(txtNombre.getText().trim());
        tablaHospitales.refresh();
        limpiarCampos();
    }

    private void eliminarSeleccionados() {
        if (seleccionados.isEmpty()) {
            mostrarAlerta("Seleccione al menos un hospital");
            return;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText("¿Eliminar " + seleccionados.size() + " hospital(es)?");
        if (alert.showAndWait().get() == ButtonType.OK) {
            hospitales.removeAll(seleccionados);
            seleccionados.clear();
        }
    }

    private void limpiarCampos() {
        txtCodigo.clear();
        txtNombre.clear();
        tablaHospitales.getSelectionModel().clearSelection();
    }

    private void mostrarAlerta(String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}