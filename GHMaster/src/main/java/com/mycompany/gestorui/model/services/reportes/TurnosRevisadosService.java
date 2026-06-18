package com.mycompany.gestorui.model.services.reportes;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import com.mycompany.gestorui.model.utils.BaseDatos;

public class TurnosRevisadosService {
    private static TurnosRevisadosService instance;
    private final Set<String> cacheTurnosRevisados;
    private boolean cargado = false;

    private TurnosRevisadosService() {
        cacheTurnosRevisados = new HashSet<>();
    }

    public static TurnosRevisadosService getInstance() {
        if (instance == null) {
            instance = new TurnosRevisadosService();
        }
        return instance;
    }

    /**
     * Marca un turno como revisado actualizando su estado en la tabla Turno
     */
    public boolean marcarComoRevisado(String hospital, String departamento, String unidad, String medico) {
        // Buscar el turno basado en los datos y actualizar su estado
        String sqlActualizar = "UPDATE public.\"Turno\" t " +
                "SET estTur = 'Revisado' " +
                "FROM public.\"Unidad\" u " +
                "JOIN public.\"Departamento\" d ON u.\"codDep\" = d.\"codDep\" " +
                "JOIN public.\"Hospital\" h ON d.\"codHos\" = h.\"codHos\" " +
                "JOIN public.\"Medico\" m ON t.\"codMed\" = m.\"codMed\" " +
                "WHERE t.\"codUni\" = u.\"codUni\" " +
                "AND h.\"nomHos\" = ? " +
                "AND d.\"nomDep\" = ? " +
                "AND u.\"nomUni\" = ? " +
                "AND m.\"nomMed\" = ? " +
                "AND t.\"estTur\" = 'Revisar'";

        try (java.sql.Connection con = BaseDatos.getConnection();
                PreparedStatement ps = con.prepareStatement(sqlActualizar)) {

            ps.setString(1, hospital);
            ps.setString(2, departamento);
            ps.setString(3, unidad);
            ps.setString(4, medico);

            int result = ps.executeUpdate();

            if (result > 0) {
                String clave = generarClave(hospital, departamento, unidad, medico);
                cacheTurnosRevisados.add(clave);
                System.out.println("✅ Turno marcado como Revisado: " + hospital + " | " + departamento + " | " + unidad
                        + " | " + medico);
                return true;
            } else {
                // Si no hay turno pendiente, lo marcamos como revisado en caché igual
                String clave = generarClave(hospital, departamento, unidad, medico);
                cacheTurnosRevisados.add(clave);
                System.out.println("⚠️ No se encontró turno pendiente, pero se marcó en caché: " + clave);
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Verifica si un turno ya fue revisado (estado = 'Revisado' o 'Extioso')
     */
    public boolean esRevisado(String hospital, String departamento, String unidad, String medico) {
        // Si no está cargado, cargarlo
        if (!cargado) {
            cargar();
        }

        String clave = generarClave(hospital, departamento, unidad, medico);
        return cacheTurnosRevisados.contains(clave);
    }

    /**
     * Carga todos los turnos que ya están revisados desde la base de datos
     */
    public void cargar() {
        cacheTurnosRevisados.clear();

        String sql = "SELECT h.\"nomHos\", d.\"nomDep\", u.\"nomUni\", m.\"nomMed\" " +
                "FROM public.\"Turno\" t " +
                "JOIN public.\"Unidad\" u ON t.\"codUni\" = u.\"codUni\" " +
                "JOIN public.\"Departamento\" d ON u.\"codDep\" = d.\"codDep\" " +
                "JOIN public.\"Hospital\" h ON d.\"codHos\" = h.\"codHos\" " +
                "JOIN public.\"Medico\" m ON t.\"codMed\" = m.\"codMed\" " +
                "WHERE t.\"estTur\" IN ('Revisado', 'Extioso')";

        try (java.sql.Connection con = BaseDatos.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String hospital = rs.getString("nomHos");
                String departamento = rs.getString("nomDep");
                String unidad = rs.getString("nomUni");
                String medico = rs.getString("nomMed");

                String clave = generarClave(hospital, departamento, unidad, medico);
                cacheTurnosRevisados.add(clave);
            }

            cargado = true;
            System.out
                    .println("📦 Cargados " + cacheTurnosRevisados.size() + " turnos revisados desde la base de datos");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Limpia el caché (útil para forzar recarga)
     */
    public void limpiar() {
        cargado = false;
        cacheTurnosRevisados.clear();
    }

    private String generarClave(String hospital, String departamento, String unidad, String medico) {
        return hospital + "|" + departamento + "|" + unidad + "|" + medico;
    }

}
