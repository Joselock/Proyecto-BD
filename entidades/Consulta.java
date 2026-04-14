package entidades;

public class Consulta {
    private String numH;
    private int numeroT;
    private boolean atendido;
    
    public Consulta(String numH, int numeroT, boolean atendido) {
        this.numH = numH;
        this.numeroT = numeroT;
        this.atendido = atendido;
    }

    public String getNumH() {
        return numH;
    }

    public void setNumH(String numH) {
        this.numH = numH;
    }

    public int getNumeroT() {
        return numeroT;
    }

    public void setNumeroT(int numeroT) {
        this.numeroT = numeroT;
    }

    public boolean isAtendido() {
        return atendido;
    }

    public void setAtendido(boolean atendido) {
        this.atendido = atendido;
    }

    

}
