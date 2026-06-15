/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.gestorui.controllers.Gestion;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 *
 * @author ignacio
 */
public class GestionController implements Initializable {
    
    @FXML
    private Label cerrar;
    
    @FXML
    private Label minimizar;

    @FXML
    private StackPane contentPane;

    @FXML
    private AnchorPane menuPane;

    @FXML
    private Button btnBack;

    private Map<String, Node> views = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            loadAllViews();
            showView("menu");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private void loadAllViews() {
        try {
            addView("hospital", "/com/mycompany/gestorui/views/Gestion/Hospital.fxml");
            addView("departamento", "/com/mycompany/gestorui/views/Gestion/GestionDepartamentoFXML.fxml");
            addView("unidad", "/com/mycompany/gestorui/views/Gestion/GestionUnidadFXML.fxml");
            addView("medico", "/com/mycompany/gestorui/views/Gestion/GestionMedicoFXML.fxml");
            addView("turno", "/com/mycompany/gestorui/views/Gestion/GestionTurnoFXML.fxml");
            addView("informe", "/com/mycompany/gestorui/views/Gestion/GestionInformeFXML.fxml");
            addView("consulta", "/com/mycompany/gestorui/views/Gestion/GestionConsultaFXML.fxml");
            addView("paciente", "/com/mycompany/gestorui/views/Gestion/GestionPacienteFXML.fxml");
        } catch (Exception ex) {
            System.err.println("Error cargando vistas de gestión");
            ex.printStackTrace();
        }
    }

    private void addView(String key, String fxmlPath) throws Exception {
        Parent view = FXMLLoader.load(getClass().getResource(fxmlPath));
        view.setVisible(false);
        view.setManaged(false);
        views.put(key, view);
        contentPane.getChildren().add(view);
    }

    private void showView(String key) {
        boolean menuVisible = "menu".equals(key);
        if (menuPane != null) {
            menuPane.setVisible(menuVisible);
            menuPane.setManaged(menuVisible);
        }
        if (btnBack != null) {
            btnBack.setVisible(!menuVisible);
            btnBack.setManaged(!menuVisible);
        }
        views.forEach((id, view) -> {
            boolean visible = id.equals(key);
            view.setVisible(visible);
            view.setManaged(visible);
        });
    }

    @FXML
    private void handleBack(ActionEvent event) {
        showView("menu");
    }

    @FXML  
    private void handleHospital(MouseEvent event){
        showView("hospital");
    }
    
    @FXML
    private void handleDepartamento(MouseEvent event){
        showView("departamento");
    }
    
    @FXML
    private void handleUnidad(MouseEvent event){
        showView("unidad");
    }
    
    @FXML
    private void handleMedico(MouseEvent event){
        showView("medico");
    }
    
    @FXML
    private void handleTurno(MouseEvent event){
        showView("turno");
    }
    
    @FXML
    private void handleInforme(MouseEvent event){
        showView("informe");
    }
    
    @FXML
    private void handleConsulta (MouseEvent event){
        showView("consulta");
    }
    
    @FXML
    private void handlePaciente(MouseEvent event){
        showView("paciente");
    }

    @FXML
    private void handleMinimize(MouseEvent event) {
        // Obtener el Stage (ventana) desde el elemento que disparó el evento
        Stage stage = (Stage) ((Label) event.getSource()).getScene().getWindow();
        // Minimizar la ventana
        stage.setIconified(true);
    }

    @FXML
    private void handleClose(MouseEvent event) {
        Stage stage = (Stage) cerrar.getScene().getWindow();
        stage.close();
    }

}
