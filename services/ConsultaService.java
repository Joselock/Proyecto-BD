package services;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import entidades.Departamento;
import entidades.Hospital;
import entidades.Informe;
import entidades.Unidad;
import utils.BaseDatos;


public class ConsultaService {


    // REPORTE 5 - Informe durante las consultas

    public LinkedList<Hospital> obtenerInformeHospital(String codigo) throws SQLException {
        LinkedList<Hospital> resultado = new LinkedList<>();
        String sql = "SELECT * FROM public.informeDuranteConsultaHospital(?)";

        try (Connection con = BaseDatos.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {

                Hospital hospital = null;
                // Mapas para evitar duplicados y mantener referencias
                Map<String, Departamento> deptosMap = new HashMap<>();
                Map<String, Unidad> unidadesMap = new HashMap<>();

                while (rs.next()) {
                    // Crear el objeto Hospital una sola vez (primera fila)
                    if (hospital == null) {
                        hospital = new Hospital();
                        hospital.setCodigoHos(codigo);
                        hospital.setNombreHos(rs.getString("hospital"));
                        hospital.setDepartamentos(new LinkedList<>());
                    }

                    // Obtener datos de la fila
                    String nombreDep = rs.getString("departamento");
                    String nombreUni = rs.getString("unidad");
                    java.sql.Date fecha = rs.getDate("fecha");
                    java.sql.Time hora = rs.getTime("hora_informe");
                    int numInforme = rs.getInt("numInforme");
                    int cantIniPac = rs.getInt("cantIniPac");
                    int cantAdmPac = rs.getInt("cantAdmPac");
                    int cantAltPac = rs.getInt("cantAltPac");
                    int cantAnterior = rs.getInt("cantAnterior");
                    int cantDia = rs.getInt("cantDia");

                    // Buscar o crear Departamento
                    Departamento departamento = deptosMap.get(nombreDep);
                    if (departamento == null) {
                        departamento = new Departamento();
                        departamento.setNombreDep(nombreDep);
                        departamento.setUnidades(new LinkedList<>());
                        deptosMap.put(nombreDep, departamento);
                        hospital.getDepartamentos().add(departamento);
                    }

                    // Buscar o crear Unidad dentro del departamento
                    // Nota: la unidad se identifica por nombre (podrías usar código si la función lo retorna)
                    Unidad unidad = unidadesMap.get(nombreUni);
                    if (unidad == null) {
                        unidad = new Unidad(null, nombreUni, null); // código y ubicación se pueden setear después
                        unidad.setInformes(new LinkedList<>());
                        unidadesMap.put(nombreUni, unidad);
                        departamento.getUnidades().add(unidad);
                    }

                    // Crear el objeto Informe con todos los campos
                    // El constructor de Informe espera: (Time hora, Date fecha, String numIn, int pacAtend, int pacAlta, int cantAdm, int total)
                    // Asignamos:
                    // - pacAtend = cantAnterior (pacientes atendidos desde el informe anterior)
                    // - pacAlta = cantAltPac
                    // - cantAdm = cantAdmPac
                    // - total = cantDia (acumulado del día)
                    Informe informe = new Informe(
                        hora,
                        fecha,
                        String.valueOf(numInforme),
                        cantAnterior,    // pacientes atendidos en el periodo
                        cantAltPac,      // pacientes dados de alta
                        cantAdmPac,      // pacientes admitidos
                        cantDia          // total acumulado en el día hasta este informe
                    );
                    // Si tu clase Informe tiene un setter para numTurno, puedes agregarlo:
                    // informe.setNumTurno(numTurno);
                    unidad.getInformes().add(informe);
                }

                if (hospital != null) {
                    resultado.add(hospital);
                }
            }
        }
        return resultado;
    }
}


