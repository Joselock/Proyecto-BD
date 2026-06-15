package com.mycompany.gestorui.controllers.Gestion;

import java.net.URL;
import java.sql.Date;
import java.sql.Time;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

import com.jfoenix.controls.JFXTextField;
import com.mycompany.gestorui.model.entidades.Informe;

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

public class GestionInformeController implements Initializable {

    @FXML private TableView<Informe> tablaInformes;
    @FXML private TableColumn<Informe, Boolean> colSeleccion;
    @FXML private TableColumn<Informe, String> colNumInforme;
    @FXML private TableColumn<Informe, Time> colHora;
    @FXML private TableColumn<Informe, Date> colFecha;
    @FXML private TableColumn<Informe, Integer> colPacAtendidos, colPacAltas, colCantAdmitidos, colTotal;
    @FXML private JFXTextField txtNumInforme, txtHora, txtFecha, txtPacAtendidos, txtPacAltas, txtCantAdmitidos, txtTotal;
    @FXML private Label agregar, modificar, eliminar, limpiar;

    private ObservableList<Informe> items = FXCollections.observableArrayList();
    private Set<Informe> seleccionados = new HashSet<>();

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
            { cb.setOnAction(e -> { Informe i = getTableRow().getItem(); if (i != null) { if (cb.isSelected()) seleccionados.add(i); else seleccionados.remove(i); } }); }
            @Override public void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) { setGraphic(null); return; }
                cb.setSelected(seleccionados.contains(getTableRow().getItem()));
                setGraphic(cb); setAlignment(Pos.CENTER);
            }
        });
        colSeleccion.setEditable(false); colSeleccion.setPrefWidth(60); colSeleccion.setText("");
        colNumInforme.setCellValueFactory(new PropertyValueFactory<>("numIn"));
        colHora.setCellValueFactory(new PropertyValueFactory<>("hora"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colPacAtendidos.setCellValueFactory(new PropertyValueFactory<>("pacAtend"));
        colPacAltas.setCellValueFactory(new PropertyValueFactory<>("pacAlta"));
        colCantAdmitidos.setCellValueFactory(new PropertyValueFactory<>("cantAdm"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        tablaInformes.setItems(items);
        tablaInformes.setOnMouseClicked(ev -> {
            if (ev.getClickCount() == 2) {
                Informe i = tablaInformes.getSelectionModel().getSelectedItem();
                if (i != null) {
                    txtNumInforme.setText(i.getNumIn());
                    txtHora.setText(i.getHora() != null ? i.getHora().toString() : "");
                    txtFecha.setText(i.getFecha() != null ? i.getFecha().toString() : "");
                    txtPacAtendidos.setText(String.valueOf(i.getPacAtend()));
                    txtPacAltas.setText(String.valueOf(i.getPacAlta()));
                    txtCantAdmitidos.setText(String.valueOf(i.getCantAdm()));
                    txtTotal.setText(String.valueOf(i.getTotal()));
                }
            }
        });
    }

    private void configurarCheckBox() {
        CheckBox cb = new CheckBox("Seleccionar todos");
        cb.selectedProperty().addListener((obs, old, val) -> {
            if (val) seleccionados.addAll(items); else seleccionados.clear();
            tablaInformes.refresh();
        });
        HBox h = new HBox(cb); h.setAlignment(Pos.CENTER);
        colSeleccion.setGraphic(h); colSeleccion.setSortable(false);
    }

    private void cargarEjemplos() {
        // items.add(new Informe(...)); // Añadir ejemplos si quieres
    }

    private void configurarEventos() {
        agregar.setOnMouseClicked(e -> agregar());
        modificar.setOnMouseClicked(e -> modificar());
        eliminar.setOnMouseClicked(e -> eliminarSeleccionados());
        limpiar.setOnMouseClicked(e -> limpiarCampos());
    }

    private void agregar() {
        String num = txtNumInforme.getText().trim();
        if (num.isEmpty()) { mostrarAlerta("Número de informe es obligatorio"); return; }
        Time hora = null;
        Date fecha = null;
        try { hora = Time.valueOf(txtHora.getText().trim()); } catch (Exception e) {}
        try { fecha = Date.valueOf(txtFecha.getText().trim()); } catch (Exception e) {}
        int atend = parseInt(txtPacAtendidos.getText());
        int altas = parseInt(txtPacAltas.getText());
        int adm = parseInt(txtCantAdmitidos.getText());
        int total = parseInt(txtTotal.getText());
        // Usamos un constructor adecuado: Informe(Time hora, Date fecha, String numIn, int pacAtend, int pacAlta, int cantAdm, int total, int cantIni, int cantAnterior)
        // Pasamos valores por defecto para cantIni y cantAnterior (0)
        Informe i = new Informe(hora, fecha, num, atend, altas, adm, total, 0, 0);
        items.add(i);
        limpiarCampos();
    }

    private void modificar() {
        Informe i = tablaInformes.getSelectionModel().getSelectedItem();
        if (i == null) { mostrarAlerta("Seleccione un informe"); return; }
        i.setNumIn(txtNumInforme.getText().trim());
        try { i.setHora(Time.valueOf(txtHora.getText().trim())); } catch (Exception e) {}
        try { i.setFecha(Date.valueOf(txtFecha.getText().trim())); } catch (Exception e) {}
        i.setPacAtend(parseInt(txtPacAtendidos.getText()));
        i.setPacAlta(parseInt(txtPacAltas.getText()));
        i.setCantAdm(parseInt(txtCantAdmitidos.getText()));
        i.setTotal(parseInt(txtTotal.getText()));
        tablaInformes.refresh();
        limpiarCampos();
    }

    private void eliminarSeleccionados() {
        if (seleccionados.isEmpty()) { mostrarAlerta("Seleccione al menos un informe"); return; }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText("¿Eliminar " + seleccionados.size() + " informe(s)?");
        if (alert.showAndWait().get() == ButtonType.OK) {
            items.removeAll(seleccionados);
            seleccionados.clear();
        }
    }

    private void limpiarCampos() {
        txtNumInforme.clear(); txtHora.clear(); txtFecha.clear();
        txtPacAtendidos.clear(); txtPacAltas.clear(); txtCantAdmitidos.clear(); txtTotal.clear();
        tablaInformes.getSelectionModel().clearSelection();
    }

    private int parseInt(String s) {
        try { return Integer.parseInt(s.trim()); } catch (Exception e) { return 0; }
    }

    private void mostrarAlerta(String msg) { Alert a = new Alert(Alert.AlertType.WARNING); a.setHeaderText(null); a.setContentText(msg); a.showAndWait(); }
}