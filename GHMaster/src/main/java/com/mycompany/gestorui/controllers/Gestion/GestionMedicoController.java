package com.mycompany.gestorui.controllers.Gestion;

import java.net.URL;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

import com.jfoenix.controls.JFXTextField;
import com.mycompany.gestorui.model.entidades.Medico;

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

public class GestionMedicoController implements Initializable {

    @FXML private TableView<Medico> tablaMedicos;
    @FXML private TableColumn<Medico, Boolean> colSeleccion;
    @FXML private TableColumn<Medico, String> colCodigo, colNombre, colEspecialidad, colLicencia, colTelefono;
    @FXML private JFXTextField txtCodigo, txtNombre, txtEspecialidad, txtLicencia, txtTelefono, txtDatosContacto, txtExperiencia;
    @FXML private Label agregar, modificar, eliminar, limpiar;

    private ObservableList<Medico> items = FXCollections.observableArrayList();
    private Set<Medico> seleccionados = new HashSet<>();

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
            { cb.setOnAction(e -> { Medico m = getTableRow().getItem(); if (m != null) { if (cb.isSelected()) seleccionados.add(m); else seleccionados.remove(m); } }); }
            @Override public void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) { setGraphic(null); return; }
                cb.setSelected(seleccionados.contains(getTableRow().getItem()));
                setGraphic(cb); setAlignment(Pos.CENTER);
            }
        });
        colSeleccion.setEditable(false); colSeleccion.setPrefWidth(60); colSeleccion.setText("");
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigoMed"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombreMed"));
        colEspecialidad.setCellValueFactory(new PropertyValueFactory<>("especialidad"));
        colLicencia.setCellValueFactory(new PropertyValueFactory<>("numeroLic"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        tablaMedicos.setItems(items);
        tablaMedicos.setOnMouseClicked(ev -> {
            if (ev.getClickCount() == 2) {
                Medico m = tablaMedicos.getSelectionModel().getSelectedItem();
                if (m != null) {
                    txtCodigo.setText(m.getCodigoMed());
                    txtNombre.setText(m.getNombreMed());
                    txtEspecialidad.setText(m.getEspecialidad());
                    txtLicencia.setText(m.getNumeroLic());
                    txtTelefono.setText(m.getTelefono());
                    txtDatosContacto.setText(m.getDatosC());
                    txtExperiencia.setText(String.valueOf(m.getExperiencia()));
                }
            }
        });
    }

    private void configurarCheckBox() {
        CheckBox cb = new CheckBox("Seleccionar todos");
        cb.selectedProperty().addListener((obs, old, val) -> {
            if (val) seleccionados.addAll(items); else seleccionados.clear();
            tablaMedicos.refresh();
        });
        HBox h = new HBox(cb); h.setAlignment(Pos.CENTER);
        colSeleccion.setGraphic(h); colSeleccion.setSortable(false);
    }

    private void cargarEjemplos() {
        items.add(new Medico("M001", "Dr. Juan Pérez", "Cardiología", "LIC123", "juan@mail.com", 10, "555-1234"));
    }

    private void configurarEventos() {
        agregar.setOnMouseClicked(e -> agregar());
        modificar.setOnMouseClicked(e -> modificar());
        eliminar.setOnMouseClicked(e -> eliminarSeleccionados());
        limpiar.setOnMouseClicked(e -> limpiarCampos());
    }

    private void agregar() {
        String cod = txtCodigo.getText().trim(), nom = txtNombre.getText().trim();
        if (cod.isEmpty() || nom.isEmpty()) { mostrarAlerta("Código y nombre son obligatorios"); return; }
        int exp = 0;
        try { exp = Integer.parseInt(txtExperiencia.getText().trim()); } catch (NumberFormatException e) { }
        Medico m = new Medico(cod, nom, txtEspecialidad.getText().trim(), txtLicencia.getText().trim(),
                              txtDatosContacto.getText().trim(), exp, txtTelefono.getText().trim());
        items.add(m);
        limpiarCampos();
    }

    private void modificar() {
        Medico m = tablaMedicos.getSelectionModel().getSelectedItem();
        if (m == null) { mostrarAlerta("Seleccione un médico"); return; }
        m.setCodigoMed(txtCodigo.getText().trim());
        m.setNombreMed(txtNombre.getText().trim());
        m.setEspecialidad(txtEspecialidad.getText().trim());
        m.setNumeroLic(txtLicencia.getText().trim());
        m.setTelefono(txtTelefono.getText().trim());
        m.setDatosC(txtDatosContacto.getText().trim());
        try { m.setExperiencia(Integer.parseInt(txtExperiencia.getText().trim())); } catch (NumberFormatException e) {}
        tablaMedicos.refresh();
        limpiarCampos();
    }

    private void eliminarSeleccionados() {
        if (seleccionados.isEmpty()) { mostrarAlerta("Seleccione al menos un médico"); return; }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText("¿Eliminar " + seleccionados.size() + " médico(s)?");
        if (alert.showAndWait().get() == ButtonType.OK) {
            items.removeAll(seleccionados);
            seleccionados.clear();
        }
    }

    private void limpiarCampos() {
        txtCodigo.clear(); txtNombre.clear(); txtEspecialidad.clear(); txtLicencia.clear();
        txtTelefono.clear(); txtDatosContacto.clear(); txtExperiencia.clear();
        tablaMedicos.getSelectionModel().clearSelection();
    }
    private void mostrarAlerta(String msg) { Alert a = new Alert(Alert.AlertType.WARNING); a.setHeaderText(null); a.setContentText(msg); a.showAndWait(); }
}