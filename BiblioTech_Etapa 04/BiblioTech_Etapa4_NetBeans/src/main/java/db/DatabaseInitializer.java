package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public final class DatabaseInitializer {
    private DatabaseInitializer() {}

    public static void inicializar() {
        try (Connection con = DatabaseConnection.getConnection();
             Statement st = con.createStatement()) {

            st.executeUpdate("CREATE TABLE IF NOT EXISTS administradores (" +
                    "id_admin INT AUTO_INCREMENT PRIMARY KEY," +
                    "usuario VARCHAR(80) NOT NULL UNIQUE," +
                    "email VARCHAR(150) NOT NULL UNIQUE," +
                    "senha VARCHAR(255) NOT NULL" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");

            st.executeUpdate("CREATE TABLE IF NOT EXISTS livros (" +
                    "id_livro INT AUTO_INCREMENT PRIMARY KEY," +
                    "titulo VARCHAR(180) NOT NULL," +
                    "autor VARCHAR(180) NOT NULL," +
                    "ano INT NOT NULL," +
                    "disponivel BOOLEAN NOT NULL DEFAULT TRUE," +
                    "editora VARCHAR(150) DEFAULT ''," +
                    "isbn VARCHAR(40) NULL UNIQUE" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");

            st.executeUpdate("CREATE TABLE IF NOT EXISTS usuarios (" +
                    "id_usuario INT AUTO_INCREMENT PRIMARY KEY," +
                    "nome VARCHAR(160) NOT NULL," +
                    "matricula VARCHAR(30) NULL UNIQUE," +
                    "contato VARCHAR(160) NOT NULL," +
                    "telefone VARCHAR(30) DEFAULT ''," +
                    "situacao VARCHAR(20) NOT NULL DEFAULT 'Regular'" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");

            st.executeUpdate("CREATE TABLE IF NOT EXISTS emprestimos (" +
                    "id_emprestimo INT AUTO_INCREMENT PRIMARY KEY," +
                    "id_livro INT NOT NULL," +
                    "id_usuario INT NOT NULL," +
                    "data_emprestimo DATE NOT NULL," +
                    "data_devolucao DATE NOT NULL," +
                    "data_devolucao_real DATE NULL," +
                    "CONSTRAINT fk_emp_livro FOREIGN KEY (id_livro) REFERENCES livros(id_livro)," +
                    "CONSTRAINT fk_emp_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario)," +
                    "INDEX idx_emp_livro (id_livro)," +
                    "INDEX idx_emp_usuario (id_usuario)," +
                    "INDEX idx_emp_devolucao (data_devolucao, data_devolucao_real)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");

            popularSeVazio(con);

        } catch (SQLException ex) {
            throw new IllegalStateException("Falha ao preparar o banco de dados: " + ex.getMessage(), ex);
        }
    }

    private static void popularSeVazio(Connection con) throws SQLException {
        if (contar(con, "administradores") == 0) {
            try (PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO administradores (usuario,email,senha) VALUES (?,?,?)")) {
                ps.setString(1, "admin");
                ps.setString(2, "admin@bibliotech.com");
                ps.setString(3, "1234");
                ps.executeUpdate();
            }
        }

        if (contar(con, "livros") == 0) {
            String sql = "INSERT INTO livros (id_livro,titulo,autor,ano,disponivel,editora,isbn) VALUES (?,?,?,?,?,?,?)";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                livro(ps,1001,"O Senhor dos Anéis","J.R.R. Tolkien",1954,true,"HarperCollins","978-0007525546");
                livro(ps,1002,"Harry Potter","J.K. Rowling",1997,true,"Rocco","978-8532511010");
                livro(ps,1003,"1984","George Orwell",1949,true,"Companhia das Letras","978-8535914849");
                livro(ps,1004,"O Pequeno Príncipe","Antoine de Saint-Exupéry",1943,true,"Agir","978-8522005239");
                livro(ps,1005,"Dom Casmurro","Machado de Assis",1899,true,"Ática","978-8508142567");
                livro(ps,1006,"A Revolução dos Bichos","George Orwell",1945,true,"Companhia das Letras","978-8535909555");
                livro(ps,1007,"Cem Anos de Solidão","Gabriel García Márquez",1967,true,"Record","978-8501012078");
                livro(ps,1008,"O Código Da Vinci","Dan Brown",2003,true,"Sextante","978-8599296158");
            }
        }

        if (contar(con, "usuarios") == 0) {
            String sql = "INSERT INTO usuarios (id_usuario,nome,matricula,contato,telefone,situacao) VALUES (?,?,?,?,?,?)";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                usuario(ps,2101,"João Silva","2026001","joao.silva@email.com","(11) 99999-1111","Pendente");
                usuario(ps,2102,"Maria Souza","2026002","maria.souza@email.com","(11) 98765-4321","Regular");
                usuario(ps,2103,"Carlos Oliveira","2026003","carlos.oliveira@email.com","(21) 98888-3333","Bloqueado");
                usuario(ps,2104,"Ana Santos","2026004","ana.santos@email.com","(21) 91234-5678","Regular");
                usuario(ps,2105,"Pedro Costa","2026005","pedro.costa@email.com","(85) 97777-5555","Regular");
            }
        }

        if (contar(con, "emprestimos") == 0) {
            con.setAutoCommit(false);
            try {
                try (Statement st = con.createStatement()) {
                    st.executeUpdate("INSERT INTO emprestimos (id_emprestimo,id_livro,id_usuario,data_emprestimo,data_devolucao,data_devolucao_real) VALUES " +
                            "(1,1005,2101,DATE_SUB(CURDATE(), INTERVAL 20 DAY),DATE_SUB(CURDATE(), INTERVAL 5 DAY),NULL)," +
                            "(2,1008,2102,DATE_SUB(CURDATE(), INTERVAL 4 DAY),DATE_ADD(CURDATE(), INTERVAL 10 DAY),NULL)," +
                            "(3,1002,2103,DATE_SUB(CURDATE(), INTERVAL 18 DAY),DATE_SUB(CURDATE(), INTERVAL 3 DAY),NULL)");
                    st.executeUpdate("UPDATE livros SET disponivel=FALSE WHERE id_livro IN (1002,1005,1008)");
                }
                con.commit();
            } catch (SQLException ex) {
                con.rollback();
                throw ex;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    private static int contar(Connection con, String tabela) throws SQLException {
        try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM " + tabela)) {
            rs.next();
            return rs.getInt(1);
        }
    }

    private static void livro(PreparedStatement ps, int id, String titulo, String autor, int ano,
                              boolean disponivel, String editora, String isbn) throws SQLException {
        ps.setInt(1,id); ps.setString(2,titulo); ps.setString(3,autor); ps.setInt(4,ano);
        ps.setBoolean(5,disponivel); ps.setString(6,editora); ps.setString(7,isbn);
        ps.executeUpdate();
    }

    private static void usuario(PreparedStatement ps, int id, String nome, String matricula,
                                String contato, String telefone, String situacao) throws SQLException {
        ps.setInt(1,id); ps.setString(2,nome); ps.setString(3,matricula);
        ps.setString(4,contato); ps.setString(5,telefone); ps.setString(6,situacao);
        ps.executeUpdate();
    }
}
