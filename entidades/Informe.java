package entidades;

import java.sql.Date;
import java.sql.Time;

public class Informe {
    private Time hora;
    private Date fecha;
    private String numIn;
    private int pacAtend;
    private int pacAlta;
    private int cantAdm;
    private int total;
    
    public Informe(Time hora, Date fecha, String numIn, int pacAtend, int pacAlta, int cantAdm, int total) {
        this.hora = hora;
        this.fecha = fecha;
        this.numIn = numIn;
        this.pacAtend = pacAtend;
        this.pacAlta = pacAlta;
        this.cantAdm = cantAdm;
        this.total = total;
    }

    public Time getHora() {
        return hora;
    }

    public void setHora(Time hora) {
        this.hora = hora;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getNumIn() {
        return numIn;
    }

    public void setNumIn(String numIn) {
        this.numIn = numIn;
    }

    public int getPacAtend() {
        return pacAtend;
    }

    public void setPacAtend(int pacAtend) {
        this.pacAtend = pacAtend;
    }

    public int getPacAlta() {
        return pacAlta;
    }

    public void setPacAlta(int pacAlta) {
        this.pacAlta = pacAlta;
    }

    public int getCantAdm() {
        return cantAdm;
    }

    public void setCantAdm(int cantAdm) {
        this.cantAdm = cantAdm;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    

}
