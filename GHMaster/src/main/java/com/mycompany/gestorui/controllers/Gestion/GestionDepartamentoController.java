package com.mycompany.gestorui.controllers.Gestion;

import java.net.URL;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

import com.jfoenix.controls.JFXTextField;
import com.mycompany.gestorui.model.entidades.Departamento;

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

public class GestionDepartamentoController implements Initializable {

    @FXML private TableView<Departamento> tablaDepartamentos;
    @FXML private TableColumn<Departamento, Boolean> colSeleccion;
    @FXML private TableColumn<Departamento, String> colCodigo, colNombre;
    @FXML private JFXTextField txtCodigo, txtNombre;
    @FXML private Label agregar, modificar, eliminar, limpiar;

    private ObservableList<Departamento> items = FXCollections.observableArrayList();
    private Set<Departamento> seleccionados = new HashSet<>();

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
            { cb.setOnAction(e -> { Departamento d = getTableRow().getItem(); if (d != null) { if (cb.isSelected()) seleccionados.add(d); else seleccionados.remove(d); } }); }
            @Override public void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) { setGraphic(null); return; }
                cb.setSelected(seleccionados.contains(getTableRow().getItem()));
                setGraphic(cb); setAlignment(Pos.CENTER);
            }
        });
        colSeleccion.setEditable(false); colSeleccion.setPrefWidth(60); colSeleccion.setText("");
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigoDep"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombreDep"));
        tablaDepartamentos.setItems(items);
        tablaDepartamentos.setOnMouseClicked(ev -> {
            if (ev.getClickCount() == 2) {
                Departamento d = tablaDepartamentos.getSelectionModel().getSelectedItem();
                if (d != null) { txtCodigo.setText(d.getCodigoDep()); txtNombre.setText(d.getNombreDep()); }
            }
        });
    }

    private void configurarCheckBox() {
        CheckBox cb = new CheckBox("Seleccionar todos");
        cb.selectedProperty().addListener((obs, old, val) -> {
            if (val) seleccionados.addAll(items); else seleccionados.clear();
            tablaDepartamentos.refresh();
        });
        HBox h = new HBox(cb); h.setAlignment(Pos.CENTER);
        colSeleccion.setGraphic(h); colSeleccion.setSortable(false);
    }

    private void cargarEjemplos() {
        items.addAll(new Departamento("D001", "Cardiología"), new Departamento("D002", "Neurología"));
    }

    private void configurarEventos() {
        agregar.setOnMouseClicked(e -> agregar());
        modificar.setOnMouseClicked(e -> modificar());
        eliminar.setOnMouseClicked(e -> eliminarSeleccionados());
        limpiar.setOnMouseClicked(e -> limpiarCampos());
    }

    private void agregar() {
        String cod = txtCodigo.getText().trim(), nom = txtNombre.getText().trim();
        if (cod.isEmpty() || nom.isEmpty()) { mostrarAlerta("Complete código y nombre"); return; }
        items.add(new Departamento(cod, nom));
        limpiarCampos();
    }

    private void modificar() {
        Departamento d = tablaDepartamentos.getSelectionModel().getSelectedItem();
        if (d == null) { mostrarAlerta("Seleccione un departamento"); return; }
        d.setCodigoDep(txtCodigo.getText().trim());
        d.setNombreDep(txtNombre.getText().trim());
        tablaDepartamentos.refresh();
        limpiarCampos();
    }

    private void eliminarSeleccionados() {
        if (seleccionados.isEmpty()) { mostrarAlerta("Seleccione al menos uno"); return; }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText("¿Eliminar " + seleccionados.size() + " departamento(s)?");
        if (alert.showAndWait().get() == ButtonType.OK) {
            items.removeAll(seleccionados);
            seleccionados.clear();
        }
    }

    private void limpiarCampos() { txtCodigo.clear(); txtNombre.clear(); tablaDepartamentos.getSelectionModel().clearSelection(); }
    private void mostrarAlerta(String msg) { Alert a = new Alert(Alert.AlertType.WARNING); a.setHeaderText(null); a.setContentText(msg); a.showAndWait(); }
}