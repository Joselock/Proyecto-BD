package com.mycompany.gestorui.model.services.crud;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.mycompany.gestorui.model.entidades.Unidad;
import com.mycompany.gestorui.model.utils.BaseDatos;

public class crudUnidad {

    public static List<Unidad> obtenerUnidades() {
        List<Unidad> unidades = new ArrayList<>();
        String sql = "SELECT * FROM public.\"Unidad\" ORDER BY \"codUni\"";
        
        java.sql.Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            con = BaseDatos.getConnection();
            if (con == null) {
                System.err.println("Error: No se pudo conectar a la base de datos");
                return unidades;
            }
            
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                Unidad unidad = new Unidad(null, null, null, null);
                unidad.setCodigoUni(rs.getString("codUni"));
                unidad.setNombreUni(rs.getString("nomUni"));
                unidad.setUbicacion(rs.getString("ubiUni"));
                unidad.setCodigoDep(rs.getString("codDep"));
                unidades.add(unidad);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener unidades: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        return unidades;
    }

    //Function para insertar una unidad
    public static Map<String, Object> insertarUnidad(String codUni, String nomUni, String ubicacion, String codDep) {
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
    public static Map<String, Object> modificarUnidad(String oldCod, String newCod, String nomUni, String ubicacion,
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
    public static String eliminarUnidad(String codUni) {
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
