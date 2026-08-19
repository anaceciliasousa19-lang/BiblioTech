package service;

import dao.AuthDAO;
import dao.EmprestimoDAO;
import dao.LivroDAO;
import dao.UsuarioDAO;
import model.Emprestimo;
import model.Livro;
import model.Usuario;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class BibliotecaService {
    private final LivroDAO livroDAO = new LivroDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final EmprestimoDAO emprestimoDAO = new EmprestimoDAO();
    private final AuthDAO authDAO = new AuthDAO();

    public boolean autenticar(String usuario, String senha) {
        try {
            return authDAO.autenticar(usuario, senha);
        } catch (SQLException ex) {
            throw falhaBanco(ex);
        }
    }

    public List<Livro> getLivros() {
        try { return livroDAO.listarTodos(); }
        catch (SQLException ex) { throw falhaBanco(ex); }
    }

    public List<Usuario> getUsuarios() {
        try { return usuarioDAO.listarTodos(); }
        catch (SQLException ex) { throw falhaBanco(ex); }
    }

    public List<Emprestimo> getEmprestimos() {
        try { return emprestimoDAO.listarTodos(); }
        catch (SQLException ex) { throw falhaBanco(ex); }
    }

    public Livro adicionarLivro(String titulo, String autor, int ano, String editora, String isbn) {
        try {
            return livroDAO.inserir(titulo.trim(), autor.trim(), ano,
                    editora == null ? "" : editora.trim(), isbn == null ? "" : isbn.trim());
        } catch (SQLException ex) {
            if ("23000".equals(ex.getSQLState()))
                throw new IllegalArgumentException("Já existe um livro com esse ISBN.");
            throw falhaBanco(ex);
        }
    }

    public Usuario adicionarUsuario(String nome, String email, String telefone) {
        try {
            return usuarioDAO.inserir(nome.trim(), email.trim(), telefone == null ? "" : telefone.trim());
        } catch (SQLException ex) {
            if ("23000".equals(ex.getSQLState()))
                throw new IllegalArgumentException("Já existe um leitor com esse e-mail ou matrícula.");
            throw falhaBanco(ex);
        }
    }

    public Emprestimo registrarEmprestimo(Livro livro, Usuario usuario, LocalDate dataDevolucao) {
        if (livro == null || usuario == null) throw new IllegalArgumentException("Selecione um livro e um leitor.");
        if (dataDevolucao == null || !dataDevolucao.isAfter(LocalDate.now()))
            throw new IllegalArgumentException("A data de devolução deve ser posterior à data atual.");
        try { return emprestimoDAO.registrar(livro.getIdLivro(), usuario.getIdUsuario(), dataDevolucao); }
        catch (SQLException ex) { throw falhaBanco(ex); }
    }

    public void registrarDevolucao(Emprestimo emprestimo) {
        if (emprestimo == null) throw new IllegalArgumentException("Selecione um empréstimo.");
        try { emprestimoDAO.registrarDevolucao(emprestimo.getIdEmprestimo()); }
        catch (SQLException ex) { throw falhaBanco(ex); }
    }

    public Optional<Livro> buscarLivroPorTitulo(String titulo) {
        return getLivros().stream().filter(l -> l.getTitulo().equalsIgnoreCase(titulo)).findFirst();
    }

    public long totalEmprestimosAtivos() { return getEmprestimos().stream().filter(Emprestimo::isAtivo).count(); }
    public long totalAtrasados() { return getEmprestimos().stream().filter(Emprestimo::isAtrasado).count(); }

    private IllegalStateException falhaBanco(SQLException ex) {
        return new IllegalStateException("Falha de acesso ao banco de dados. " + ex.getMessage(), ex);
    }
}
