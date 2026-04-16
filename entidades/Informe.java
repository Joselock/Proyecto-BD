package entidades;

import java.sql.Date;
import java.sql.Time;

public class Informe {
    private Time hora;
    private Date fecha;
    private int numIn;
    private int pacAtend;
    private int pacAlta;
    private int cantAdm;
    private int total;
    private int cantIni;

    public Informe(){}

    
    //Informe-consulta
    public Informe(Time hora, Date fecha, int numIn, int pacAtend, int pacAlta, int cantAdm, int total, int cantIni) {
        this.hora = hora;
        this.fecha = fecha;
        this.numIn = numIn;
        this.pacAtend = pacAtend;
        this.pacAlta = pacAlta;
        this.cantAdm = cantAdm;
        this.total = total;
        this.cantIni = cantIni;
    }



    public Informe(Time hora, Date fecha, int numIn, int pacAtend, int pacAlta, int cantAdm, int total) {
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

    public int getNumIn() {
        return numIn;
    }

    public void setNumIn(int numIn) {
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


    public int getCantIni() {
        return cantIni;
    }


    public void setCantIni(int cantIni) {
        this.cantIni = cantIni;
    }

    
    

}
