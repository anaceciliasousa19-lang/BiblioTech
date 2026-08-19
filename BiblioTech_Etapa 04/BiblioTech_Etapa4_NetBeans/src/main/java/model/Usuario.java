package model;

public class Usuario {
    private int idUsuario;
    private String nome;
    private String matricula;
    private String contato;
    private String telefone;
    private String situacao;
    private int livrosEmPosse;

    public Usuario(int idUsuario, String nome, String matricula, String contato) {
        this(idUsuario, nome, matricula, contato, "", "Regular", 0);
    }

    public Usuario(int idUsuario, String nome, String matricula, String contato,
                   String telefone, String situacao, int livrosEmPosse) {
        this.idUsuario = idUsuario;
        this.nome = nome;
        this.matricula = matricula;
        this.contato = contato;
        this.telefone = telefone;
        this.situacao = situacao;
        this.livrosEmPosse = livrosEmPosse;
    }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getMatricula() { return matricula; }
    public void setMatricula(String matricula) { this.matricula = matricula; }
    public String getContato() { return contato; }
    public void setContato(String contato) { this.contato = contato; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public String getSituacao() { return situacao; }
    public void setSituacao(String situacao) { this.situacao = situacao; }
    public int getLivrosEmPosse() { return livrosEmPosse; }
    public void setLivrosEmPosse(int livrosEmPosse) { this.livrosEmPosse = livrosEmPosse; }

    @Override
    public String toString() {
        return nome;
    }
}
