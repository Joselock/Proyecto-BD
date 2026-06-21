/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package com.mycompany.gestorui.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mycompany.gestorui.controllers.Login.LoginController;

import static org.junit.Assert.*;
import org.junit.Ignore;

/**
 *
 * @author ignacio
 */
public class LoginControllerTest {
    
     private LoginController instance;
    
    public LoginControllerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        instance = new LoginController();
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of initialize method, of class LoginController.
     */
    @Test
    @Ignore("Este test requiere que visiblePasswordField (componente JavaFX) esté inicializado")
    public void testInitialize() {
        System.out.println("initialize");
        URL url = null;
        ResourceBundle rb = null;
        LoginController instance = new LoginController();
        instance.initialize(url, rb);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getUsuarioActual method, of class LoginController.
     */
    @Test
    public void testGetUsuarioActual() {
        System.out.println("getUsuarioActual");
        // Inicialmente debe ser null (nadie ha iniciado sesión)
        String result = LoginController.getUsuarioActual();
        assertNull("El usuario actual debería ser null antes de iniciar sesión", result);
    }
    
    /**
     * Verifica que la instancia se crea correctamente
     */
    @Test
    public void testInstanceCreation() {
        System.out.println("testInstanceCreation");
        assertNotNull("LoginController instance should not be null", instance);
    }
    
}
