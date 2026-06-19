package com.mycompany.gestorui.controllers.Registro;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;

public class RegistroCredencialesControllerTest {
    
    private RegistroCredencialesController instance;
    
    public RegistroCredencialesControllerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        instance = new RegistroCredencialesController();
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of initialize - requiere componentes JavaFX
     */
    @Test
    @Ignore("Requiere cargar el archivo FXML o usar Mockito para txtPasswordVisible, txtPassword, etc.")
    public void testInitialize() {
        System.out.println("initialize - Test ignorado por dependencia JavaFX");
        // URL url = null;
        // ResourceBundle rb = null;
        // instance.initialize(url, rb);
    }
    
    @Test
    public void testInstanceCreation() {
        System.out.println("testInstanceCreation");
        assertNotNull(instance);
    }
}