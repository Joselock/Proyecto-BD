package services;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;

import utils.BaseDatos;

public class PacienteService {

    // REPORTE 1

    // OBTENER EL LISTADO DE PACIENTES DE UN DEPARTAMENTO ESPECIFICO
    public LinkedList<String> obtenerPacientesDepartamento(String codigo) throws SQLException {
        LinkedList<String> lista = new LinkedList<>();
        java.sql.Connection con = BaseDatos.getConnection();

        String function = "SELECT * FROM public.obtenerPacientesDepartamento(?)";

        PreparedStatement ps = con.prepareStatement(function);
        ps.setString(1, codigo);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            String paciente = String.format(
                    "Hospital: %s| Departamento: %s | Unidad: %s" +
                            "| Historia: %s | Nombre: %s | Fecha Nac: %s | Direccion: %s",
                    rs.getString("hospital"),
                    rs.getString("departamento"),
                    rs.getString("unidad"),
                    rs.getString("numHis"),
                    rs.getString("nombre"),
                    rs.getString("fechaN"),
                    rs.getString("direccion"));
            lista.add(paciente);
        }

        ps.close();
        rs.close();
        con.close();

        return lista;
    }

    // OBTENER LISTA DE PACIENTES DE UNA UNIDAD ESPECIFICA
    public LinkedList<String> obtenerPacientesUnidad(String codigo) throws SQLException {
        LinkedList<String> lista = new LinkedList<>();
        java.sql.Connection con = BaseDatos.getConnection();

        String function = "SELECT * FROM public.obtenerPacientesUnidad(?)";

        PreparedStatement ps = con.prepareStatement(function);
        ps.setString(1, codigo);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            String pacientes = String.format(
                    "Hospital: %s| Departamento: %s | Unidad: %s" +
                            "| Historia: %s | Nombre: %s | Fecha Nac: %s | Direccion: %s",
                    rs.getString("hospital"),
                    rs.getString("departamento"),
                    rs.getString("unidad"),
                    rs.getString("numHis"),
                    rs.getString("nombre"),
                    rs.getString("fechaN"),
                    rs.getString("direccion"));
            lista.add(pacientes);
        }

        ps.close();
        rs.close();
        con.close();

        return lista;
    }

    // OBTENER LISTA DE PACIENTES DE TODOS LOS DEPARTAMENTOS DE UN HOSPITAL
    public LinkedList<String> obtenerPacientesDeDepartamentos() throws SQLException {
        LinkedList<String> lista = new LinkedList<>();
        java.sql.Connection con = BaseDatos.getConnection();

        String function = "SELECT * FROM public.obtenerPacientesDeDepartamentos() ORDER BY departamento, unidad, nombre";

        PreparedStatement ps = con.prepareStatement(function);
        ResultSet rs = ps.executeQuery();

        String hospitalActual = "";
        String departamentoActual = "";
        String unidadActual = "";
        int contadorPacientes = 0;

        // Formateador de fechas
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");

        while (rs.next()) {
            String hospital = rs.getString("hospital");
            String departamento = rs.getString("departamento");
            String unidad = rs.getString("unidad");
            String numHis = rs.getString("numHis");
            String nombre = rs.getString("nombre");
            Date fechaN = rs.getDate("fechaN");
            String direccion = rs.getString("direccion");

            String fechaStr = (fechaN != null) ? sdf.format(fechaN) : "No registrada";
            String direccionStr = (direccion != null && !direccion.isEmpty()) ? direccion : "No registrada";

            // Mostrar hospital solo cuando cambia
            if (!hospital.equals(hospitalActual)) {
                if (!hospitalActual.isEmpty()) {
                    lista.add(""); // línea en blanco entre hospitales
                    lista.add("═══════════════════════════════════════════════════════════════════════════════");
                }
                lista.add("");
                lista.add(" HOSPITAL: " + hospital);
                lista.add("═══════════════════════════════════════════════════════════════════════════════");
                hospitalActual = hospital;
                departamentoActual = "";
                unidadActual = "";
                contadorPacientes = 0;
            }

            // Mostrar departamento solo cuando cambia
            if (!departamento.equals(departamentoActual)) {
                lista.add("");
                lista.add("DEPARTAMENTO: " + departamento);
                lista.add("┌─────────────────────────────────────────────────────────────────────────────");
                departamentoActual = departamento;
                unidadActual = "";
            }

            // Mostrar unidad solo cuando cambia
            if (!unidad.equals(unidadActual)) {
                lista.add("│");
                lista.add("├── UNIDAD: " + unidad);
                lista.add("│");
                lista.add(String.format("|        %-18s | %-10s | %-10s | %-10s", "Nombre y apellidos", "Historia",
                        "Nacimiento", "Direccion"));
                lista.add(String.format("        ────────────────────────────────────────────────────────────"));
                unidadActual = unidad;
            }

            // Mostrar paciente en formato tabla
            contadorPacientes++;
            lista.add(String.format("│    %d. %-19s │ %-10s │ %-10s │ %-10s |",
                    contadorPacientes,
                    nombre.length() > 25 ? nombre.substring(0, 22) + "..." : nombre,
                    numHis,
                    fechaStr, direccionStr));

            lista.add("│");
        }

        ps.close();
        rs.close();
        con.close();

        return lista;
    }

}
