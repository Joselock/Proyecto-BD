package entidades;

import java.util.List;

public class Unidad {
    private String codigoUni;
    private String nombreUni;
    private String ubicacion;
    private List<Paciente> pacientes;
    private List<Medico>medicos;
    private List<Informe>informes;
    

    public Unidad(String codigoUni, String nombreUni, String ubicacion) {
        this.codigoUni = codigoUni;
        this.nombreUni = nombreUni;
        this.ubicacion = ubicacion;
    }

    public String getCodigoUni() {
        return codigoUni;
    }

    public void setCodigoUni(String codigoUni) {
        this.codigoUni = codigoUni;
    }

    public String getNombreUni() {
        return nombreUni;
    }

    public void setNombreUni(String nombreUni) {
        this.nombreUni = nombreUni;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public List<Paciente> getPacientes() {
        return pacientes;
    }

    public void setPacientes(List<Paciente> pacientes) {
        this.pacientes = pacientes;
    }

    public List<Medico> getMedicos() {
        return medicos;
    }

    public void setMedicos(List<Medico> medicos) {
        this.medicos = medicos;
    }

    public List<Informe> getInformes() {
        return informes;
    }

    public void setInformes(List<Informe> informes) {
        this.informes = informes;
    }

    
    
}