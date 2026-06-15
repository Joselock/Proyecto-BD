package com.mycompany.gestorui.components;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;

import com.mycompany.gestorui.model.entidades.Hospital;
import com.mycompany.gestorui.model.services.reportes.HospitalService;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainWindow {
    private static Stage mainStage;
    
    public static void mostrarVentanaPrincipal() {
        try {
            System.out.println("=== INICIANDO CARGA DE MAINWINDOW ===");
            
            // Cargar el FXML
            FXMLLoader loader = new FXMLLoader(MainWindow.class.getResource("/com/mycompany/gestorui/views/MainWindowFXML.fxml"));
            System.out.println("Loader creado, cargando FXML...");
            
            Parent root = loader.load();
            System.out.println("FXML cargado correctamente");
            
            Scene scene = new Scene(root);
            System.out.println("Scene creada");
            
            mainStage = new Stage();
            mainStage.setScene(scene);
            mainStage.setTitle("Ventana Principal");
            mainStage.show();
            
           
            
        } catch (Exception e) {
            System.err.println("=== ERROR AL CARGAR MAINWINDOW ===");
            e.printStackTrace();
        }
    }
    
    
    public static HashMap<String,Integer> cantIndicadores() throws SQLException{
        HashMap<String,Integer>indicadoresMap = new HashMap<>();
        int cantDep = 0;
        int cantUni = 0;
        int cantMed = 0;
        int cantPac = 0;
        
        HospitalService hs = new HospitalService();
        LinkedList<Hospital>resumen = new LinkedList<>(hs.resumenHospitales());
        
        indicadoresMap.put("hospitales", resumen.size());
        
        for(Hospital h: resumen){
            cantDep+= h.getCantDep();
            cantUni+= h.getCantUni();
            cantMed+= h.getCantMed();
            cantPac+= h.getCantPac();
        }
        
        indicadoresMap.put("departamentos", cantDep);
        indicadoresMap.put("unidades", cantUni);
        indicadoresMap.put("medicos", cantMed);
        indicadoresMap.put("pacientes", cantPac);
        
        
        return indicadoresMap;
    }
    
    
}