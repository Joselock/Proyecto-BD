package com.mycompany.gestorui.controllers.Reporte;

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
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class ReporteController implements Initializable {
    
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
    
    @FXML
    private Button btnVolverMenu;

    @FXML
    private Label lblTituloReporte;

    private Node currentView;
    
    private static final Map<String, String> TITULOS_REPORTES = new HashMap<>();
    private static final Map<String, String> RUTAS_REPORTES = new HashMap<>();
    
    static {
        TITULOS_REPORTES.put("listadoPacientes", "Listado de Pacientes");
        TITULOS_REPORTES.put("listadoMedicos", "Listado de Médicos");
        TITULOS_REPORTES.put("resumenHospital", "Resumen por Hospitales");
        TITULOS_REPORTES.put("topHospitales", "Top 5 Hospitales con más Pacientes");
        TITULOS_REPORTES.put("informesConsultas", "Informe durante Consultas");
        TITULOS_REPORTES.put("noAtendidos", "Pacientes no Atendidos");
        TITULOS_REPORTES.put("resumenProceso", "Resumen del Proceso");
        TITULOS_REPORTES.put("revisarTurnos", "Revisar Turnos");
        TITULOS_REPORTES.put("consultasExitosas", "Consultas Exitosas");
        
        RUTAS_REPORTES.put("listadoPacientes", "com/mycompany/gestorui/views/Reportes/ListadoPacientes.fxml");
        RUTAS_REPORTES.put("listadoMedicos", "com/mycompany/gestorui/views/Reportes/ListadoMedicos.fxml");
        RUTAS_REPORTES.put("resumenHospital", "com/mycompany/gestorui/views/Reportes/ResumenHospital.fxml");
        RUTAS_REPORTES.put("topHospitales", "com/mycompany/gestorui/views/Reportes/TopHospitales.fxml");
        RUTAS_REPORTES.put("informesConsultas", "com/mycompany/gestorui/views/Reportes/InformesConsultas.fxml");
        RUTAS_REPORTES.put("noAtendidos", "com/mycompany/gestorui/views/Reportes/NoAtendidos.fxml");
        RUTAS_REPORTES.put("resumenProceso", "com/mycompany/gestorui/views/Reportes/ResumenProceso.fxml");
        RUTAS_REPORTES.put("revisarTurnos", "com/mycompany/gestorui/views/Reportes/RevisarTurnos.fxml");
        RUTAS_REPORTES.put("consultasExitosas", "com/mycompany/gestorui/views/Reportes/ConsultasExitosas.fxml");
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        showView("menu");
    }
    
    private void loadView(String key) {
        String fxmlPath = RUTAS_REPORTES.get(key);
        if (fxmlPath == null) {
            showErrorView("Ruta no definida para: " + key);
            return;
        }
        
        try {
            // Intentar cargar con getResource (ruta absoluta con /)
            URL resource = getClass().getResource(fxmlPath);
            System.out.println("Intentando cargar: " + fxmlPath + " -> " + resource);
            
            // Si no funciona, intentar con ClassLoader (sin / inicial)
            if (resource == null) {
                String pathSinBarra = fxmlPath.startsWith("/") ? fxmlPath.substring(1) : fxmlPath;
                resource = getClass().getClassLoader().getResource(pathSinBarra);
                System.out.println("Intentando con ClassLoader: " + pathSinBarra + " -> " + resource);
            }
            
            if (resource == null) {
                showErrorView("No se encontró el recurso: " + fxmlPath);
                return;
            }
            
            Parent view = FXMLLoader.load(resource);
            
            // Reemplazar vista actual (excepto menú)
            if (currentView != null && currentView != menuPane) {
                contentPane.getChildren().remove(currentView);
            }
            
            contentPane.getChildren().add(view);
            currentView = view;
            
            // Hacer que la vista ocupe todo el espacio disponible en el StackPane
            StackPane.setAlignment(view, javafx.geometry.Pos.CENTER);
            ((Region) view).setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            
            System.out.println("✅ Vista cargada: " + key);
            showView(key);
            
        } catch (Exception ex) {
            System.err.println("❌ Error cargando: " + fxmlPath);
            ex.printStackTrace();
            showErrorView("Error al cargar: " + fxmlPath + "\n" + ex.getMessage());
        }
    }
    
    
    
    private void showErrorView(String mensaje) {
        Label errorLabel = new Label("❌ " + mensaje);
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 16px; -fx-padding: 20; -fx-wrap-text: true;");
        errorLabel.setWrapText(true);
        if (currentView != null && currentView != menuPane) {
            contentPane.getChildren().remove(currentView);
        }
        contentPane.getChildren().add(errorLabel);
        currentView = errorLabel;
        StackPane.setAlignment(errorLabel, javafx.geometry.Pos.CENTER);
        errorLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        showView("error");
    }

    private void showView(String key) {
        boolean menuVisible = "menu".equals(key);
        
        if (menuPane != null) {
            menuPane.setVisible(menuVisible);
            menuPane.setManaged(menuVisible);
            if (menuVisible) menuPane.toFront();
        }
        if (btnBack != null) {
            btnBack.setVisible(!menuVisible);
            btnBack.setManaged(!menuVisible);
        }
        if (lblTituloReporte != null) {
            lblTituloReporte.setVisible(!menuVisible);
            lblTituloReporte.setManaged(!menuVisible);
            if (!menuVisible) {
                lblTituloReporte.setText(TITULOS_REPORTES.getOrDefault(key, "Reporte"));
            }
        }
        
        if ("error".equals(key) && currentView != null) {
            currentView.setVisible(true);
            currentView.setManaged(true);
            currentView.toFront();
        }
        if (!menuVisible && !"error".equals(key) && currentView != null && currentView != menuPane) {
            currentView.setVisible(true);
            currentView.setManaged(true);
            currentView.toFront();
        }
    }

    @FXML
    private void handleBack(ActionEvent event) {
        showView("menu");
        if (currentView != null && currentView != menuPane) {
            contentPane.getChildren().remove(currentView);
            currentView = null;
        }
    }
    
    @FXML
    private void handleVolverMenu(ActionEvent event) {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML  
    private void handleListadoPacientes(MouseEvent event){ loadView("listadoPacientes"); }
    @FXML
    private void handleListadoMedicos(MouseEvent event){ loadView("listadoMedicos"); }
    @FXML
    private void handleResumenHospital(MouseEvent event){ loadView("resumenHospital"); }
    @FXML
    private void handleTop(MouseEvent event){ loadView("topHospitales"); }
    @FXML
    private void handleInformesConsultas(MouseEvent event){ loadView("informesConsultas"); }
    @FXML
    private void handleNOAtendidos(MouseEvent event){ loadView("noAtendidos"); }
    @FXML
    private void handleResumenProceso(MouseEvent event){ loadView("resumenProceso"); }
    @FXML
    private void handleRevisarTurno(MouseEvent event){ loadView("revisarTurnos"); }
    @FXML
    private void handleConsultasExitosas(MouseEvent event){ loadView("consultasExitosas"); }
    
    @FXML
    private void handleMinimize(MouseEvent event) {
        Stage stage = (Stage) ((Label) event.getSource()).getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    private void handleClose(MouseEvent event) {
        Stage stage = (Stage) cerrar.getScene().getWindow();
        stage.close();
    }
}

