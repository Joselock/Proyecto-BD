
package com.mycompany.gestorui.components;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;

import com.mycompany.gestorui.model.entidades.Hospital;
import com.mycompany.gestorui.model.services.reportes.HospitalService;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MainWindow {
    
    private static Stage stagePrincipal;
    
    public static void mostrarVentanaPrincipal() {
        try {
            if (stagePrincipal != null && stagePrincipal.isShowing()) {
                // Si ya está abierta, traer al frente
                stagePrincipal.toFront();
                return;
            }
            
            Parent root = FXMLLoader.load(MainWindow.class.getResource("/com/mycompany/gestorui/views/MainWindowFXML.fxml"));
            Stage stage = new Stage();
            stage.initStyle(StageStyle.DECORATED);
            stage.setScene(new Scene(root));
            stage.setTitle("Sistema de Gestión Hospitalaria");
            stage.setMaximized(true);
            
            // Guardar referencia
            stagePrincipal = stage;
            stage.show();
            
            System.out.println("✅ MainWindow abierta");
            
        } catch (IOException e) {
            System.err.println("❌ Error al abrir MainWindow: " + e.getMessage());
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

    
    public static Stage getStagePrincipal() {
        return stagePrincipal;
    }
    
    public static void cerrarVentanaPrincipal() {
        if (stagePrincipal != null) {
            stagePrincipal.close();
            stagePrincipal = null;
        }
    }
}