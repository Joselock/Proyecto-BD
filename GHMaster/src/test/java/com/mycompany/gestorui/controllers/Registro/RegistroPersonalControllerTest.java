package com.mycompany.gestorui.controllers.Registro;

import java.net.URL;
import java.util.ResourceBundle;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class RegistroPersonalControllerTest {
    
    private RegistroPersonalController instance;
    
    public RegistroPersonalControllerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        // Reiniciar datos estáticos antes de todas las pruebas
        // Como son private static, usamos reflexión o simplemente los dejamos
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        instance = new RegistroPersonalController();
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void testInitialize() {
        System.out.println("initialize");
        URL url = null;
        ResourceBundle rb = null;
        
        instance.initialize(url, rb);
        
        // Después de initialize, todos los datos estáticos deben ser null
        assertNull(RegistroPersonalController.getNombreCompleto());
        assertNull(RegistroPersonalController.getEspecialidad());
        assertNull(RegistroPersonalController.getDireccion());
        assertNull(RegistroPersonalController.getTelefono());
    }

    @Test
    public void testGetNombreCompleto() {
        System.out.println("getNombreCompleto");
        // Inicialmente debe ser null, no cadena vacía
        assertNull(RegistroPersonalController.getNombreCompleto());
    }

    @Test
    public void testGetEspecialidad() {
        System.out.println("getEspecialidad");
        assertNull(RegistroPersonalController.getEspecialidad());
    }

    @Test
    public void testGetDireccion() {
        System.out.println("getDireccion");
        assertNull(RegistroPersonalController.getDireccion());
    }

    @Test
    public void testGetTelefono() {
        System.out.println("getTelefono");
        assertNull(RegistroPersonalController.getTelefono());
    }
}