package app;

import db.DatabaseInitializer;
import service.BibliotecaService;
import view.TelaLogin;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            UIManager.put("OptionPane.messageFont", new Font("SansSerif", Font.PLAIN, 14));
            UIManager.put("Button.font", new Font("SansSerif", Font.PLAIN, 14));

            try {
                DatabaseInitializer.inicializar();
                BibliotecaService service = new BibliotecaService();
                new TelaLogin(service).setVisible(true);
            } catch (Throwable ex) {
                String detalhe = ex.getMessage() == null ? ex.getClass().getSimpleName() : ex.getMessage();
                JOptionPane.showMessageDialog(null,
                        "Não foi possível conectar/preparar o MySQL.\\n\\n" +
                        "1. Abra o MySQL Workbench e execute banco/bibliotech.sql.\\n" +
                        "2. Confira usuário e senha em src/main/resources/database.properties.\\n" +
                        "3. Verifique se o MySQL Server está iniciado.\\n\\n" +
                        "Detalhe: " + detalhe,
                        "BiblioTech - Banco de dados",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
