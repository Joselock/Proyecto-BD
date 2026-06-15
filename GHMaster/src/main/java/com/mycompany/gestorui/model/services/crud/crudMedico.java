package com.mycompany.gestorui.model.services.crud;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import com.mycompany.gestorui.model.utils.BaseDatos;

public class crudMedico {

    public Map<String, Object> insertarMedico(String codMed, String nomMed, String especialidad,
            String licencia, String telefono, int experiencia,
            String datosCon, String codUni) {

        java.sql.Connection con = BaseDatos.getConnection();        
        String function = "SELECT * FROM public.insertarMedico(?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(function)) {
            ps.setString(1, codMed);
            ps.setString(2, nomMed);
            ps.setString(3, especialidad);
            ps.setString(4, licencia);
            ps.setString(5, telefono);
            ps.setInt(6, experiencia);
            ps.setString(7, datosCon);
            ps.setString(8, codUni);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Map.of(
                            "existe", rs.getBoolean("existe"),
                            "mensaje", rs.getString("mensaje"),
                            "codMed", rs.getString("codMed"),
                            "nomMed", rs.getString("nomMed"),
                            "espMed", rs.getString("espMed"),
                            "numLic", rs.getString("numLic"),
                            "telefono", rs.getString("telefono"),
                            "experiencia", rs.getInt("experiencia"),
                            "datosCon", rs.getString("datosCon"),
                            "codUni", rs.getString("codUni"));
                }
            }
        } catch (SQLException e) {
            return Map.of("existe", false, "mensaje", e.getMessage());
        }

        return Map.of("existe", false, "mensaje", "Sin resultado");
    }

    public Map<String, Object> modificarMedico(String oldCod, String newCod, String nomMed, String espMed,
            String numLic, String telefono, int experiencia,
            String datos, String codUni) {

        java.sql.Connection con = BaseDatos.getConnection();          
        String function  = "SELECT * FROM public.modificarMedico(?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(function )) {
            ps.setString(1, oldCod);
            ps.setString(2, newCod);
            ps.setString(3, nomMed);
            ps.setString(4, espMed);
            ps.setString(5, numLic);
            ps.setString(6, telefono);
            ps.setInt(7, experiencia);
            ps.setString(8, datos);
            ps.setString(9, codUni);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Map.of(
                            "existe", rs.getBoolean("existe"),
                            "mensaje", rs.getString("mensaje"),
                            "codMed", rs.getString("codMed"),
                            "nomMed", rs.getString("nomMed"),
                            "espMed", rs.getString("espMed"),
                            "numLic", rs.getString("numLic"),
                            "telefono", rs.getString("telefono"),
                            "experiencia", rs.getInt("experiencia"),
                            "datos", rs.getString("datos"),
                            "codUni", rs.getString("codUni"));
                }
            }
        } catch (SQLException e) {
            return Map.of("existe", false, "mensaje", e.getMessage());
        }

        return Map.of("existe", false, "mensaje", "Sin resultado");
    }

    public String eliminarMedico(String codMed) {

        java.sql.Connection con = BaseDatos.getConnection();  
        String function  = "SELECT public.eliminarMedico(?)";

        try (PreparedStatement ps = con.prepareStatement(function )) {
            ps.setString(1, codMed);
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
