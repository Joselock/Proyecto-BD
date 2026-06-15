package com.mycompany.gestorui.controllers.Gestion;

import com.jfoenix.controls.JFXTextField;
import com.mycompany.gestorui.model.entidades.Turno;
import com.mycompany.gestorui.model.entidades.Medico;
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

public class GestionTurnoController implements Initializable {

    @FXML private TableView<Turno> tablaTurnos;
    @FXML private TableColumn<Turno, Boolean> colSeleccion;
    @FXML private TableColumn<Turno, Integer> colNumero, colCantAtendidos;
    @FXML private TableColumn<Turno, String> colEstado, colUnidad, colMedico;
    @FXML private JFXTextField txtNumero, txtCantAtendidos, txtEstado, txtCodUnidad, txtCodMedico;
    @FXML private Button btnAgregar, btnModificar, btnEliminar, btnLimpiar;
    @FXML private AnchorPane panelCrud;
    @FXML private Button btnTogglePanel;

    private ObservableList<Turno> items = FXCollections.observableArrayList();
    private Set<Turno> seleccionados = new HashSet<>();
    private boolean panelVisible = true;
    
    // Para simular médicos existentes (esto debería venir de la BD)
    private ObservableList<Medico> medicosDisponibles = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarMedicosEjemplo();
        configurarTabla();
        configurarCheckBoxSeleccionarTodos();
        cargarEjemplos();
        configurarEventos();
        configurarPanelToggle();
    }

    private void cargarMedicosEjemplo() {
        medicosDisponibles.addAll(
            new Medico("M001", "Dr. Juan Pérez", "Cardiología", "LIC123", "juan@mail.com", 10, "555-1234"),
            new Medico("M002", "Dra. María López", "Neurología", "LIC456", "maria@mail.com", 8, "555-5678")
        );
    }

    private void configurarTabla() {
        colSeleccion.setCellFactory(col -> new CheckBoxTableCell<>() {
            private final CheckBox cb = new CheckBox();
            {
                cb.setOnAction(e -> {
                    Turno t = getTableRow().getItem();
                    if (t != null) {
                        if (cb.isSelected()) seleccionados.add(t);
                        else seleccionados.remove(t);
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

        colNumero.setCellValueFactory(new PropertyValueFactory<>("numTurn"));
        colCantAtendidos.setCellValueFactory(new PropertyValueFactory<>("cantAten"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estTur"));
        colUnidad.setCellValueFactory(new PropertyValueFactory<>("codUni"));
        colMedico.setCellValueFactory(new PropertyValueFactory<>("medico"));
        
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
            tablaTurnos.refresh();
        });
        HBox header = new HBox(cbTodos);
        header.setAlignment(Pos.CENTER);
        colSeleccion.setGraphic(header);
        colSeleccion.setSortable(false);
    }

    private void cargarEjemplos() {
        Medico medico1 = medicosDisponibles.get(0);
        items.addAll(
            new Turno(1, 10, "Activo", "U001", medico1),
            new Turno(2, 5, "Pendiente", "U002", null),
            new Turno(3, 8, "Finalizado", "U001", medicosDisponibles.get(1))
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
            tablaTurnos.setPrefWidth(880);
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
        tablaTurnos.setPrefWidth(562);
        btnTogglePanel.setText("Ocultar Panel");
        panelVisible = true;
    }

    private void agregar() {
        try {
            String numStr = txtNumero.getText().trim();
            if (numStr.isEmpty()) {
                mostrarAlerta("Número de turno es obligatorio");
                return;
            }
            int num = Integer.parseInt(numStr);
            
            boolean existe = items.stream().anyMatch(t -> t.getNumTurn() == num);
            if (existe) {
                mostrarAlerta("Ya existe un turno con ese número");
                return;
            }
            
            int cant = 0;
            if (!txtCantAtendidos.getText().trim().isEmpty()) {
                cant = Integer.parseInt(txtCantAtendidos.getText().trim());
            }
            
            String codMedico = txtCodMedico.getText().trim();
            Medico medico = null;
            if (!codMedico.isEmpty()) {
                medico = medicosDisponibles.stream()
                    .filter(m -> m.getCodigoMed().equals(codMedico))
                    .findFirst()
                    .orElse(null);
                if (medico == null) {
                    mostrarAlerta("No existe un médico con ese código");
                    return;
                }
            }
            
            Turno t = new Turno(num, cant, txtEstado.getText().trim(), txtCodUnidad.getText().trim(), medico);
            items.add(t);
            limpiarCampos();
            mostrarAlerta("Turno agregado correctamente", Alert.AlertType.INFORMATION);
        } catch (NumberFormatException e) {
            mostrarAlerta("Los valores numéricos deben ser válidos");
        }
    }

    private void modificar() {
        Turno t = tablaTurnos.getSelectionModel().getSelectedItem();
        if (t == null) {
            mostrarAlerta("Seleccione un turno de la tabla");
            return;
        }
        
        try {
            int nuevoNum = Integer.parseInt(txtNumero.getText().trim());
            
            if (nuevoNum != t.getNumTurn()) {
                boolean existe = items.stream().anyMatch(turno -> turno.getNumTurn() == nuevoNum);
                if (existe) {
                    mostrarAlerta("Ya existe otro turno con ese número");
                    return;
                }
            }
            
            t.setNumTurn(nuevoNum);
            t.setCantAten(Integer.parseInt(txtCantAtendidos.getText().trim()));
            t.setEstTur(txtEstado.getText().trim());
            t.setCodUni(txtCodUnidad.getText().trim());
            
            String codMedico = txtCodMedico.getText().trim();
            if (!codMedico.isEmpty()) {
                Medico medico = medicosDisponibles.stream()
                    .filter(m -> m.getCodigoMed().equals(codMedico))
                    .findFirst()
                    .orElse(null);
                t.setMedico(medico);
            } else {
                t.setMedico(null);
            }
            
            tablaTurnos.refresh();
            limpiarCampos();
            mostrarAlerta("Turno modificado correctamente", Alert.AlertType.INFORMATION);
        } catch (NumberFormatException e) {
            mostrarAlerta("Los valores numéricos deben ser válidos");
        }
    }

    private void eliminarSeleccionados() {
        if (seleccionados.isEmpty()) {
            mostrarAlerta("Seleccione al menos un turno");
            return;
        }
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText(null);
        alert.setContentText("¿Eliminar " + seleccionados.size() + " turno(s)?");
        
        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            items.removeAll(seleccionados);
            seleccionados.clear();
            mostrarAlerta("Turno(s) eliminado(s) correctamente", Alert.AlertType.INFORMATION);
        }
    }

    private void limpiarCampos() {
        txtNumero.clear();
        txtCantAtendidos.clear();
        txtEstado.clear();
        txtCodUnidad.clear();
        txtCodMedico.clear();
        tablaTurnos.getSelectionModel().clearSelection();
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