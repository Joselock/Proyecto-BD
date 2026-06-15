package com.mycompany.gestorui.model.services.crud;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import com.mycompany.gestorui.model.utils.BaseDatos;

public class crudTurno {

    public Map<String, Object> insertarTurno(int numTur, int cantAtendidos, String estadoTur, String codUni,
            String codMed) {
        
        java.sql.Connection con = BaseDatos.getConnection();          
        String function = "SELECT * FROM public.insertarTurno(?, ?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(function)) {
            ps.setInt(1, numTur);
            ps.setInt(2, cantAtendidos);
            ps.setString(3, estadoTur);
            ps.setString(4, codUni);
            ps.setString(5, codMed);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Map.of(
                            "existe", rs.getBoolean("existe"),
                            "mensaje", rs.getString("mensaje"),
                            "numTur", rs.getInt("numTur"),
                            "cantAtendidos", rs.getInt("cantAtendidos"),
                            "estadoTur", rs.getString("estadoTur"),
                            "codUni", rs.getString("codUni"),
                            "codMed", rs.getString("codMed"));
                }
            }
        } catch (SQLException e) {
            return Map.of("existe", false, "mensaje", e.getMessage());
        }
        return Map.of("existe", false, "mensaje", "Sin resultado");
    }

    public Map<String, Object> modificarTurno(int numActual, int numNuevo, int cantAtendidos, String estadoTur,
            String codUni, String codMed) {

        java.sql.Connection con = BaseDatos.getConnection();          
        String function = "SELECT * FROM public.modificarTurno(?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(function)) {
            ps.setInt(1, numActual);
            ps.setInt(2, numNuevo);
            ps.setInt(3, cantAtendidos);
            ps.setString(4, estadoTur);
            ps.setString(5, codUni);
            ps.setString(6, codMed);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Map.of(
                            "existe", rs.getBoolean("existe"),
                            "mensaje", rs.getString("mensaje"),
                            "numTur", rs.getInt("numTur"),
                            "cantAtendidos", rs.getInt("cantAtendidos"),
                            "estadoTur", rs.getString("estadoTur"),
                            "codUni", rs.getString("codUni"),
                            "codMed", rs.getString("codMed"));
                }
            }
        } catch (SQLException e) {
            return Map.of("existe", false, "mensaje", e.getMessage());
        }
        return Map.of("existe", false, "mensaje", "Sin resultado");
    }

    public String eliminarTurno(int numTur) {

        java.sql.Connection con = BaseDatos.getConnection();  
        String function = "SELECT public.eliminarTurno(?)";

        try (PreparedStatement ps = con.prepareStatement(function)) {
            ps.setInt(1, numTur);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return rs.getString(1);
            }
        } catch (SQLException e) {
            return "Error: " + e.getMessage();
        }
        return "Error: sin respuesta";
    }

}
