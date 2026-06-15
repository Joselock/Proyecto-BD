package com.mycompany.gestorui.controllers.Gestion;

import java.net.URL;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

import com.jfoenix.controls.JFXTextField;
import com.mycompany.gestorui.model.entidades.Turno;

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

public class GestionTurnoController implements Initializable {

    @FXML private TableView<Turno> tablaTurnos;
    @FXML private TableColumn<Turno, Boolean> colSeleccion;
    @FXML private TableColumn<Turno, Integer> colNumero, colCantAtendidos;
    @FXML private TableColumn<Turno, String> colEstado, colUnidad, colMedico;
    @FXML private JFXTextField txtNumero, txtCantAtendidos, txtEstado, txtCodUnidad, txtCodMedico;
    @FXML private Label agregar, modificar, eliminar, limpiar;

    private ObservableList<Turno> items = FXCollections.observableArrayList();
    private Set<Turno> seleccionados = new HashSet<>();

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
            { cb.setOnAction(e -> { Turno t = getTableRow().getItem(); if (t != null) { if (cb.isSelected()) seleccionados.add(t); else seleccionados.remove(t); } }); }
            @Override public void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) { setGraphic(null); return; }
                cb.setSelected(seleccionados.contains(getTableRow().getItem()));
                setGraphic(cb); setAlignment(Pos.CENTER);
            }
        });
        colSeleccion.setEditable(false); colSeleccion.setPrefWidth(60); colSeleccion.setText("");
        colNumero.setCellValueFactory(new PropertyValueFactory<>("numTurn"));
        colCantAtendidos.setCellValueFactory(new PropertyValueFactory<>("cantAten"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estTur"));
        colUnidad.setCellValueFactory(new PropertyValueFactory<>("codUni"));
        colMedico.setCellValueFactory(new PropertyValueFactory<>("medico")); // muestra el objeto Medico, se verá como referencia
        tablaTurnos.setItems(items);
        tablaTurnos.setOnMouseClicked(ev -> {
            if (ev.getClickCount() == 2) {
                Turno t = tablaTurnos.getSelectionModel().getSelectedItem();
                if (t != null) {
                    txtNumero.setText(String.valueOf(t.getNumTurn()));
                    txtCantAtendidos.setText(String.valueOf(t.getCantAten()));
                    txtEstado.setText(t.getEstTur());
                    txtCodUnidad.setText(t.getCodUni());
                    txtCodMedico.setText(t.getMedico() != null ? t.getMedico().getCodigoMed() : "");
                }
            }
        });
    }

    private void configurarCheckBox() {
        CheckBox cb = new CheckBox("Seleccionar todos");
        cb.selectedProperty().addListener((obs, old, val) -> {
            if (val) seleccionados.addAll(items); else seleccionados.clear();
            tablaTurnos.refresh();
        });
        HBox h = new HBox(cb); h.setAlignment(Pos.CENTER);
        colSeleccion.setGraphic(h); colSeleccion.setSortable(false);
    }

    private void cargarEjemplos() {
        // Simulación: crear un médico de ejemplo
        // Turno(int numTurn, int cantAten, String estTur, String codUni, Medico medico)
        // items.add(new Turno(1, 10, "Activo", "U001", new Medico(...)));
    }

    private void configurarEventos() {
        agregar.setOnMouseClicked(e -> agregar());
        modificar.setOnMouseClicked(e -> modificar());
        eliminar.setOnMouseClicked(e -> eliminarSeleccionados());
        limpiar.setOnMouseClicked(e -> limpiarCampos());
    }

    private void agregar() {
        String numStr = txtNumero.getText().trim();
        if (numStr.isEmpty()) { mostrarAlerta("Número de turno requerido"); return; }
        int num = Integer.parseInt(numStr);
        int cant = txtCantAtendidos.getText().trim().isEmpty() ? 0 : Integer.parseInt(txtCantAtendidos.getText().trim());
        Turno t = new Turno(num, cant, txtEstado.getText().trim(), txtCodUnidad.getText().trim(), null);
        items.add(t);
        limpiarCampos();
    }

    private void modificar() {
        Turno t = tablaTurnos.getSelectionModel().getSelectedItem();
        if (t == null) { mostrarAlerta("Seleccione un turno"); return; }
        t.setNumTurn(Integer.parseInt(txtNumero.getText().trim()));
        t.setCantAten(Integer.parseInt(txtCantAtendidos.getText().trim()));
        t.setEstTur(txtEstado.getText().trim());
        t.setCodUni(txtCodUnidad.getText().trim());
        tablaTurnos.refresh();
        limpiarCampos();
    }

    private void eliminarSeleccionados() {
        if (seleccionados.isEmpty()) { mostrarAlerta("Seleccione al menos un turno"); return; }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText("¿Eliminar " + seleccionados.size() + " turno(s)?");
        if (alert.showAndWait().get() == ButtonType.OK) {
            items.removeAll(seleccionados);
            seleccionados.clear();
        }
    }

    private void limpiarCampos() {
        txtNumero.clear(); txtCantAtendidos.clear(); txtEstado.clear(); txtCodUnidad.clear(); txtCodMedico.clear();
        tablaTurnos.getSelectionModel().clearSelection();
    }
    private void mostrarAlerta(String msg) { Alert a = new Alert(Alert.AlertType.WARNING); a.setHeaderText(null); a.setContentText(msg); a.showAndWait(); }
}