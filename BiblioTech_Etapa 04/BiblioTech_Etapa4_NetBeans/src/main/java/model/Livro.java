package model;

import java.time.LocalDate;

public class Livro {
    private int idLivro;
    private String titulo;
    private String autor;
    private int ano;
    private boolean disponivel;
    private String editora;
    private String isbn;
    private LocalDate dataDevolucao;

    public Livro(int idLivro, String titulo, String autor, int ano, boolean disponivel) {
        this(idLivro, titulo, autor, ano, disponivel, "", "");
    }

    public Livro(int idLivro, String titulo, String autor, int ano, boolean disponivel,
                 String editora, String isbn) {
        this.idLivro = idLivro;
        this.titulo = titulo;
        this.autor = autor;
        this.ano = ano;
        this.disponivel = disponivel;
        this.editora = editora;
        this.isbn = isbn;
    }

    public int getIdLivro() { return idLivro; }
    public void setIdLivro(int idLivro) { this.idLivro = idLivro; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }
    public int getAno() { return ano; }
    public void setAno(int ano) { this.ano = ano; }
    public boolean isDisponivel() { return disponivel; }
    public void setDisponivel(boolean disponivel) { this.disponivel = disponivel; }
    public String getEditora() { return editora; }
    public void setEditora(String editora) { this.editora = editora; }
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public LocalDate getDataDevolucao() { return dataDevolucao; }
    public void setDataDevolucao(LocalDate dataDevolucao) { this.dataDevolucao = dataDevolucao; }

    @Override
    public String toString() {
        return titulo + " — " + autor;
    }
}
