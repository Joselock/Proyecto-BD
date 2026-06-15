package com.mycompany.gestorui.model.services.crud;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.mycompany.gestorui.model.utils.BaseDatos;

public class crudInforme {

     public String eliminarInforme(String numIn) {

        java.sql.Connection con = BaseDatos.getConnection();
        String function = "SELECT public.eliminarInforme(?)";

        try (PreparedStatement ps = con.prepareStatement(function)) {
            ps.setString(1, numIn);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString(1);
            }
        } catch (SQLException e) {
            return "Error: " + e.getMessage();
        }
        return "Error: sin respuesta";
    }

}
