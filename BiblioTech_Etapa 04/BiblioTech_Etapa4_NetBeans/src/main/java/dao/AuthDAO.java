package dao;

import db.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthDAO {
    public boolean autenticar(String usuarioOuEmail, String senha) throws SQLException {
        String sql = "SELECT 1 FROM administradores " +
                "WHERE (LOWER(usuario)=LOWER(?) OR LOWER(email)=LOWER(?)) AND senha=? LIMIT 1";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, usuarioOuEmail);
            ps.setString(2, usuarioOuEmail);
            ps.setString(3, senha);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
}
