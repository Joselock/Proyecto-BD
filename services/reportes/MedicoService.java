package services.reportes;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;

import entidades.Departamento;
import entidades.Hospital;
import entidades.Medico;
import entidades.Registro;
import entidades.Unidad;
import utils.BaseDatos;

public class MedicoService {

    // Reporte 2: Obtener listado de medicos

    public LinkedList<Hospital> obtenerListadosMedicos() throws SQLException {
        LinkedList<Hospital> listado = new LinkedList<>();
        HashMap<String,Hospital>hospitalMap = new HashMap<>();

        java.sql.Connection con = BaseDatos.getConnection();
        String function = "SELECT * FROM public.listadoMedicos()";

        PreparedStatement ps = con.prepareStatement(function);
        ResultSet rs = ps.executeQuery();

        

        while (rs.next()) {
            String hospital = rs.getString("hospital");
            String departamento = rs.getString("departamento");
            String unidad = rs.getString("unidad");
            String nombre = rs.getString("nombre");
            String licencia = rs.getString("licencia");
            String especialidad = rs.getString("especialidad");
            String telefono = rs.getString("telefono");
            int experiencia = rs.getInt("experiencia");
            String datCon = rs.getString("datosCon");

           Medico m = new Medico(null, nombre, especialidad, licencia, datCon, experiencia,telefono);

           Hospital h = hospitalMap.get(hospital);
           if (h == null) {
             h = new Hospital(null, hospital,new LinkedList<>());
             hospitalMap.put(hospital, h);
             listado.add(h);
           }
           
           Departamento d = null;
            for (Departamento dep : h.getDepartamentos()) {
                if (dep.getNombreDep().equals(departamento)) {
                    d = dep;
                    break;
                }
            }
            if (d == null) {
                d = new Departamento(null, departamento, new LinkedList<>());
                h.getDepartamentos().add(d);
            }


            Unidad u = null;
            for (Unidad uni : d.getUnidades()) {
                if (uni.getNombreUni().equals(unidad)) {
                    u = uni;
                    break;
                }
            }
            if (u == null) {
                u = new Unidad(null, unidad, null, new Registro(null, new LinkedList<>()),
                               new LinkedList<>() ,new LinkedList<>(),new LinkedList<>());
                d.getUnidades().add(u);
            }

            u.getMedicos().add(m);

        }

        ps.close();
        rs.close();
        con.close();

        return listado;
    }

}
