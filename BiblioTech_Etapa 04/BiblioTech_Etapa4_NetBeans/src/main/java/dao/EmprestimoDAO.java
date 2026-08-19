package dao;

import db.DatabaseConnection;
import model.Emprestimo;
import model.Livro;
import model.Usuario;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EmprestimoDAO {
    public List<Emprestimo> listarTodos() throws SQLException {
        String sql = "SELECT e.id_emprestimo,e.data_emprestimo,e.data_devolucao,e.data_devolucao_real," +
                "l.id_livro,l.titulo,l.autor,l.ano,l.disponivel,l.editora,l.isbn," +
                "u.id_usuario,u.nome,u.matricula,u.contato,u.telefone,u.situacao," +
                "(SELECT COUNT(*) FROM emprestimos ep WHERE ep.id_usuario=u.id_usuario AND ep.data_devolucao_real IS NULL) livros_em_posse " +
                "FROM emprestimos e JOIN livros l ON l.id_livro=e.id_livro " +
                "JOIN usuarios u ON u.id_usuario=e.id_usuario ORDER BY e.id_emprestimo DESC";
        List<Emprestimo> lista = new ArrayList<>();
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Livro l = new Livro(rs.getInt("id_livro"),rs.getString("titulo"),rs.getString("autor"),
                        rs.getInt("ano"),rs.getBoolean("disponivel"),texto(rs.getString("editora")),texto(rs.getString("isbn")));
                Usuario u = new Usuario(rs.getInt("id_usuario"),rs.getString("nome"),texto(rs.getString("matricula")),
                        rs.getString("contato"),texto(rs.getString("telefone")),rs.getString("situacao"),rs.getInt("livros_em_posse"));
                Emprestimo e = new Emprestimo(rs.getInt("id_emprestimo"), l, u,
                        rs.getDate("data_emprestimo").toLocalDate(), rs.getDate("data_devolucao").toLocalDate());
                Date real = rs.getDate("data_devolucao_real");
                if (real != null) e.setDataDevolucaoReal(real.toLocalDate());
                if (e.isAtivo()) l.setDataDevolucao(e.getDataDevolucao());
                lista.add(e);
            }
        }
        return lista;
    }

    public Emprestimo registrar(int idLivro, int idUsuario, LocalDate devolucao) throws SQLException {
        try (Connection con = DatabaseConnection.getConnection()) {
            con.setAutoCommit(false);
            try {
                Livro livro;
                try (PreparedStatement ps = con.prepareStatement(
                        "SELECT id_livro,titulo,autor,ano,disponivel,editora,isbn FROM livros WHERE id_livro=? FOR UPDATE")) {
                    ps.setInt(1,idLivro);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) throw new IllegalArgumentException("Livro não encontrado.");
                        if (!rs.getBoolean("disponivel")) throw new IllegalStateException("O livro selecionado não está disponível.");
                        livro = new Livro(rs.getInt("id_livro"),rs.getString("titulo"),rs.getString("autor"),
                                rs.getInt("ano"),true,texto(rs.getString("editora")),texto(rs.getString("isbn")));
                    }
                }

                Usuario usuario;
                try (PreparedStatement ps = con.prepareStatement(
                        "SELECT id_usuario,nome,matricula,contato,telefone,situacao FROM usuarios WHERE id_usuario=? FOR UPDATE")) {
                    ps.setInt(1,idUsuario);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) throw new IllegalArgumentException("Leitor não encontrado.");
                        if ("Bloqueado".equalsIgnoreCase(rs.getString("situacao")))
                            throw new IllegalStateException("O leitor selecionado está bloqueado.");
                        usuario = new Usuario(rs.getInt("id_usuario"),rs.getString("nome"),texto(rs.getString("matricula")),
                                rs.getString("contato"),texto(rs.getString("telefone")),rs.getString("situacao"),0);
                    }
                }

                int id;
                try (PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO emprestimos (id_livro,id_usuario,data_emprestimo,data_devolucao,data_devolucao_real) VALUES (?,?,CURDATE(),?,NULL)",
                        Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1,idLivro); ps.setInt(2,idUsuario); ps.setDate(3,Date.valueOf(devolucao));
                    ps.executeUpdate();
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (!rs.next()) throw new SQLException("ID do empréstimo não retornado.");
                        id = rs.getInt(1);
                    }
                }
                try (PreparedStatement ps = con.prepareStatement("UPDATE livros SET disponivel=FALSE WHERE id_livro=?")) {
                    ps.setInt(1,idLivro); ps.executeUpdate();
                }
                con.commit();
                livro.setDisponivel(false);
                livro.setDataDevolucao(devolucao);
                return new Emprestimo(id,livro,usuario,LocalDate.now(),devolucao);
            } catch (SQLException | RuntimeException ex) {
                con.rollback();
                throw ex;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    public void registrarDevolucao(int idEmprestimo) throws SQLException {
        try (Connection con = DatabaseConnection.getConnection()) {
            con.setAutoCommit(false);
            try {
                int idLivro;
                int idUsuario;
                Date real;
                try (PreparedStatement ps = con.prepareStatement(
                        "SELECT id_livro,id_usuario,data_devolucao_real FROM emprestimos WHERE id_emprestimo=? FOR UPDATE")) {
                    ps.setInt(1,idEmprestimo);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) throw new IllegalArgumentException("Empréstimo não encontrado.");
                        idLivro = rs.getInt("id_livro");
                        idUsuario = rs.getInt("id_usuario");
                        real = rs.getDate("data_devolucao_real");
                    }
                }
                if (real != null) throw new IllegalStateException("Esse empréstimo já foi finalizado.");

                try (PreparedStatement ps = con.prepareStatement(
                        "UPDATE emprestimos SET data_devolucao_real=CURDATE() WHERE id_emprestimo=?")) {
                    ps.setInt(1,idEmprestimo); ps.executeUpdate();
                }
                try (PreparedStatement ps = con.prepareStatement("UPDATE livros SET disponivel=TRUE WHERE id_livro=?")) {
                    ps.setInt(1,idLivro); ps.executeUpdate();
                }

                // Se não restar atraso ativo, remove situação Pendente; Bloqueado é mantido.
                try (PreparedStatement ps = con.prepareStatement(
                        "UPDATE usuarios u SET situacao='Regular' WHERE u.id_usuario=? AND u.situacao='Pendente' " +
                        "AND NOT EXISTS (SELECT 1 FROM emprestimos e WHERE e.id_usuario=u.id_usuario " +
                        "AND e.data_devolucao_real IS NULL AND e.data_devolucao<CURDATE())")) {
                    ps.setInt(1,idUsuario); ps.executeUpdate();
                }
                con.commit();
            } catch (SQLException | RuntimeException ex) {
                con.rollback();
                throw ex;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    private String texto(String valor) { return valor == null ? "" : valor; }
}
