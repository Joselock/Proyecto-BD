package com.mycompany.gestorui.controllers.Gestion;

import com.jfoenix.controls.JFXTextField;
import com.mycompany.gestorui.model.entidades.Informe;
import com.mycompany.gestorui.model.services.crud.crudInforme;

import javafx.animation.*;
import javafx.application.Platform;
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
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

public class GestionInformeController implements Initializable {

    @FXML private TableView<Informe> tablaInformes;
    @FXML private TableColumn<Informe, Boolean> colSeleccion;
    @FXML private TableColumn<Informe, String> colNumInforme;
    @FXML private TableColumn<Informe, Integer> colTurno;
    @FXML private TableColumn<Informe, String> colUnidad;
    @FXML private TableColumn<Informe, Time> colHora;
    @FXML private TableColumn<Informe, Date> colFecha;
    @FXML private TableColumn<Informe, Integer> colPacAtendidos;
    @FXML private TableColumn<Informe, Integer> colPacAltas;
    @FXML private TableColumn<Informe, Integer> colCantAdmitidos;
    @FXML private TableColumn<Informe, Integer> colTotal;
    
    @FXML private JFXTextField txtNumInforme, txtHora, txtFecha, txtPacAtendidos, txtPacAltas, txtCantAdmitidos, txtTotal;
    @FXML private Button btnEliminar, btnLimpiar;
    @FXML private AnchorPane panelCrud;
    @FXML private Button btnTogglePanel;

    private ObservableList<Informe> items = FXCollections.observableArrayList();
    private Set<Informe> seleccionados = new HashSet<>();
    private boolean panelVisible = true;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
        configurarCheckBoxSeleccionarTodos();
        cargarDatos();
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

        // Configurar columnas con las propiedades de la entidad Informe
        colNumInforme.setCellValueFactory(new PropertyValueFactory<>("numIn"));
        colHora.setCellValueFactory(new PropertyValueFactory<>("hora"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colPacAtendidos.setCellValueFactory(new PropertyValueFactory<>("pacAtend"));
        colPacAltas.setCellValueFactory(new PropertyValueFactory<>("pacAlta"));
        colCantAdmitidos.setCellValueFactory(new PropertyValueFactory<>("cantAdm"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        colTurno.setCellValueFactory(new PropertyValueFactory<>("numeroTurno"));
        colUnidad.setCellValueFactory(new PropertyValueFactory<>("codigoUni"));
        
        tablaInformes.setItems(items);
        
        tablaInformes.setOnMouseClicked(ev -> {
            if (ev.getClickCount() == 2) {
                Informe i = tablaInformes.getSelectionModel().getSelectedItem();
                if (i != null) {
                    cargarDatosEnPanel(i);
                    if (!panelVisible) mostrarPanel();
                }
            }
        });
    }

    private void cargarDatosEnPanel(Informe i) {
        txtNumInforme.setText(i.getNumIn());
        txtHora.setText(i.getHora() != null ? i.getHora().toString() : "");
        txtFecha.setText(i.getFecha() != null ? i.getFecha().toString() : "");
        txtPacAtendidos.setText(String.valueOf(i.getPacAtend()));
        txtPacAltas.setText(String.valueOf(i.getPacAlta()));
        txtCantAdmitidos.setText(String.valueOf(i.getCantAdm()));
        txtTotal.setText(String.valueOf(i.getTotal()));
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

    private void cargarDatos() {
        tablaInformes.setPlaceholder(new ProgressIndicator());
        
        new Thread(() -> {
            try {
                List<Informe> lista = crudInforme.obtenerInformes();
                
                Platform.runLater(() -> {
                    items.clear();
                    items.addAll(lista);
                    tablaInformes.setPlaceholder(new Label("No hay informes"));
                    
                    // Debug: Verificar que los datos tienen Turno y Unidad
                    for (Informe i : items) {
                        System.out.println("Informe: " + i.getNumIn() + 
                                         ", Turno: " + i.getNumeroTurno() + 
                                         ", Unidad: " + i.getCodigoUni());
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    mostrarAlerta("Error al cargar datos: " + e.getMessage(), Alert.AlertType.ERROR);
                    tablaInformes.setPlaceholder(new Label("Error al cargar datos"));
                });
                e.printStackTrace();
            }
        }).start();
    }

    private void configurarEventos() {
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
            List<Informe> aEliminar = List.copyOf(seleccionados);
            boolean todosEliminados = true;
            StringBuilder errores = new StringBuilder();
            
            for (Informe inf : aEliminar) {
                String resultado = crudInforme.eliminarInforme(inf.getNumIn());
                
                if (resultado != null && !resultado.toLowerCase().contains("error")) {
                    items.remove(inf);
                    seleccionados.remove(inf);
                } else {
                    todosEliminados = false;
                    errores.append("• ").append(inf.getNumIn()).append(": ").append(resultado).append("\n");
                }
            }
            
            if (todosEliminados) {
                mostrarAlerta("Informe(s) eliminado(s) correctamente", Alert.AlertType.INFORMATION);
            } else {
                mostrarAlerta("Algunos informes no se eliminaron:\n" + errores.toString(), Alert.AlertType.WARNING);
                cargarDatos();
            }
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