package entidades;

public class Turno {
    private String numTurn;
    private int canAten;
    private Medico medResp;
    private String estTur;
    private String codUni;
    
    public Turno(String numTurn, int canAten, Medico medResp, String estTur, String codUni) {
        this.numTurn = numTurn;
        this.canAten = canAten;
        this.medResp = medResp;
        this.estTur = estTur;
        this.codUni = codUni;
    }

    public String getNumTurn() {
        return numTurn;
    }

    public void setNumTurn(String numTurn) {
        this.numTurn = numTurn;
    }

    public int getCanAten() {
        return canAten;
    }

    public void setCanAten(int canAten) {
        this.canAten = canAten;
    }

    public Medico getMedResp() {
        return medResp;
    }

    public void setMedResp(Medico medResp) {
        this.medResp = medResp;
    }

    public String getEstTur() {
        return estTur;
    }

    public void setEstTur(String estTur) {
        this.estTur = estTur;
    }

    public String getCodUni() {
        return codUni;
    }

    public void setCodUni(String codUni) {
        this.codUni = codUni;
    }

    

}
