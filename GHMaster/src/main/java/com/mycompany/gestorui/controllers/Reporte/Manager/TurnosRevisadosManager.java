package com.mycompany.gestorui.controllers.Reporte.Manager;

import java.util.ArrayList;
import java.util.List;

import com.mycompany.gestorui.controllers.Reporte.Listener.TurnosRevisadosListener;
import com.mycompany.gestorui.model.services.reportes.TurnosRevisadosService;

public class TurnosRevisadosManager {
    
    private static TurnosRevisadosManager instance;
    private final List<TurnosRevisadosListener> listeners;
    private final TurnosRevisadosService service;
    
    private TurnosRevisadosManager() {
        listeners = new ArrayList<>();
        service = TurnosRevisadosService.getInstance();
        // Cargar caché al iniciar
        service.cargar();
    }
    
    public static TurnosRevisadosManager getInstance() {
        if (instance == null) {
            instance = new TurnosRevisadosManager();
        }
        return instance;
    }
    
    public void marcarComoRevisado(String hospital, String departamento, String unidad, String medico) {
        // Actualizar el estado en la base de datos
        boolean exito = service.marcarComoRevisado(hospital, departamento, unidad, medico);
        
        if (exito) {
            System.out.println("✅ Turno marcado como Revisado: " + hospital + " | " + departamento + " | " + unidad + " | " + medico);
            // Notificar a todos los listeners
            notificarListeners(hospital, departamento, unidad, medico);
        } else {
            System.err.println("❌ Error al marcar el turno como revisado");
        }
    }
    
    public boolean esRevisado(String hospital, String departamento, String unidad, String medico) {
        return service.esRevisado(hospital, departamento, unidad, medico);
    }
    
    public void recargarCache() {
        service.limpiar();
        service.cargar();
        // Notificar a todos los listeners para que actualicen la UI
        notificarListeners("", "", "", "");
    }
    
    // Métodos para manejar listeners
    public void agregarListener(TurnosRevisadosListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }
    
    public void removerListener(TurnosRevisadosListener listener) {
        listeners.remove(listener);
    }
    
    private void notificarListeners(String hospital, String departamento, String unidad, String medico) {
        for (TurnosRevisadosListener listener : listeners) {
            listener.onTurnoRevisado(hospital, departamento, unidad, medico);
        }
    }
}