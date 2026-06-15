package com.mycompany.gestorui.controllers;

import java.net.URL;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ResourceBundle;

import com.mycompany.gestorui.components.Cuenta;
import com.mycompany.gestorui.components.Gestion;
import com.mycompany.gestorui.components.Login;
import com.mycompany.gestorui.components.MainWindow;
import com.mycompany.gestorui.components.Reporte;
import com.mycompany.gestorui.model.entidades.Departamento;
import com.mycompany.gestorui.model.entidades.Hospital;
import com.mycompany.gestorui.model.entidades.Medico;
import com.mycompany.gestorui.model.entidades.Unidad;
import com.mycompany.gestorui.model.services.reportes.HospitalService;
import com.mycompany.gestorui.model.services.reportes.UnidadService;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PopupControl;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainWindowController implements Initializable {

    @FXML
    private Label lblUsuario;

    @FXML
    private Label lblTotalHospitales;

    @FXML
    private Label lblTotalDepartamentos;

    @FXML
    private Label lblTotalUnidades;

    @FXML
    private Label lblTotalMedicos;

    @FXML
    private Label lblTotalPacientes;

    @FXML
    private BarChart<String, Number> chartTopHospitales;

    @FXML
    private VBox vboxAlertas;

    @FXML
    private Button btnMenu;

    private PopupControl popupMenu;
    private VBox menuContent;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        String usuario = LoginController.getUsuarioActual();
        if (usuario != null && !usuario.isEmpty()) {
            lblUsuario.setText("👤 " + usuario);
        } else {
            lblUsuario.setText("👤 Usuario");
        }

        try {
            cargarIndicadores();
            cargarGraficoTopHospitales();
            cargarAlertas();
        } catch (SQLException ex) {
            System.getLogger(MainWindowController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
        crearMenuPersonalizado();
    }

    private Button crearItemMenu(String texto, javafx.event.EventHandler<ActionEvent> accion) {
        Button item = new Button(texto);
        item.getStyleClass().add("menu-item-custom");
        item.setMaxWidth(Double.MAX_VALUE);
        item.setOnAction(e -> {
            accion.handle(e);
            if (popupMenu != null) {
                popupMenu.hide();
            }
        });
        return item;
    }

    private void crearMenuPersonalizado() {
        // Crear el contenido del menú
        menuContent = new VBox();
        menuContent.getStyleClass().add("menu-popup");

        String cssUrl = getClass().getResource("/com/mycompany/gestorui/styles/menu.css").toExternalForm();
        menuContent.getStylesheets().add(cssUrl);

        // Crear los items del menú - Ahora usando referencia de método
        Button itemGestion = crearItemMenu("👤Gestión", this::mostrarGestion);
        Button itemReportes = crearItemMenu("Reportes", this::mostrarReportes);
        Button itemCuenta = crearItemMenu("Mi cuenta", this::mostrarCuenta);
        Button itemCerrar = crearItemMenu("🚪Cerrar Sesión", this::cerrarSesion);

        menuContent.getChildren().addAll(itemGestion, itemReportes, itemCuenta, itemCerrar);

        // Crear el popup
        popupMenu = new PopupControl();
        popupMenu.getScene().setRoot(menuContent);
        popupMenu.setAutoHide(true);  // Se cierra al hacer clic fuera
    }

    @FXML
    private void mostrarMenu(ActionEvent event) {
        if (popupMenu == null) {
            crearMenuPersonalizado();
        }

        // Calcular posición debajo del botón
        Point2D point = btnMenu.localToScreen(0, btnMenu.getHeight());
        double x = point.getX();
        double y = point.getY();

        // Asegurar que el menú no se salga de la ventana
        Stage stage = (Stage) btnMenu.getScene().getWindow();
        double menuWidth = popupMenu.getWidth() > 0 ? popupMenu.getWidth() : 180;
        double windowRightEdge = stage.getX() + stage.getWidth();

        if (x + menuWidth > windowRightEdge) {
            x = windowRightEdge - menuWidth - 10;
        }

        // Mostrar el menú
        popupMenu.show(btnMenu, x, y);
    }

    // Métodos del menú con ActionEvent como parámetro
    @FXML
    private void mostrarGestion(ActionEvent event) {
        try {
            Gestion.mostrarVentanaGestion();
        } catch (Exception ex) {
            System.getLogger(MainWindowController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }

    @FXML
    private void mostrarReportes(ActionEvent event) {
        try {
            Reporte.mostrarVentanaReportes();
        } catch (Exception ex) {
            System.getLogger(MainWindowController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }

    @FXML
    private void mostrarCuenta(ActionEvent event) {
        try {
            Cuenta.mostrarVentanaCuenta();
        } catch (Exception ex) {
            System.getLogger(MainWindowController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }

    @FXML
    private void cerrarSesion(ActionEvent event) {
        try {
            // Cerrar la ventana actual (MainWindow)
            Stage stageActual = (Stage) btnMenu.getScene().getWindow();
            stageActual.close();

            Login.mostrarVentanaLogin();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Indicadores con la cantidad de hos,dep,uni,med y pac
    private void cargarIndicadores() throws SQLException {
        HashMap<String, Integer> indicadores = new HashMap<>(MainWindow.cantIndicadores());

        //Mostrar indicadores obtenidos
        lblTotalHospitales.setText(Integer.toString(indicadores.get("hospitales")));
        lblTotalDepartamentos.setText(Integer.toString(indicadores.get("departamentos")));
        lblTotalUnidades.setText(Integer.toString(indicadores.get("unidades")));
        lblTotalMedicos.setText(Integer.toString(indicadores.get("medicos")));
        lblTotalPacientes.setText(Integer.toString(indicadores.get("pacientes")));

    }

    //Grafico para el top 5 de hospitales con mas de 100 pacientes
    private void cargarGraficoTopHospitales() throws SQLException {
        HospitalService hs = new HospitalService();
        LinkedList<Hospital> listado = new LinkedList<>(hs.hospitalesMayorCantidadPacientes());

        chartTopHospitales.getData().clear();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Pacientes");

        for (Hospital h : listado) {
            series.getData().add(new XYChart.Data<>(h.getNombreHos(), h.getCantPac()));
        }

        chartTopHospitales.getData().add(series);
    }

    private void cargarAlertas() throws SQLException {
        vboxAlertas.getChildren().clear();

        String unidad;
        String departamento;
        String hospital;
        String medico;

        UnidadService us = new UnidadService();
        LinkedList<Hospital> listado = new LinkedList<>(us.listadoUnidades());

        for (Hospital h : listado) {
            hospital = h.getNombreHos();
            //System.out.println("Hospital: "+hospital);

            for (Departamento d : h.getDepartamentos()) {
                departamento = d.getNombreDep();
                //System.out.println("Depa: "+departamento);

                for (Unidad u : d.getUnidades()) {
                    unidad = u.getNombreUni();
                    //System.out.println("Uni: "+unidad);

                    for (Medico m : u.getMedicos()) {
                        medico = m.getNombreMed();
                        //System.out.println("Med: "+medico);

                        agregarAlerta(unidad, departamento, hospital, medico);
                    }
                }
            }
        }

    }

    private void agregarAlerta(String unidad, String departamento, String hospital, String medico) {
        VBox alertaBox = new VBox(5);
        alertaBox.setStyle("-fx-background-color: #fff3cd; -fx-background-radius: 5; -fx-border-color: #ffc107; -fx-border-radius: 5; -fx-border-width: 1;");
        alertaBox.setPadding(new Insets(10));

        Label lblUnidad = new Label("• " + unidad + "(" + departamento + ")");
        lblUnidad.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Label lblHospital = new Label("Hospital: " + hospital);
        lblHospital.setStyle("-fx-font-size: 12px; -fx-text-fill: #666666;");

        Label lblMedico = new Label("Medico: " + medico);
        lblMedico.setStyle("-fx-font-size: 11px; -fx-text-fill: #666666;");

        Button btnRevisar = new Button("Revisar");
        btnRevisar.setStyle("-fx-background-color: #2d7a2d; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand;");
        btnRevisar.setOnAction(e -> handleRevisarUnidad(unidad, hospital, departamento, medico));

        alertaBox.getChildren().addAll(lblUnidad, lblHospital, lblMedico, btnRevisar);
        vboxAlertas.getChildren().add(alertaBox);
    }

    private void handleRevisarUnidad(String unidad, String hospital, String departamento, String medico) {
        System.out.println("Revisar: " + unidad + " - " + hospital + " - " + departamento + " - " + medico);
    }
}
