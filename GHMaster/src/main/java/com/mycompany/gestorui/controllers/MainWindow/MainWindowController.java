package com.mycompany.gestorui.controllers.MainWindow;

import java.io.File;
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
import com.mycompany.gestorui.model.entidades.Informe;
import com.mycompany.gestorui.model.entidades.Medico;
import com.mycompany.gestorui.model.entidades.Unidad;
import com.mycompany.gestorui.model.services.reportes.HospitalService;
import com.mycompany.gestorui.controllers.LoginController;
import com.mycompany.gestorui.controllers.Cuenta.Listener.PerfilListener;
import com.mycompany.gestorui.controllers.MainWindow.Manager.DatosCambiadosManager;
import com.mycompany.gestorui.controllers.Reporte.Listener.TurnosRevisadosListener;
import com.mycompany.gestorui.controllers.Reporte.Manager.TurnosRevisadosManager;
import com.mycompany.gestorui.model.services.reportes.UnidadService;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PopupControl;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MainWindowController implements Initializable, TurnosRevisadosListener,
        com.mycompany.gestorui.controllers.MainWindow.Listener.DatosCambiadosListener {

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

    @FXML
    private ImageView imgAvatar; // Añadir este ImageView en el FXML

    @FXML
    private HBox hboxUsuario; // Contenedor para usuario y avatar

    private PopupControl popupMenu;
    private VBox menuContent;
    private TurnosRevisadosManager manager = TurnosRevisadosManager.getInstance();
    private DatosCambiadosManager datosManager = DatosCambiadosManager.getInstance();

    private int totalHospitales = 0;
    private int totalDepartamentos = 0;
    private int totalUnidades = 0;
    private int totalMedicos = 0;
    private int totalPacientes = 0;

    private LinkedList<Hospital> datosHospitales = new LinkedList<>();
    private boolean animacionGraficoIniciada = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Registrar listeners
        manager.agregarListener(this);
        datosManager.agregarListener(this);

        // Registrar listener para cambios de perfil
        PerfilListener.agregarListener(this::actualizarDatosUsuario);

        // Configurar avatar
        configurarAvatar();
        actualizarDatosUsuario();

        try {
            cargarIndicadores();
            cargarDatosGrafico();
            cargarAlertas();
            iniciarAnimaciones();
        } catch (SQLException ex) {
            System.getLogger(MainWindowController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
        crearMenuPersonalizado();
    }

    private void configurarAvatar() {
        if (imgAvatar != null) {
            imgAvatar.setFitWidth(35);
            imgAvatar.setFitHeight(35);
            imgAvatar.setPreserveRatio(true);
            // Hacerlo circular (opcional)
            imgAvatar.setClip(new javafx.scene.shape.Circle(17.5, 17.5, 17.5));
        }
    }

    /**
     * Actualiza los datos del usuario (nombre y avatar)
     */
    private void actualizarDatosUsuario() {
        Platform.runLater(() -> {
            String usuario = LoginController.getUsuarioActual();
            if (usuario != null && !usuario.isEmpty()) {
                lblUsuario.setText("👤 " + usuario);
            } else {
                lblUsuario.setText("👤 Usuario");
            }

            // Cargar avatar
            cargarAvatar();
        });
    }

    /**
     * Carga el avatar del usuario desde el sistema de archivos
     */
    private void cargarAvatar() {
        if (imgAvatar == null)
            return;

        String usuario = LoginController.getUsuarioActual();
        if (usuario == null || usuario.isEmpty()) {
            imgAvatar.setImage(null);
            return;
        }

        String avatarPath = System.getProperty("user.home") + "/.gestorui/avatars/avatar_" + usuario + ".png";
        File avatarFile = new File(avatarPath);

        if (avatarFile.exists()) {
            try {
                Image image = new Image(avatarFile.toURI().toString(), 35, 35, false, true);
                imgAvatar.setImage(image);
            } catch (Exception e) {
                System.err.println("Error al cargar avatar en MainWindow: " + e.getMessage());
                imgAvatar.setImage(null);
            }
        } else {
            imgAvatar.setImage(null);
        }
    }

    @Override
    public void onTurnoRevisado(String hospital, String departamento, String unidad, String medico) {
        Platform.runLater(() -> {
            System.out.println("🔔 Notificación: Turno revisado - " + hospital + " | " + departamento + " | " + unidad
                    + " | " + medico);
            try {
                cargarAlertas();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }

    @Override
    public void onDatosCambiados() {
        // Este método se ejecuta cuando se agrega, modifica o elimina un dato
        Platform.runLater(() -> {
            System.out.println("🔄 Datos cambiados - Recargando indicadores...");
            try {
                // Recargar indicadores
                cargarIndicadores();
                cargarDatosGrafico();
                cargarAlertas();
                reiniciarAnimaciones();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
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
        menuContent = new VBox();
        menuContent.getStyleClass().add("menu-popup");

        String cssUrl = getClass().getResource("/com/mycompany/gestorui/styles/menu.css").toExternalForm();
        menuContent.getStylesheets().add(cssUrl);

        Button itemGestion = crearItemMenu("Gestión", this::mostrarGestion);
        Button itemReportes = crearItemMenu("Reportes", this::mostrarReportes);
        Button itemCuenta = crearItemMenu("Mi cuenta", this::mostrarCuenta);
        Button itemCerrar = crearItemMenu("Cerrar Sesión", this::cerrarSesion);

        menuContent.getChildren().addAll(itemGestion, itemReportes, itemCuenta, itemCerrar);

        popupMenu = new PopupControl();
        popupMenu.getScene().setRoot(menuContent);
        popupMenu.setAutoHide(true);
    }

    @FXML
    private void mostrarMenu(ActionEvent event) {
        if (popupMenu == null) {
            crearMenuPersonalizado();
        }

        Point2D point = btnMenu.localToScreen(0, btnMenu.getHeight());
        double x = point.getX();
        double y = point.getY();

        Stage stage = (Stage) btnMenu.getScene().getWindow();
        double menuWidth = popupMenu.getWidth() > 0 ? popupMenu.getWidth() : 180;
        double windowRightEdge = stage.getX() + stage.getWidth();

        if (x + menuWidth > windowRightEdge) {
            x = windowRightEdge - menuWidth - 10;
        }

        popupMenu.show(btnMenu, x, y);
    }

    @FXML
    private void mostrarGestion(ActionEvent event) {
        try {
            // Cuando se abra la ventana de gestión, registrar un listener para cuando se
            // cierre
            Stage stage = new Stage();
            Gestion.mostrarVentanaGestion();

            // Notificar cambio después de cerrar la ventana de gestión
            // (la notificación se hará desde GestionController al guardar/eliminar)
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
            Stage stageActual = (Stage) btnMenu.getScene().getWindow();
            stageActual.close();
            Login.mostrarVentanaLogin();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cargarIndicadores() throws SQLException {
        HashMap<String, Integer> indicadores = new HashMap<>(MainWindow.cantIndicadores());

        totalHospitales = indicadores.get("hospitales");
        totalDepartamentos = indicadores.get("departamentos");
        totalUnidades = indicadores.get("unidades");
        totalMedicos = indicadores.get("medicos");
        totalPacientes = indicadores.get("pacientes");

        // Actualizar labels directamente
        lblTotalHospitales.setText(String.valueOf(totalHospitales));
        lblTotalDepartamentos.setText(String.valueOf(totalDepartamentos));
        lblTotalUnidades.setText(String.valueOf(totalUnidades));
        lblTotalMedicos.setText(String.valueOf(totalMedicos));
        lblTotalPacientes.setText(String.valueOf(totalPacientes));
    }

    private void cargarDatosGrafico() throws SQLException {
        HospitalService hs = new HospitalService();
        datosHospitales = new LinkedList<>(hs.hospitalesMayorCantidadPacientes());

        chartTopHospitales.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Pacientes");

        for (Hospital h : datosHospitales) {
            series.getData().add(new XYChart.Data<>(h.getNombreHos(), h.getCantPac()));
        }
        chartTopHospitales.getData().add(series);

        // Reiniciar animación del gráfico
        animacionGraficoIniciada = false;
    }

    private void animarGrafico() {
        if (datosHospitales == null || datosHospitales.isEmpty()) {
            return;
        }

        if (animacionGraficoIniciada) {
            return;
        }
        animacionGraficoIniciada = true;

        XYChart.Series<String, Number> series = chartTopHospitales.getData().get(0);
        Duration duracionTotal = Duration.millis(1200);
        int pasos = 30;

        Timeline timeline = new Timeline();

        for (int paso = 0; paso <= pasos; paso++) {
            double progreso = (double) paso / pasos;
            double valorEasing = 1 - Math.pow(1 - progreso, 1.8);
            Duration tiempo = duracionTotal.multiply(progreso);

            final double valorEasingFinal = valorEasing;
            final int pasoFinal = paso;

            KeyFrame keyFrame = new KeyFrame(
                    tiempo,
                    event -> {
                        for (int j = 0; j < series.getData().size() && j < datosHospitales.size(); j++) {
                            XYChart.Data<String, Number> data = series.getData().get(j);
                            Hospital hospital = datosHospitales.get(j);
                            int valorReal = hospital.getCantPac();
                            int valorActual = (int) (valorReal * valorEasingFinal);

                            if (pasoFinal == pasos) {
                                valorActual = valorReal;
                            }

                            data.setYValue(valorActual);
                        }
                    });
            timeline.getKeyFrames().add(keyFrame);
        }

        KeyFrame frameFinal = new KeyFrame(
                duracionTotal,
                event -> {
                    for (int j = 0; j < series.getData().size() && j < datosHospitales.size(); j++) {
                        XYChart.Data<String, Number> data = series.getData().get(j);
                        Hospital hospital = datosHospitales.get(j);
                        data.setYValue(hospital.getCantPac());
                    }
                });
        timeline.getKeyFrames().add(frameFinal);

        timeline.play();
    }

    private void iniciarAnimaciones() {
        animarNumero(lblTotalHospitales, totalHospitales, 0);
        animarNumero(lblTotalDepartamentos, totalDepartamentos, 300);
        animarNumero(lblTotalUnidades, totalUnidades, 600);
        animarNumero(lblTotalMedicos, totalMedicos, 900);
        animarNumero(lblTotalPacientes, totalPacientes, 1200);

        Timeline delay = new Timeline(
                new KeyFrame(Duration.millis(1500), e -> animarGrafico()));
        delay.play();
    }

    private void reiniciarAnimaciones() {
        // Reiniciar la animación del gráfico
        animacionGraficoIniciada = false;

        // Volver a animar los números
        lblTotalHospitales.setText("0");
        lblTotalDepartamentos.setText("0");
        lblTotalUnidades.setText("0");
        lblTotalMedicos.setText("0");
        lblTotalPacientes.setText("0");

        animarNumero(lblTotalHospitales, totalHospitales, 0);
        animarNumero(lblTotalDepartamentos, totalDepartamentos, 200);
        animarNumero(lblTotalUnidades, totalUnidades, 400);
        animarNumero(lblTotalMedicos, totalMedicos, 600);
        animarNumero(lblTotalPacientes, totalPacientes, 800);

        Timeline delay = new Timeline(
                new KeyFrame(Duration.millis(1000), e -> animarGrafico()));
        delay.play();
    }

    private void animarNumero(Label label, int valorFinal, int delayInicio) {
        if (valorFinal <= 0) {
            label.setText("0");
            return;
        }

        Timeline timeline = new Timeline();

        Duration duracion;
        if (valorFinal < 10) {
            duracion = Duration.millis(500);
        } else if (valorFinal < 100) {
            duracion = Duration.millis(800);
        } else {
            duracion = Duration.millis(1000);
        }

        int pasos = Math.min(valorFinal, 50);

        for (int paso = 0; paso <= pasos; paso++) {
            double progreso = (double) paso / pasos;
            double valorEasing = 1 - Math.pow(1 - progreso, 1.5);
            int valorEnPaso = (int) (valorFinal * valorEasing);

            if (paso == pasos) {
                valorEnPaso = valorFinal;
            }

            final int valorMostrar = valorEnPaso;
            Duration tiempo = duracion.multiply(progreso);

            KeyFrame keyFrame = new KeyFrame(
                    tiempo,
                    event -> label.setText(String.valueOf(valorMostrar)));
            timeline.getKeyFrames().add(keyFrame);
        }

        KeyFrame frameFinal = new KeyFrame(
                duracion,
                event -> label.setText(String.valueOf(valorFinal)));
        timeline.getKeyFrames().add(frameFinal);

        timeline.setDelay(Duration.millis(delayInicio));
        timeline.play();
    }

    private void cargarAlertas() throws SQLException {
        vboxAlertas.getChildren().clear();

        UnidadService us = new UnidadService();
        LinkedList<Hospital> listado = new LinkedList<>(us.listadoUnidades());
        int alertasCount = 0;

        for (Hospital h : listado) {
            String hospital = h.getNombreHos();

            for (Departamento d : h.getDepartamentos()) {
                String departamento = d.getNombreDep();

                for (Unidad u : d.getUnidades()) {
                    String unidad = u.getNombreUni();

                    for (Medico m : u.getMedicos()) {
                        String medico = m.getNombreMed();

                        if (!manager.esRevisado(hospital, departamento, unidad, medico)) {
                            agregarAlerta(unidad, departamento, hospital, medico);
                            alertasCount++;
                        }
                    }
                }
            }
        }

        if (alertasCount == 0) {
            Label noAlertas = new Label("✅ No hay turnos pendientes de revisar");
            noAlertas.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 10;");
            vboxAlertas.getChildren().add(noAlertas);
        }
    }

    private void agregarAlerta(String unidad, String departamento, String hospital, String medico) {
        VBox alertaBox = new VBox(5);
        alertaBox.setStyle(
                "-fx-background-color: #fff3cd; -fx-background-radius: 5; -fx-border-color: #ffc107; -fx-border-radius: 5; -fx-border-width: 1;");
        alertaBox.setPadding(new Insets(10));

        Label lblUnidad = new Label("• " + unidad + " (" + departamento + ")");
        lblUnidad.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Label lblHospital = new Label("Hospital: " + hospital);
        lblHospital.setStyle("-fx-font-size: 12px; -fx-text-fill: #666666;");

        Label lblMedico = new Label("Médico: " + medico);
        lblMedico.setStyle("-fx-font-size: 11px; -fx-text-fill: #666666;");

        Button btnRevisar = new Button("🔍 Revisar");
        btnRevisar.setStyle(
                "-fx-background-color: #2d7a2d; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand;");
        btnRevisar.setOnAction(e -> handleRevisarUnidad(unidad, hospital, departamento, medico, alertaBox));

        alertaBox.getChildren().addAll(lblUnidad, lblHospital, lblMedico, btnRevisar);
        vboxAlertas.getChildren().add(alertaBox);
    }

    private void handleRevisarUnidad(String unidad, String hospital, String departamento, String medico,
            VBox alertaBox) {
        try {
            // Buscar los datos adicionales del turno
            DatosTurno datos = obtenerDatosTurno(hospital, departamento, unidad, medico);

            Stage ventana = new Stage();
            ventana.initModality(Modality.APPLICATION_MODAL);
            ventana.setTitle("Detalle del Turno - Revisión");
            ventana.setWidth(500);
            ventana.setHeight(550);
            ventana.setResizable(false);

            VBox content = new VBox(15);
            content.setPadding(new Insets(25));
            content.setStyle("-fx-background-color: #f5f5f5;");

            Label titulo = new Label("Detalle del Turno");
            titulo.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

            VBox infoBox = new VBox(8);
            infoBox.setStyle(
                    "-fx-background-color: white; -fx-padding: 15; -fx-background-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 2);");

            Label lblHospital = new Label("Hospital: " + hospital);
            Label lblDepartamento = new Label("Departamento: " + departamento);
            Label lblUnidad = new Label("Unidad: " + unidad);
            Label lblMedico = new Label("Médico: " + medico);
            Label lblTotalPac = new Label("Total Pacientes: " + datos.totalPacientes);
            Label lblPacAtend = new Label("Pacientes Atendidos: " + datos.pacAtendidos);
            Label lblPorcentaje = new Label("Porcentaje Atención: " + datos.porcentaje);
            Label lblEstado = new Label("Estado Actual: " + datos.estado);

            String labelStyle = "-fx-font-size: 14px;";
            lblHospital.setStyle(labelStyle);
            lblDepartamento.setStyle(labelStyle);
            lblUnidad.setStyle(labelStyle);
            lblMedico.setStyle(labelStyle);
            lblTotalPac.setStyle(labelStyle);
            lblPacAtend.setStyle(labelStyle);
            lblPorcentaje.setStyle(labelStyle);
            lblEstado.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #e74c3c;");

            infoBox.getChildren().addAll(lblHospital, lblDepartamento, lblUnidad, lblMedico,
                    lblTotalPac, lblPacAtend, lblPorcentaje, lblEstado);

            Button btnRevisar = new Button("✅ Marcar como Revisado");
            btnRevisar.setStyle(
                    "-fx-background-color: #27ae60; " +
                            "-fx-text-fill: white; " +
                            "-fx-font-size: 14px; " +
                            "-fx-font-weight: bold; " +
                            "-fx-padding: 10 20; " +
                            "-fx-background-radius: 5; " +
                            "-fx-cursor: hand;");
            btnRevisar.setMaxWidth(Double.MAX_VALUE);

            // Si ya está revisado, deshabilitar
            if (datos.estado.contains("Revisado") || datos.estado.contains("Extioso") || datos.estado.contains("OK")) {
                btnRevisar.setText("✅ Ya revisado");
                btnRevisar.setStyle(
                        "-fx-background-color: #95a5a6; " +
                                "-fx-text-fill: white; " +
                                "-fx-font-size: 14px; " +
                                "-fx-font-weight: bold; " +
                                "-fx-padding: 10 20; " +
                                "-fx-background-radius: 5;");
                btnRevisar.setDisable(true);
            }

            btnRevisar.setOnAction(e -> {
                manager.marcarComoRevisado(hospital, departamento, unidad, medico);
                ventana.close();
            });

            Button btnCerrar = new Button("✖ Cerrar");
            btnCerrar.setStyle(
                    "-fx-background-color: #e74c3c; " +
                            "-fx-text-fill: white; " +
                            "-fx-font-size: 14px; " +
                            "-fx-font-weight: bold; " +
                            "-fx-padding: 10 20; " +
                            "-fx-background-radius: 5; " +
                            "-fx-cursor: hand;");
            btnCerrar.setMaxWidth(Double.MAX_VALUE);
            btnCerrar.setOnAction(e -> ventana.close());

            content.getChildren().addAll(titulo, infoBox, btnRevisar, btnCerrar);

            Scene scene = new Scene(content);
            ventana.setScene(scene);
            ventana.showAndWait();

        } catch (Exception ex) {
            ex.printStackTrace();
            Label errorLabel = new Label("❌ Error al abrir detalle: " + ex.getMessage());
            errorLabel.setStyle("-fx-text-fill: red; -fx-padding: 10;");
            if (vboxAlertas != null) {
                vboxAlertas.getChildren().add(errorLabel);
            }
        }
    }

    /**
     * Obtiene los datos del turno desde la base de datos
     */
    private DatosTurno obtenerDatosTurno(String hospital, String departamento, String unidad, String medico) {
        DatosTurno datos = new DatosTurno();

        try {
            UnidadService us = new UnidadService();
            LinkedList<Hospital> hospitales = us.listadoUnidades();

            for (Hospital h : hospitales) {
                if (!h.getNombreHos().equals(hospital))
                    continue;

                for (Departamento d : h.getDepartamentos()) {
                    if (!d.getNombreDep().equals(departamento))
                        continue;

                    for (Unidad u : d.getUnidades()) {
                        if (!u.getNombreUni().equals(unidad))
                            continue;

                        for (Medico m : u.getMedicos()) {
                            if (!m.getNombreMed().equals(medico))
                                continue;

                            // Encontrar el informe correspondiente
                            int size = Math.min(u.getInformes().size(), u.getMedicos().size());
                            for (int i = 0; i < size; i++) {
                                Informe informe = u.getInformes().get(i);
                                Medico med = u.getMedicos().get(i);
                                if (med.getNombreMed().equals(medico) && informe != null) {
                                    datos.totalPacientes = String.valueOf(informe.getTotal());
                                    datos.pacAtendidos = String.valueOf(informe.getPacAtend());
                                    float porcentaje = informe.getPorcentajePacAtend();
                                    datos.porcentaje = String.format("%.1f%%", porcentaje);
                                    datos.estado = porcentaje >= 80 ? "🟢 OK" : "🔴 Revisar";
                                    return datos;
                                }
                            }
                        }
                    }
                }
            }

            // Si no se encontraron datos
            datos.totalPacientes = "0";
            datos.pacAtendidos = "0";
            datos.porcentaje = "0%";
            datos.estado = "🔴 Revisar";

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return datos;
    }

    // Clase interna para almacenar los datos del turno
    private static class DatosTurno {
        String totalPacientes = "0";
        String pacAtendidos = "0";
        String porcentaje = "0%";
        String estado = "🔴 Revisar";
    }
}