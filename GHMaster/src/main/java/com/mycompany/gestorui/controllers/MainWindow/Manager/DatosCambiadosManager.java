package com.mycompany.gestorui.controllers.MainWindow.Manager;

import java.util.ArrayList;
import java.util.List;

import com.mycompany.gestorui.controllers.MainWindow.Listener.DatosCambiadosListener;

public class DatosCambiadosManager {
    private static DatosCambiadosManager instance;
    private final List<DatosCambiadosListener> listeners;
    
    private DatosCambiadosManager() {
        listeners = new ArrayList<>();
    }
    
    public static DatosCambiadosManager getInstance() {
        if (instance == null) {
            instance = new DatosCambiadosManager();
        }
        return instance;
    }
    
    public void agregarListener(DatosCambiadosListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }
    
    public void removerListener(DatosCambiadosListener listener) {
        listeners.remove(listener);
    }
    
    /**
     * Notifica a todos los listeners que los datos han cambiado
     */
    public void notificarCambios() {
        for (DatosCambiadosListener listener : listeners) {
            listener.onDatosCambiados();
        }
    }

}
