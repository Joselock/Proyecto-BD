package com.mycompany.gestorui.model.services.crud;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Map;

import com.mycompany.gestorui.model.utils.BaseDatos;

public class crudPaciente {

    public Map<String, Object> insertarPaciente(String numHistoria, String nomPac, String direccion,
            LocalDate fechaNac, String estadoPac, String codUni) {

        java.sql.Connection con = BaseDatos.getConnection();        
        String function = "SELECT * FROM public.insertarPaciente(?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(function)) {
            ps.setString(1, numHistoria);
            ps.setString(2, nomPac);
            ps.setString(3, direccion);
            ps.setDate(4, Date.valueOf(fechaNac));
            ps.setString(5, estadoPac);
            ps.setString(6, codUni);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Map.of(
                            "existe", rs.getBoolean("existe"),
                            "mensaje", rs.getString("mensaje"),
                            "numHistoria", rs.getString("numHistoria"),
                            "nomPac", rs.getString("nomPac"),
                            "direccion", rs.getString("direccion"),
                            "fecha", rs.getDate("fecha"),
                            "estadoPac", rs.getString("estadoPac"),
                            "codUni", rs.getString("codUni"));
                }
            }
        } catch (SQLException e) {
            return Map.of("existe", false, "mensaje", e.getMessage());
        }
        return Map.of("existe", false, "mensaje", "Sin resultado");
    }

    public Map<String, Object> modificarPaciente(String oldHistoria, String newHistoria, String estPac,
            String nomPac, String direccion, LocalDate fechaN, String codUni) {
        
        java.sql.Connection con = BaseDatos.getConnection();          
        String function = "SELECT * FROM public.modificarPaciente(?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(function)) {
            ps.setString(1, oldHistoria);
            ps.setString(2, newHistoria);
            ps.setString(3, estPac);
            ps.setString(4, nomPac);
            ps.setString(5, direccion);
            ps.setDate(6, Date.valueOf(fechaN));
            ps.setString(7, codUni);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Map.of(
                            "existe", rs.getBoolean("existe"),
                            "mensaje", rs.getString("mensaje"),
                            "numHistoria", rs.getString("numHistoria"),
                            "estPac", rs.getString("estPac"),
                            "nomPac", rs.getString("nomPac"),
                            "direccion", rs.getString("direccion"),
                            "fechaN", rs.getDate("fechaN"),
                            "codUni", rs.getString("codUni"));
                }
            }
        } catch (SQLException e) {
            return Map.of("existe", false, "mensaje", e.getMessage());
        }
        return Map.of("existe", false, "mensaje", "Sin resultado");
    }

    public String eliminarPaciente(String numHistoria) {

        java.sql.Connection con = BaseDatos.getConnection();  
        String function = "SELECT public.eliminarPaciente(?)";

        try (PreparedStatement ps = con.prepareStatement(function)) {
            ps.setString(1, numHistoria);
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
