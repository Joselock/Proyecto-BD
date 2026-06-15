package com.mycompany.gestorui.controllers.Gestion;

import java.net.URL;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

import com.jfoenix.controls.JFXTextField;
import com.mycompany.gestorui.model.entidades.Consulta;

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

public class GestionConsultaController implements Initializable {

    @FXML private TableView<Consulta> tablaConsultas;
    @FXML private TableColumn<Consulta, Boolean> colSeleccion;
    @FXML private TableColumn<Consulta, Integer> colNumTurno;
    @FXML private TableColumn<Consulta, String> colNumHistoria, colCausa;
    @FXML private TableColumn<Consulta, Boolean> colAtendido;
    @FXML private JFXTextField txtNumTurno, txtNumHistoria, txtCausa;
    @FXML private CheckBox chkAtendido;
    @FXML private Label agregar, modificar, eliminar, limpiar;

    private ObservableList<Consulta> items = FXCollections.observableArrayList();
    private Set<Consulta> seleccionados = new HashSet<>();

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
            { cb.setOnAction(e -> { Consulta c = getTableRow().getItem(); if (c != null) { if (cb.isSelected()) seleccionados.add(c); else seleccionados.remove(c); } }); }
            @Override public void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) { setGraphic(null); return; }
                cb.setSelected(seleccionados.contains(getTableRow().getItem()));
                setGraphic(cb); setAlignment(Pos.CENTER);
            }
        });
        colSeleccion.setEditable(false); colSeleccion.setPrefWidth(60); colSeleccion.setText("");
        colNumTurno.setCellValueFactory(new PropertyValueFactory<>("numeroT"));
        colNumHistoria.setCellValueFactory(new PropertyValueFactory<>("numH"));
        colAtendido.setCellValueFactory(new PropertyValueFactory<>("atend"));
        colCausa.setCellValueFactory(new PropertyValueFactory<>("causa"));
        tablaConsultas.setItems(items);
        tablaConsultas.setOnMouseClicked(ev -> {
            if (ev.getClickCount() == 2) {
                Consulta c = tablaConsultas.getSelectionModel().getSelectedItem();
                if (c != null) {
                    txtNumTurno.setText(String.valueOf(c.getNumeroT()));
                    txtNumHistoria.setText(c.getNumH());
                    chkAtendido.setSelected(c.isAtend());
                    txtCausa.setText(c.getCausa());
                }
            }
        });
    }

    private void configurarCheckBox() {
        CheckBox cb = new CheckBox("Seleccionar todos");
        cb.selectedProperty().addListener((obs, old, val) -> {
            if (val) seleccionados.addAll(items); else seleccionados.clear();
            tablaConsultas.refresh();
        });
        HBox h = new HBox(cb); h.setAlignment(Pos.CENTER);
        colSeleccion.setGraphic(h); colSeleccion.setSortable(false);
    }

    private void cargarEjemplos() {
        // items.add(new Consulta("P001", 1, false, "", null));
    }

    private void configurarEventos() {
        agregar.setOnMouseClicked(e -> agregar());
        modificar.setOnMouseClicked(e -> modificar());
        eliminar.setOnMouseClicked(e -> eliminarSeleccionados());
        limpiar.setOnMouseClicked(e -> limpiarCampos());
    }

    private void agregar() {
        String numTurnoStr = txtNumTurno.getText().trim();
        if (numTurnoStr.isEmpty()) { mostrarAlerta("Número de turno es obligatorio"); return; }
        int numTurno = Integer.parseInt(numTurnoStr);
        String numHistoria = txtNumHistoria.getText().trim();
        if (numHistoria.isEmpty()) { mostrarAlerta("Número de historia clínica es obligatorio"); return; }
        boolean atendido = chkAtendido.isSelected();
        String causa = atendido ? "" : txtCausa.getText().trim();
        Consulta c = new Consulta(numHistoria, numTurno, atendido, causa, null);
        items.add(c);
        limpiarCampos();
    }

    private void modificar() {
        Consulta c = tablaConsultas.getSelectionModel().getSelectedItem();
        if (c == null) { mostrarAlerta("Seleccione una consulta"); return; }
        c.setNumeroT(Integer.parseInt(txtNumTurno.getText().trim()));
        c.setNumH(txtNumHistoria.getText().trim());
        c.setAtend(chkAtendido.isSelected());
        c.setCausa(chkAtendido.isSelected() ? "" : txtCausa.getText().trim());
        tablaConsultas.refresh();
        limpiarCampos();
    }

    private void eliminarSeleccionados() {
        if (seleccionados.isEmpty()) { mostrarAlerta("Seleccione al menos una consulta"); return; }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText("¿Eliminar " + seleccionados.size() + " consulta(s)?");
        if (alert.showAndWait().get() == ButtonType.OK) {
            items.removeAll(seleccionados);
            seleccionados.clear();
        }
    }

    private void limpiarCampos() {
        txtNumTurno.clear(); txtNumHistoria.clear(); chkAtendido.setSelected(false); txtCausa.clear();
        tablaConsultas.getSelectionModel().clearSelection();
    }

    private void mostrarAlerta(String msg) { Alert a = new Alert(Alert.AlertType.WARNING); a.setHeaderText(null); a.setContentText(msg); a.showAndWait(); }
}