/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.gestorui.controllers.Reporte;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 *
 * @author ignacio
 */
public class ReporteController implements Initializable{
    
    @FXML
    private Label cerrar;
    
    @FXML
    private Label minimizar;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }
    
    @FXML  
    private void handleListadoPacientes(MouseEvent event){
        System.out.println("Aqui-1");
    }
    
    @FXML
    private void handleListadoMedicos(MouseEvent event){
        System.out.println("Aqui-2");
    }
    
    @FXML
    private void handleResumenHospital(MouseEvent event){
        System.out.println("Aqui-3");
    }
    
    @FXML
    private void handleTop(MouseEvent event){
        System.out.println("Aqui-4");
    }
    
    @FXML
    private void handleInformesConsultas(MouseEvent event){
        System.out.println("Aqui-5");
    }
    
    @FXML
    private void handleNOAtendidos(MouseEvent event){
        System.out.println("Aqui-6");
    }
    
    @FXML
    private void handleResumenProceso(MouseEvent event){
        System.out.println("Aqui-7");
    }
    
    @FXML
    private void handleRevisarTurno(MouseEvent event){
        System.out.println("Aqui-8");
    }
    
    @FXML
    private void handleConsultasExitosas(MouseEvent event){
        System.out.println("Aqui-9");
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
