package com.mycompany.gestorui.controllers.Cuenta.Manager;

import com.mycompany.gestorui.controllers.Cuenta.Listener.PerfilListener;
import com.mycompany.gestorui.model.login.entidad.User;

public class PerfilManager {
    private static PerfilManager instance;
    private User datosUsuarioActual;
    private String avatarPath;
    private boolean avatarCargado = false;
    
    private PerfilManager() {}
    
    public static PerfilManager getInstance() {
        if (instance == null) {
            instance = new PerfilManager();
        }
        return instance;
    }
    
    public User getDatosUsuarioActual() {
        return datosUsuarioActual;
    }
    
    public void setDatosUsuarioActual(User user) {
        this.datosUsuarioActual = user;
    }
    
    public String getAvatarPath() {
        return avatarPath;
    }
    
    public void setAvatarPath(String path) {
        this.avatarPath = path;
        this.avatarCargado = true;
    }
    
    public boolean isAvatarCargado() {
        return avatarCargado;
    }
    
    public void limpiarAvatar() {
        this.avatarPath = null;
        this.avatarCargado = false;
    }
    
    public void notificarCambioPerfil() {
        // Este método será llamado cuando se actualice el perfil
        // Los controladores que implementen PerfilListener se actualizarán
        PerfilListener.notificarCambio();
    }

}
