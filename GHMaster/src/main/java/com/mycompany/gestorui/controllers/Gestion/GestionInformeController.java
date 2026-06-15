package com.mycompany.gestorui.controllers.Gestion;

import com.jfoenix.controls.JFXTextField;
import com.mycompany.gestorui.model.entidades.Informe;
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
import java.sql.Date;
import java.sql.Time;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

public class GestionInformeController implements Initializable {

    @FXML private TableView<Informe> tablaInformes;
    @FXML private TableColumn<Informe, Boolean> colSeleccion;
    @FXML private TableColumn<Informe, String> colNumInforme;
    @FXML private TableColumn<Informe, Time> colHora;
    @FXML private TableColumn<Informe, Date> colFecha;
    @FXML private TableColumn<Informe, Integer> colPacAtendidos, colPacAltas, colCantAdmitidos, colTotal;
    @FXML private JFXTextField txtNumInforme, txtHora, txtFecha, txtPacAtendidos, txtPacAltas, txtCantAdmitidos, txtTotal;
    @FXML private Button btnAgregar, btnModificar, btnEliminar, btnLimpiar;
    @FXML private AnchorPane panelCrud;
    @FXML private Button btnTogglePanel;

    private ObservableList<Informe> items = FXCollections.observableArrayList();
    private Set<Informe> seleccionados = new HashSet<>();
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
                    Informe i = getTableRow().getItem();
                    if (i != null) {
                        if (cb.isSelected()) seleccionados.add(i);
                        else seleccionados.remove(i);
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
            tablaInformes.refresh();
        });
        HBox header = new HBox(cbTodos);
        header.setAlignment(Pos.CENTER);
        colSeleccion.setGraphic(header);
        colSeleccion.setSortable(false);
    }

    private void cargarEjemplos() {
        try {
            items.addAll(
                new Informe(Time.valueOf("08:30:00"), Date.valueOf("2024-01-15"), "INF001", 25, 20, 5, 50, 30, 20),
                new Informe(Time.valueOf("09:00:00"), Date.valueOf("2024-01-16"), "INF002", 30, 25, 5, 60, 35, 25),
                new Informe(Time.valueOf("10:15:00"), Date.valueOf("2024-01-17"), "INF003", 28, 22, 6, 56, 32, 24)
            );
        } catch (Exception e) {
            System.out.println("Error al cargar ejemplos: " + e.getMessage());
        }
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
            tablaInformes.setPrefWidth(880);
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
        tablaInformes.setPrefWidth(562);
        btnTogglePanel.setText("Ocultar Panel");
        panelVisible = true;
    }

    private void agregar() {
        String num = txtNumInforme.getText().trim();
        if (num.isEmpty()) {
            mostrarAlerta("Número de informe es obligatorio");
            return;
        }
        
        boolean existe = items.stream().anyMatch(i -> i.getNumIn().equals(num));
        if (existe) {
            mostrarAlerta("Ya existe un informe con ese número");
            return;
        }
        
        Time hora = null;
        Date fecha = null;
        try {
            hora = Time.valueOf(txtHora.getText().trim());
        } catch (Exception e) {}
        try {
            fecha = Date.valueOf(txtFecha.getText().trim());
        } catch (Exception e) {}
        
        int atend = parseInt(txtPacAtendidos.getText());
        int altas = parseInt(txtPacAltas.getText());
        int adm = parseInt(txtCantAdmitidos.getText());
        int total = parseInt(txtTotal.getText());
        
        Informe i = new Informe(hora, fecha, num, atend, altas, adm, total, 0, 0);
        items.add(i);
        limpiarCampos();
        mostrarAlerta("Informe agregado correctamente", Alert.AlertType.INFORMATION);
    }

    private void modificar() {
        Informe i = tablaInformes.getSelectionModel().getSelectedItem();
        if (i == null) {
            mostrarAlerta("Seleccione un informe de la tabla");
            return;
        }
        
        String nuevoNum = txtNumInforme.getText().trim();
        if (nuevoNum.isEmpty()) {
            mostrarAlerta("Número de informe es obligatorio");
            return;
        }
        
        if (!nuevoNum.equals(i.getNumIn())) {
            boolean existe = items.stream().anyMatch(inf -> inf.getNumIn().equals(nuevoNum));
            if (existe) {
                mostrarAlerta("Ya existe otro informe con ese número");
                return;
            }
        }
        
        i.setNumIn(nuevoNum);
        try {
            i.setHora(Time.valueOf(txtHora.getText().trim()));
        } catch (Exception e) {}
        try {
            i.setFecha(Date.valueOf(txtFecha.getText().trim()));
        } catch (Exception e) {}
        
        i.setPacAtend(parseInt(txtPacAtendidos.getText()));
        i.setPacAlta(parseInt(txtPacAltas.getText()));
        i.setCantAdm(parseInt(txtCantAdmitidos.getText()));
        i.setTotal(parseInt(txtTotal.getText()));
        
        tablaInformes.refresh();
        limpiarCampos();
        mostrarAlerta("Informe modificado correctamente", Alert.AlertType.INFORMATION);
    }

    private void eliminarSeleccionados() {
        if (seleccionados.isEmpty()) {
            mostrarAlerta("Seleccione al menos un informe");
            return;
        }
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText(null);
        alert.setContentText("¿Eliminar " + seleccionados.size() + " informe(s)?");
        
        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            items.removeAll(seleccionados);
            seleccionados.clear();
            mostrarAlerta("Informe(s) eliminado(s) correctamente", Alert.AlertType.INFORMATION);
        }
    }

    private void limpiarCampos() {
        txtNumInforme.clear();
        txtHora.clear();
        txtFecha.clear();
        txtPacAtendidos.clear();
        txtPacAltas.clear();
        txtCantAdmitidos.clear();
        txtTotal.clear();
        tablaInformes.getSelectionModel().clearSelection();
    }

    private int parseInt(String s) {
        try {
            return Integer.parseInt(s.trim());
        } catch (Exception e) {
            return 0;
        }
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