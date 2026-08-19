package dao;

import db.DatabaseConnection;
import model.Livro;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LivroDAO {
    public List<Livro> listarTodos() throws SQLException {
        String sql = "SELECT l.id_livro,l.titulo,l.autor,l.ano,l.disponivel,l.editora,l.isbn," +
                "(SELECT e.data_devolucao FROM emprestimos e " +
                " WHERE e.id_livro=l.id_livro AND e.data_devolucao_real IS NULL " +
                " ORDER BY e.id_emprestimo DESC LIMIT 1) AS data_devolucao " +
                "FROM livros l ORDER BY l.id_livro";
        List<Livro> lista = new ArrayList<>();
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Livro l = new Livro(rs.getInt("id_livro"), rs.getString("titulo"), rs.getString("autor"),
                        rs.getInt("ano"), rs.getBoolean("disponivel"),
                        texto(rs.getString("editora")), texto(rs.getString("isbn")));
                Date d = rs.getDate("data_devolucao");
                if (d != null) l.setDataDevolucao(d.toLocalDate());
                lista.add(l);
            }
        }
        return lista;
    }

    public Livro inserir(String titulo, String autor, int ano, String editora, String isbn) throws SQLException {
        String sql = "INSERT INTO livros (titulo,autor,ano,disponivel,editora,isbn) VALUES (?,?,?,TRUE,?,?)";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, titulo);
            ps.setString(2, autor);
            ps.setInt(3, ano);
            ps.setString(4, texto(editora));
            if (isbn == null || isbn.isBlank()) ps.setNull(5, Types.VARCHAR); else ps.setString(5, isbn.trim());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (!rs.next()) throw new SQLException("O banco não retornou o ID do livro.");
                return new Livro(rs.getInt(1), titulo, autor, ano, true, texto(editora), texto(isbn));
            }
        }
    }

    private String texto(String valor) { return valor == null ? "" : valor; }
}
