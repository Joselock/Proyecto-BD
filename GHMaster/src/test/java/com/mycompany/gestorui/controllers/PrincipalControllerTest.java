/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package com.mycompany.gestorui.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.stage.Stage;
import static org.junit.Assert.*;

/**
 *
 * @author ignacio
 */
public class PrincipalControllerTest {

    public PrincipalControllerTest() {
    }

    @org.junit.BeforeClass
    public static void setUpClass() throws Exception {
    }

    @org.junit.AfterClass
    public static void tearDownClass() throws Exception {
    }

    @org.junit.Before
    public void setUp() throws Exception {
    }

    @org.junit.After
    public void tearDown() throws Exception {
    }

    /**
     * Test of initialize method, of class PrincipalController.
     */
    @org.junit.Test
    public void testInitialize() {
        System.out.println("initialize");
        URL url = null;
        ResourceBundle rb = null;
        try {
            PrincipalController instance = new PrincipalController();
            instance.initialize(url, rb);
            assertTrue(true);
        } catch (Exception e) {
            fail("inicialize lanzo una excepcion"+e.getMessage());
        }
    }

    /**
     * Test of cerrarVentanaPrincipal method, of class PrincipalController.
     */
    @org.junit.Test
    public void testCerrarVentanaPrincipal() {
        System.out.println("cerrarVentanaPrincipal");
        
        try {
            PrincipalController.cerrarVentanaPrincipal();
            assertTrue(true); //Pasa si no hayy una excepcion
        } catch (Exception e) {
            fail("cerrarVentanaPrincipal lanzo una excepcion:"+e.getMessage());
        }
    }

    /**
     * Test of getStagePrincipal method, of class PrincipalController.
     */
    @org.junit.Test
    public void testGetStagePrincipal() {
        System.out.println("getStagePrincipal");
        Stage expResult = null;
        Stage result = PrincipalController.getStagePrincipal();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        assertNull("El stagePrincipal debería ser null inicialmente", result);
    }

}
