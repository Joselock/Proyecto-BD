package com.mycompany.gestorui.model.services.crud;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import com.mycompany.gestorui.model.utils.BaseDatos;

public class crudUnidad {

    //Function para insertar una unidad
    public Map<String, Object> insertarUnidad(String codUni, String nomUni, String ubicacion, String codDep) {
        java.sql.Connection con = BaseDatos.getConnection();

        String function = "SELECT * FROM public.insertarUnidad(?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(function)) {
            ps.setString(1, codUni);
            ps.setString(2, nomUni);
            ps.setString(3, ubicacion);
            ps.setString(4, codDep);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Map.of(
                            "existe", rs.getBoolean("existe"),
                            "mensaje", rs.getString("mensaje"),
                            "codUni", rs.getString("codUni"),
                            "nomUni", rs.getString("nomUni"),
                            "ubiUni", rs.getString("ubiUni"),
                            "codDep", rs.getString("codDep"));
                }
            }
        } catch (SQLException e) {
            return Map.of("existe", false, "mensaje", e.getMessage());
        }

        return Map.of("existe", false, "mensaje", "Sin resultado");
    }

    
    //Funcion para modificar una unidad
    public Map<String, Object> modificarUnidad(String oldCod, String newCod, String nomUni, String ubicacion,
            String codDep) {
        java.sql.Connection con = BaseDatos.getConnection();        

        String function = "SELECT * FROM public.modificarUnidad(?, ?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(function)) {
            ps.setString(1, oldCod);
            ps.setString(2, newCod);
            ps.setString(3, nomUni);
            ps.setString(4, ubicacion);
            ps.setString(5, codDep);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Map.of(
                            "existe", rs.getBoolean("existe"),
                            "mensaje", rs.getString("mensaje"),
                            "codUni", rs.getString("codUni"),
                            "nomUni", rs.getString("nomUni"),
                            "ubiUni", rs.getString("ubiUni"),
                            "codDep", rs.getString("codDep"));
                }
            }
        } catch (SQLException e) {
            return Map.of("existe", false, "mensaje", e.getMessage());
        }

        return Map.of("existe", false, "mensaje", "Sin resultado");
    }


    //Funcion para eliminar una unidad
    public String eliminarUnidad(String codUni) {
        java.sql.Connection con = BaseDatos.getConnection();

        String function = "SELECT public.eliminarUnidad(?)";

        try (PreparedStatement ps = con.prepareStatement(function)) {
            ps.setString(1, codUni);
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
