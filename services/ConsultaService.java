package services;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.LinkedList;

import entidades.Departamento;
import entidades.Hospital;
import entidades.Informe;
import entidades.Turno;
import entidades.Unidad;
import utils.BaseDatos;

public class ConsultaService {


    // REPORTE 5 - Informe durante las consultas

    public LinkedList<Hospital> obtenerInformeHospital(String codigo) throws SQLException {
        LinkedList<Hospital> resultado = new LinkedList<>();
        String function = "SELECT * FROM public.informeDuranteConsultaHospital(?)";

        java.sql.Connection con = BaseDatos.getConnection();
        PreparedStatement ps = con.prepareStatement(function);
        ps.setString(1, codigo);
        ResultSet rs = ps.executeQuery();


        while (rs.next()) {
            String nomHos = rs.getString("hospital");
            String nomDep = rs.getString("departamento");
            String nomUni = rs.getString("unidad");
            Date fecha = rs.getDate("fecha");
            String numTurno = rs.getString("numTurno");
            Time hora = rs.getTime("hora_informe");
            int numInforme = rs.getInt("numInforme");
            int cantInicial = rs.getInt("canIniPac");
            int cantAdmitida = rs.getInt("cantAdmPac");
            int cantAlta = rs.getInt("cantAltPac");
            int cantAnterior = rs.getInt("cantAnterior");
            int cantDia = rs.getInt("cantDia");


            Informe informe = new Informe(hora, fecha, numInforme, cantAnterior, cantDia, cantAdmitida, cantAlta,cantInicial);
            Turno turno = new Turno(numTurno, 0, null, null, null);
    
            Unidad unidad = new Unidad(null, nomUni, null, null, null,new LinkedList<>(),new LinkedList<>());
            unidad.getInformes().add(informe);
            unidad.getTurnos().add(turno);

            Departamento departamento = new Departamento(null, nomDep, new LinkedList<>());
            departamento.getUnidades().add(unidad);

            Hospital hospital = new Hospital(codigo, nomHos,new LinkedList<>());
            hospital.getDepartamentos().add(departamento);

            resultado.add(hospital);

        }

        
        return resultado;
    }
}


