package com.mycompany.gestorui.controllers.Gestion;

import java.net.URL;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

import com.jfoenix.controls.JFXTextField;
import com.mycompany.gestorui.model.entidades.Unidad;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

public class GestionUnidadController implements Initializable {

    @FXML private TableView<Unidad> tablaUnidades;
    @FXML private TableColumn<Unidad, Boolean> colSeleccion;
    @FXML private TableColumn<Unidad, String> colCodigo, colNombre, colUbicacion;
    @FXML private JFXTextField txtCodigo, txtNombre, txtUbicacion;
    @FXML private Label agregar, modificar, eliminar, limpiar;

    private ObservableList<Unidad> items = FXCollections.observableArrayList();
    private Set<Unidad> seleccionados = new HashSet<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
        configurarCheckBox();
        cargarEjemplos();
        configurarEventos();
    }

    private void configurarTabla() {
        colSeleccion.setCellFactory(col -> new CheckBoxTableCell<>() {
            private final CheckBox cb = new CheckBox();
            { cb.setOnAction(e -> { Unidad u = getTableRow().getItem(); if (u != null) { if (cb.isSelected()) seleccionados.add(u); else seleccionados.remove(u); } }); }
            @Override public void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) { setGraphic(null); return; }
                cb.setSelected(seleccionados.contains(getTableRow().getItem()));
                setGraphic(cb); setAlignment(Pos.CENTER);
            }
        });
        colSeleccion.setEditable(false); colSeleccion.setPrefWidth(60); colSeleccion.setText("");
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigoUni"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombreUni"));
        colUbicacion.setCellValueFactory(new PropertyValueFactory<>("ubicacion"));
        tablaUnidades.setItems(items);
        tablaUnidades.setOnMouseClicked(ev -> {
            if (ev.getClickCount() == 2) {
                Unidad u = tablaUnidades.getSelectionModel().getSelectedItem();
                if (u != null) { txtCodigo.setText(u.getCodigoUni()); txtNombre.setText(u.getNombreUni()); txtUbicacion.setText(u.getUbicacion()); }
            }
        });
    }

    private void configurarCheckBox() {
        CheckBox cb = new CheckBox("Seleccionar todos");
        cb.selectedProperty().addListener((obs, old, val) -> {
            if (val) seleccionados.addAll(items); else seleccionados.clear();
            tablaUnidades.refresh();
        });
        HBox h = new HBox(cb); h.setAlignment(Pos.CENTER);
        colSeleccion.setGraphic(h); colSeleccion.setSortable(false);
    }

    private void cargarEjemplos() {
        items.addAll(new Unidad("U001", "UCI", "Planta 3"), new Unidad("U002", "Consulta Externa", "Planta 1"));
    }

    private void configurarEventos() {
        agregar.setOnMouseClicked(e -> agregar());
        modificar.setOnMouseClicked(e -> modificar());
        eliminar.setOnMouseClicked(e -> eliminarSeleccionados());
        limpiar.setOnMouseClicked(e -> limpiarCampos());
    }

    private void agregar() {
        String cod = txtCodigo.getText().trim(), nom = txtNombre.getText().trim(), ubi = txtUbicacion.getText().trim();
        if (cod.isEmpty() || nom.isEmpty()) { mostrarAlerta("Complete código y nombre"); return; }
        items.add(new Unidad(cod, nom, ubi));
        limpiarCampos();
    }

    private void modificar() {
        Unidad u = tablaUnidades.getSelectionModel().getSelectedItem();
        if (u == null) { mostrarAlerta("Seleccione una unidad"); return; }
        u.setCodigoUni(txtCodigo.getText().trim());
        u.setNombreUni(txtNombre.getText().trim());
        u.setUbicacion(txtUbicacion.getText().trim());
        tablaUnidades.refresh();
        limpiarCampos();
    }

    private void eliminarSeleccionados() {
        if (seleccionados.isEmpty()) { mostrarAlerta("Seleccione al menos una unidad"); return; }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText("¿Eliminar " + seleccionados.size() + " unidad(es)?");
        if (alert.showAndWait().get() == ButtonType.OK) {
            items.removeAll(seleccionados);
            seleccionados.clear();
        }
    }

    private void limpiarCampos() { txtCodigo.clear(); txtNombre.clear(); txtUbicacion.clear(); tablaUnidades.getSelectionModel().clearSelection(); }
    private void mostrarAlerta(String msg) { Alert a = new Alert(Alert.AlertType.WARNING); a.setHeaderText(null); a.setContentText(msg); a.showAndWait(); }
}