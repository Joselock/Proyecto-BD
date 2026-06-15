/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.gestorui.components;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


/**
 *
 * @author ignacio
 */
public class Login{
    private static Stage mainStage;
    
    public static void mostrarVentanaLogin() throws Exception {
        // Crear un nuevo Stage
        mainStage = new Stage();
        
        // Cargar el FXML (ruta corregida)
        Parent root = FXMLLoader.load(MainWindow.class.getResource("/com/mycompany/gestorui/views/LoginFXML.fxml"));
        mainStage.initStyle(StageStyle.UNDECORATED);
        Scene scene = new Scene(root);
        mainStage.setScene(scene);
        mainStage.show();
    }
    
}
