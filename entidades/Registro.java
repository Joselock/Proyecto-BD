package entidades;

public class Registro {
    private String NHC;
    private boolean atend;
    private String causa;
    
    public Registro(String nHC, boolean atend, String causa) {
        NHC = nHC;
        this.atend = atend;
        this.causa = causa;
    }

    public String getNHC() {
        return NHC;
    }

    public void setNHC(String nHC) {
        NHC = nHC;
    }

    public boolean isAtend() {
        return atend;
    }

    public void setAtend(boolean atend) {
        this.atend = atend;
    }

    public String getCausa() {
        return causa;
    }

    public void setCausa(String causa) {
        this.causa = causa;
    }

    

}
