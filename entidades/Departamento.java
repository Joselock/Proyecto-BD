package entidades;

import java.util.List;

public class Departamento{
    private String codigoDep;
    private String nombreDep;
    private List<Unidad>unidades;
    
    
    public Departamento() {
    }

    

    public Departamento(String codigoDep, String nombreDep, List<Unidad> unidades) {
        this.codigoDep = codigoDep;
        this.nombreDep = nombreDep;
        this.unidades = unidades;
    }



    public Departamento(String codigoDep, String nombreDep) {
        this.codigoDep = codigoDep;
        this.nombreDep = nombreDep;
    }

    public String getCodigoDep() {
        return codigoDep;
    }

    public void setCodigoDep(String codigoDep) {
        this.codigoDep = codigoDep;
    }

    public String getNombreDep() {
        return nombreDep;
    }

    public void setNombreDep(String nombreDep) {
        this.nombreDep = nombreDep;
    }

    public List<Unidad> getUnidades() {
        return unidades;
    }

    public void setUnidades(List<Unidad> unidades) {
        this.unidades = unidades;
    }

    


}