package com.mycompany.gestorui.controllers.Gestion;

import com.jfoenix.controls.JFXTextField;
import com.mycompany.gestorui.model.entidades.Consulta;
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

public class GestionConsultaController implements Initializable {

    @FXML private TableView<Consulta> tablaConsultas;
    @FXML private TableColumn<Consulta, Boolean> colSeleccion;
    @FXML private TableColumn<Consulta, Integer> colNumTurno;
    @FXML private TableColumn<Consulta, String> colNumHistoria, colCausa;
    @FXML private TableColumn<Consulta, Boolean> colAtendido;
    @FXML private JFXTextField txtNumTurno, txtNumHistoria, txtCausa;
    @FXML private CheckBox chkAtendido;
    @FXML private Button btnAgregar, btnModificar, btnEliminar, btnLimpiar;
    @FXML private AnchorPane panelCrud;
    @FXML private Button btnTogglePanel;

    private ObservableList<Consulta> items = FXCollections.observableArrayList();
    private Set<Consulta> seleccionados = new HashSet<>();
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
                    Consulta c = getTableRow().getItem();
                    if (c != null) {
                        if (cb.isSelected()) seleccionados.add(c);
                        else seleccionados.remove(c);
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
            tablaConsultas.refresh();
        });
        HBox header = new HBox(cbTodos);
        header.setAlignment(Pos.CENTER);
        colSeleccion.setGraphic(header);
        colSeleccion.setSortable(false);
    }

    private void cargarEjemplos() {
        // Datos de ejemplo
        items.addAll(
            new Consulta("P001", 1, false, "Fiebre alta", null),
            new Consulta("P002", 2, true, "", null),
            new Consulta("P003", 3, false, "Dolor de cabeza", null)
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
            tablaConsultas.setPrefWidth(880);
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
        tablaConsultas.setPrefWidth(562);
        btnTogglePanel.setText("Ocultar Panel");
        panelVisible = true;
    }

    private void agregar() {
        try {
            String numTurnoStr = txtNumTurno.getText().trim();
            if (numTurnoStr.isEmpty()) {
                mostrarAlerta("Número de turno es obligatorio");
                return;
            }
            int numTurno = Integer.parseInt(numTurnoStr);
            
            String numHistoria = txtNumHistoria.getText().trim();
            if (numHistoria.isEmpty()) {
                mostrarAlerta("Número de historia clínica es obligatorio");
                return;
            }
            
            boolean atendido = chkAtendido.isSelected();
            String causa = atendido ? "" : txtCausa.getText().trim();
            
            // Verificar si ya existe
            boolean existe = items.stream().anyMatch(c -> c.getNumeroT() == numTurno);
            if (existe) {
                mostrarAlerta("Ya existe una consulta con ese número de turno");
                return;
            }
            
            Consulta c = new Consulta(numHistoria, numTurno, atendido, causa, null);
            items.add(c);
            limpiarCampos();
            mostrarAlerta("Consulta agregada correctamente", Alert.AlertType.INFORMATION);
        } catch (NumberFormatException e) {
            mostrarAlerta("El número de turno debe ser un valor numérico");
        }
    }

    private void modificar() {
        Consulta c = tablaConsultas.getSelectionModel().getSelectedItem();
        if (c == null) {
            mostrarAlerta("Seleccione una consulta de la tabla");
            return;
        }
        
        try {
            int nuevoNumTurno = Integer.parseInt(txtNumTurno.getText().trim());
            String nuevoNumHistoria = txtNumHistoria.getText().trim();
            
            if (nuevoNumHistoria.isEmpty()) {
                mostrarAlerta("Número de historia clínica es obligatorio");
                return;
            }
            
            // Verificar si el nuevo número de turno ya existe (si es diferente al original)
            if (nuevoNumTurno != c.getNumeroT()) {
                boolean existe = items.stream().anyMatch(cons -> cons.getNumeroT() == nuevoNumTurno);
                if (existe) {
                    mostrarAlerta("Ya existe otra consulta con ese número de turno");
                    return;
                }
            }
            
            c.setNumeroT(nuevoNumTurno);
            c.setNumH(nuevoNumHistoria);
            c.setAtend(chkAtendido.isSelected());
            c.setCausa(chkAtendido.isSelected() ? "" : txtCausa.getText().trim());
            
            tablaConsultas.refresh();
            limpiarCampos();
            mostrarAlerta("Consulta modificada correctamente", Alert.AlertType.INFORMATION);
        } catch (NumberFormatException e) {
            mostrarAlerta("El número de turno debe ser un valor numérico");
        }
    }

    private void eliminarSeleccionados() {
        if (seleccionados.isEmpty()) {
            mostrarAlerta("Seleccione al menos una consulta");
            return;
        }
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText(null);
        alert.setContentText("¿Eliminar " + seleccionados.size() + " consulta(s)?");
        
        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            items.removeAll(seleccionados);
            seleccionados.clear();
            mostrarAlerta("Consulta(s) eliminada(s) correctamente", Alert.AlertType.INFORMATION);
        }
    }

    private void limpiarCampos() {
        txtNumTurno.clear();
        txtNumHistoria.clear();
        chkAtendido.setSelected(false);
        txtCausa.clear();
        tablaConsultas.getSelectionModel().clearSelection();
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