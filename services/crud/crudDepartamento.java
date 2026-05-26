package services.crud;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import utils.BaseDatos;

public class crudDepartamento {

    //Funcion para insertar un departamento
    public Map<String, Object> insertarDepartamento(String codDep, String nomDep, String codHos) {
        java.sql.Connection con = BaseDatos.getConnection();
        
        String function = "SELECT * FROM public.insertarDepartamento(?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(function)) {
            ps.setString(1, codDep);
            ps.setString(2, nomDep);
            ps.setString(3, codHos);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Map.of(
                        "existe", rs.getBoolean("existe"),
                        "mensaje", rs.getString("mensaje"),
                        "codDep", rs.getString("codDep"),
                        "nomDep", rs.getString("nomDep"),
                        "codHos", rs.getString("codHos")
                    );
                }
            }
        } catch (SQLException e) {
            return Map.of("existe", false, "mensaje", e.getMessage());
        }

        return Map.of("existe", false, "mensaje", "Sin resultado");
    }

    
    //Funcion para modificar un departamento
    public Map<String, Object> modificarDepartamento(String oldCod, String newCod, String nomDep, String codHos) {
        java.sql.Connection con = BaseDatos.getConnection();

        String function = "SELECT * FROM public.modificarDepartamento(?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(function)) {
            ps.setString(1, oldCod);
            ps.setString(2, newCod);
            ps.setString(3, nomDep);
            ps.setString(4, codHos);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Map.of(
                        "existe", rs.getBoolean("existe"),
                        "mensaje", rs.getString("mensaje"),
                        "codDep", rs.getString("codDep"),
                        "nomDep", rs.getString("nomDep"),
                        "codHos", rs.getString("codHos")
                    );
                }
            }
        } catch (SQLException e) {
            return Map.of("existe", false, "mensaje", e.getMessage());
        }

        return Map.of("existe", false, "mensaje", "Sin resultado");
    }


    //Funcion para eliminar un departamento
    public String eliminarDepartamento(String codDep) {
        java.sql.Connection con = BaseDatos.getConnection();

        String function = "SELECT public.eliminarDepartamento(?)";

        try (PreparedStatement ps = con.prepareStatement(function)) {
            ps.setString(1, codDep);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString(1);
            }
        } catch (SQLException e) {
            return "Error: " + e.getMessage();
        }
        
        return "Error: sin respuesta";
    }

}
