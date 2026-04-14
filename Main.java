import java.sql.SQLException;
import java.util.LinkedList;

import entidades.Hospital;
import services.HospitalService;

public class Main {

    public static void main(String[] args) throws SQLException {


        HospitalService ms = new HospitalService();

        LinkedList<Hospital> r = new LinkedList<>();
        r = ms.hospitalesMayorCantidadPacientes();
        
        for (Hospital p : r) {
            System.out.println("Hospital:"+p.getNombreHos());
            System.out.println("Pacientes:"+p.getCantPac());
            System.out.println("----------------------------------------");
        }

    }

}
