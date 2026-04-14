package entidades;

import java.sql.Date;

public class Paciente {
    private String numHisCli;
    private String nombrePac;
    private Date fechaN;
    private String direccionP;

    
    
    public Paciente() {
    }

    public Paciente(String numHisCli, String nombrePac, Date fechaN, String direccionP) {
        this.numHisCli = numHisCli;
        this.nombrePac = nombrePac;
        this.fechaN = fechaN;
        this.direccionP = direccionP;
    }

    public String getNumHisCli() {
        return numHisCli;
    }

    public void setNumHisCli(String numHisCli) {
        this.numHisCli = numHisCli;
    }

    public String getNombrePac() {
        return nombrePac;
    }

    public void setNombrePac(String nombrePac) {
        this.nombrePac = nombrePac;
    }

    public Date getFechaN() {
        return fechaN;
    }

    public void setFechaN(Date fechaN) {
        this.fechaN = fechaN;
    }

    public String getDireccionP() {
        return direccionP;
    }

    public void setDireccionP(String direccionP) {
        this.direccionP = direccionP;
    }

    

}
