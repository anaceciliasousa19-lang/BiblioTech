package dao;

import db.DatabaseConnection;
import model.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {
    public List<Usuario> listarTodos() throws SQLException {
        String sql = "SELECT u.id_usuario,u.nome,u.matricula,u.contato,u.telefone," +
                "CASE WHEN u.situacao='Bloqueado' THEN 'Bloqueado' " +
                "WHEN EXISTS (SELECT 1 FROM emprestimos ea WHERE ea.id_usuario=u.id_usuario " +
                " AND ea.data_devolucao_real IS NULL AND ea.data_devolucao < CURDATE()) THEN 'Pendente' " +
                "ELSE 'Regular' END AS situacao_exibida," +
                "(SELECT COUNT(*) FROM emprestimos e WHERE e.id_usuario=u.id_usuario AND e.data_devolucao_real IS NULL) AS livros_em_posse " +
                "FROM usuarios u ORDER BY u.id_usuario";
        List<Usuario> lista = new ArrayList<>();
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Usuario(rs.getInt("id_usuario"), rs.getString("nome"),
                        texto(rs.getString("matricula")), rs.getString("contato"),
                        texto(rs.getString("telefone")), rs.getString("situacao_exibida"),
                        rs.getInt("livros_em_posse")));
            }
        }
        return lista;
    }

    public Usuario inserir(String nome, String email, String telefone) throws SQLException {
        String insert = "INSERT INTO usuarios (nome,matricula,contato,telefone,situacao) VALUES (?,NULL,?,?,'Regular')";
        try (Connection con = DatabaseConnection.getConnection()) {
            con.setAutoCommit(false);
            try (PreparedStatement ps = con.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, nome);
                ps.setString(2, email);
                ps.setString(3, texto(telefone));
                ps.executeUpdate();
                int id;
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (!rs.next()) throw new SQLException("O banco não retornou o ID do leitor.");
                    id = rs.getInt(1);
                }
                String matricula = "2026" + String.format("%03d", Math.max(1, id - 2100));
                try (PreparedStatement up = con.prepareStatement("UPDATE usuarios SET matricula=? WHERE id_usuario=?")) {
                    up.setString(1, matricula);
                    up.setInt(2, id);
                    up.executeUpdate();
                }
                con.commit();
                return new Usuario(id, nome, matricula, email, texto(telefone), "Regular", 0);
            } catch (SQLException ex) {
                con.rollback();
                throw ex;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    private String texto(String valor) { return valor == null ? "" : valor; }
}
