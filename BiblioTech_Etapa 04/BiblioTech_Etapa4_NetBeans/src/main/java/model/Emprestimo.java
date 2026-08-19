package model;

import java.time.LocalDate;

public class Emprestimo {
    private int idEmprestimo;
    private Livro livro;
    private Usuario usuario;
    private LocalDate dataEmprestimo;
    private LocalDate dataDevolucao;
    private LocalDate dataDevolucaoReal;

    public Emprestimo(int idEmprestimo, Livro livro, Usuario usuario) {
        this(idEmprestimo, livro, usuario, LocalDate.now(), LocalDate.now().plusDays(7));
    }

    public Emprestimo(int idEmprestimo, Livro livro, Usuario usuario,
                      LocalDate dataEmprestimo, LocalDate dataDevolucao) {
        this.idEmprestimo = idEmprestimo;
        this.livro = livro;
        this.usuario = usuario;
        this.dataEmprestimo = dataEmprestimo;
        this.dataDevolucao = dataDevolucao;
    }

    public boolean registrarEmprestimo() {
        if (livro != null && livro.isDisponivel()) {
            livro.setDisponivel(false);
            livro.setDataDevolucao(dataDevolucao);
            return true;
        }
        return false;
    }

    public void registrarDevolucao() {
        dataDevolucaoReal = LocalDate.now();
        if (livro != null) {
            livro.setDisponivel(true);
            livro.setDataDevolucao(null);
        }
    }

    public boolean isAtivo() {
        return dataDevolucaoReal == null;
    }

    public boolean isAtrasado() {
        return isAtivo() && dataDevolucao.isBefore(LocalDate.now());
    }

    public String getStatus() {
        if (!isAtivo()) return "Devolvido";
        return isAtrasado() ? "Atrasado" : "Ativo";
    }

    public int getIdEmprestimo() { return idEmprestimo; }
    public void setIdEmprestimo(int idEmprestimo) { this.idEmprestimo = idEmprestimo; }
    public Livro getLivro() { return livro; }
    public void setLivro(Livro livro) { this.livro = livro; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public LocalDate getDataEmprestimo() { return dataEmprestimo; }
    public void setDataEmprestimo(LocalDate dataEmprestimo) { this.dataEmprestimo = dataEmprestimo; }
    public LocalDate getDataDevolucao() { return dataDevolucao; }
    public void setDataDevolucao(LocalDate dataDevolucao) { this.dataDevolucao = dataDevolucao; }
    public LocalDate getDataDevolucaoReal() { return dataDevolucaoReal; }
    public void setDataDevolucaoReal(LocalDate dataDevolucaoReal) { this.dataDevolucaoReal = dataDevolucaoReal; }
}
