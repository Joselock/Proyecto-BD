package services;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

import utils.BaseDatos;

public class MedicoService {

    // REPORTE 2

    // OBTENER LISTADO DE MEDICOS
    public LinkedList<String> obtenerListadosMedicos() throws SQLException {
        LinkedList<String> listado = new LinkedList<>();

        java.sql.Connection con = BaseDatos.getConnection();
        String function = "SELECT * FROM public.obtenerListadoDeMedicos()";

        PreparedStatement ps = con.prepareStatement(function);
        ResultSet rs = ps.executeQuery();

        String hospitalActual = "";
        String departamentoActual = "";
        String unidadActual = "";
        int contadorMedicos = 0;

        while (rs.next()) {
            String hospital = rs.getString("hospital");
            String departamento = rs.getString("departamento");
            String unidad = rs.getString("unidad");
            String nombre = rs.getString("nombre");
            String licencia = rs.getString("licencia");
            String telefono = rs.getString("telefono");
            int experiencia = rs.getInt("experiencia");
            String datCon = rs.getString("datCon");

            // Mostrar hospital solo cuando cambia
            if (!hospital.equals(hospitalActual)) {
                if (!hospitalActual.isEmpty()) {
                    listado.add(""); // línea en blanco entre hospitales
                    listado.add("═══════════════════════════════════════════════════════════════════════════════");
                }
                listado.add("");
                listado.add(" HOSPITAL: " + hospital);
                listado.add("═══════════════════════════════════════════════════════════════════════════════");
                hospitalActual = hospital;
                departamentoActual = "";
                unidadActual = "";
                contadorMedicos = 0;
            }

            // Mostrar departamento solo cuando cambia
            if (!departamento.equals(departamentoActual)) {
                listado.add("");
                listado.add("DEPARTAMENTO: " + departamento);
                listado.add("┌─────────────────────────────────────────────────────────────────────────────");
                departamentoActual = departamento;
                unidadActual = "";
            }

            // Mostrar unidad solo cuando cambia
            if (!unidad.equals(unidadActual)) {
                listado.add("│");
                listado.add("├── UNIDAD: " + unidad);
                listado.add("│");
                listado.add(String.format("|        %-18s | %-10s | %-10s | %-10s | %-10s", "Nombre y apellidos",
                        "Licencia",
                        "Telefono", "Experiencia", "Datos"));
                listado.add(String
                        .format("        ────────────────────────────────────────────────────────────────────────"));
                unidadActual = unidad;
            }

            // Mostrar medico en formato tabla
            contadorMedicos++;
            listado.add(String.format("│    %d. %-19s │ %-10s │ %-10s │ %-10s | %-10s |",
                    contadorMedicos,
                    nombre.length() > 25 ? nombre.substring(0, 22) + "..." : nombre,
                    licencia,
                    telefono, experiencia, datCon));

            listado.add("│");

        }

        ps.close();
        rs.close();
        con.close();

        return listado;
    }

}
