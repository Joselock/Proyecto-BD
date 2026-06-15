package com.mycompany.gestorui.controllers.Gestion;

import java.net.URL;
import java.sql.Date;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

import com.jfoenix.controls.JFXTextField;
import com.mycompany.gestorui.model.entidades.Paciente;

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

public class GestionPacienteController implements Initializable {

    @FXML private TableView<Paciente> tablaPacientes;
    @FXML private TableColumn<Paciente, Boolean> colSeleccion;
    @FXML private TableColumn<Paciente, String> colNumHistoria, colNombre, colDireccion, colFechaNac, colEstado;
    @FXML private JFXTextField txtNumHistoria, txtNombre, txtDireccion, txtFechaNac, txtEstado;
    @FXML private Label agregar, modificar, eliminar, limpiar;

    private ObservableList<Paciente> items = FXCollections.observableArrayList();
    private Set<Paciente> seleccionados = new HashSet<>();

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
            { cb.setOnAction(e -> { Paciente p = getTableRow().getItem(); if (p != null) { if (cb.isSelected()) seleccionados.add(p); else seleccionados.remove(p); } }); }
            @Override public void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) { setGraphic(null); return; }
                cb.setSelected(seleccionados.contains(getTableRow().getItem()));
                setGraphic(cb); setAlignment(Pos.CENTER);
            }
        });
        colSeleccion.setEditable(false); colSeleccion.setPrefWidth(60); colSeleccion.setText("");
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
                }
            }
        });
    }

    private void configurarCheckBox() {
        CheckBox cb = new CheckBox("Seleccionar todos");
        cb.selectedProperty().addListener((obs, old, val) -> {
            if (val) seleccionados.addAll(items); else seleccionados.clear();
            tablaPacientes.refresh();
        });
        HBox h = new HBox(cb); h.setAlignment(Pos.CENTER);
        colSeleccion.setGraphic(h); colSeleccion.setSortable(false);
    }

    private void cargarEjemplos() {
        items.add(new Paciente("P001", "Ana López", Date.valueOf("1990-05-10"), "Calle 123", "Activo"));
    }

    private void configurarEventos() {
        agregar.setOnMouseClicked(e -> agregar());
        modificar.setOnMouseClicked(e -> modificar());
        eliminar.setOnMouseClicked(e -> eliminarSeleccionados());
        limpiar.setOnMouseClicked(e -> limpiarCampos());
    }

    private void agregar() {
        String num = txtNumHistoria.getText().trim(), nom = txtNombre.getText().trim();
        if (num.isEmpty() || nom.isEmpty()) { mostrarAlerta("Nº Historia y nombre son obligatorios"); return; }
        Date fecha = null;
        try { fecha = Date.valueOf(txtFechaNac.getText().trim()); } catch (Exception e) {}
        Paciente p = new Paciente(num, nom, fecha, txtDireccion.getText().trim(), txtEstado.getText().trim());
        items.add(p);
        limpiarCampos();
    }

    private void modificar() {
        Paciente p = tablaPacientes.getSelectionModel().getSelectedItem();
        if (p == null) { mostrarAlerta("Seleccione un paciente"); return; }
        p.setNumHisCli(txtNumHistoria.getText().trim());
        p.setNombrePac(txtNombre.getText().trim());
        p.setDireccionP(txtDireccion.getText().trim());
        try { p.setFechaN(Date.valueOf(txtFechaNac.getText().trim())); } catch (Exception e) {}
        p.setEstado(txtEstado.getText().trim());
        tablaPacientes.refresh();
        limpiarCampos();
    }

    private void eliminarSeleccionados() {
        if (seleccionados.isEmpty()) { mostrarAlerta("Seleccione al menos un paciente"); return; }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText("¿Eliminar " + seleccionados.size() + " paciente(s)?");
        if (alert.showAndWait().get() == ButtonType.OK) {
            items.removeAll(seleccionados);
            seleccionados.clear();
        }
    }

    private void limpiarCampos() {
        txtNumHistoria.clear(); txtNombre.clear(); txtDireccion.clear(); txtFechaNac.clear(); txtEstado.clear();
        tablaPacientes.getSelectionModel().clearSelection();
    }
    private void mostrarAlerta(String msg) { Alert a = new Alert(Alert.AlertType.WARNING); a.setHeaderText(null); a.setContentText(msg); a.showAndWait(); }
}