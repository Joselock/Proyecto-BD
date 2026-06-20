package com.mycompany.gestorui.controllers.Cuenta.Listener;

import java.util.ArrayList;
import java.util.List;

public class PerfilListener {
    private static final List<Runnable> listeners = new ArrayList<>();

    public static void agregarListener(Runnable listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public static void removerListener(Runnable listener) {
        listeners.remove(listener);
    }

    public static void notificarCambio() {
        for (Runnable listener : listeners) {
            listener.run();
        }
    }
}
