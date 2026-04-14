package services;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

import entidades.Hospital;
import utils.BaseDatos;

public class HospitalService {

    // REPORTE 3

    // OBTENER RESUMEN DE HOSPITALES
    public LinkedList<Hospital> resumenHospitales() throws SQLException {
        LinkedList<Hospital> resumen = new LinkedList<>();
        java.sql.Connection con = BaseDatos.getConnection();

        String function = "SELECT * FROM public.resumenHospitales()";

        PreparedStatement ps = con.prepareStatement(function);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            String hospital = rs.getString("hospital");
            int cantDep = rs.getInt("cantDepartamentos");
            int cantUni = rs.getInt("cantUnidades");
            int cantMed = rs.getInt("cantMedicos");
            int cantPac = rs.getInt("totalPacientes");

            resumen.add(new Hospital(null, hospital, cantDep, cantUni, cantMed, cantPac));
        }

        ps.close();
        rs.close();
        con.close();

        return resumen;
    }

    // REPORTE 4

    // Obtener listado de hospitales con mas de 100 pacientes(5 primeros)
    public LinkedList<Hospital> hospitalesMayorCantidadPacientes() {
        LinkedList<Hospital> listado = new LinkedList<>();

        try {

            java.sql.Connection con = BaseDatos.getConnection();

            String function = "SELECT * FROM public.hospitalesMayorCantPacientes()";

            PreparedStatement ps = con.prepareStatement(function);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String hospital = rs.getString("hospital");
                int cantPacientes = rs.getInt("cantPacientes");

                listado.add(new Hospital(null, hospital, 0, 0, 0, cantPacientes));

            }

            ps.close();
            rs.close();
            con.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return listado;
    }

}
