package util;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public final class UIUtils {
    public static final Color AZUL = new Color(26, 35, 126);
    public static final Color AZUL_CLARO = new Color(40, 53, 147);
    public static final Color FUNDO = new Color(245, 247, 250);
    public static final Color TEXTO = new Color(45, 52, 54);
    public static final Color CINZA = new Color(108, 117, 125);
    public static final Color BORDA = new Color(209, 213, 219);
    public static final Color VERDE = new Color(22, 163, 74);
    public static final Color LARANJA = new Color(217, 119, 6);
    public static final Color VERMELHO = new Color(220, 38, 38);

    private UIUtils() {}

    public static JButton botaoPrimario(String texto) {
        JButton b = new JButton(texto);
        b.setBackground(AZUL);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setFont(new Font("SansSerif", Font.BOLD, 14));
        b.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    public static JButton botaoSucesso(String texto) {
        JButton b = botaoPrimario(texto);
        b.setBackground(VERDE);
        return b;
    }

    public static JButton botaoSecundario(String texto) {
        JButton b = new JButton(texto);
        b.setBackground(Color.WHITE);
        b.setForeground(TEXTO);
        b.setFocusPainted(false);
        b.setFont(new Font("SansSerif", Font.PLAIN, 14));
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDA),
                BorderFactory.createEmptyBorder(9, 14, 9, 14)));
        return b;
    }

    public static JLabel titulo(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font("SansSerif", Font.BOLD, 24));
        l.setForeground(TEXTO);
        return l;
    }

    public static JTextField campo() {
        JTextField t = new JTextField();
        t.setFont(new Font("SansSerif", Font.PLAIN, 14));
        t.setBorder(campoBorda());
        t.setPreferredSize(new Dimension(250, 40));
        return t;
    }

    public static JPasswordField senha() {
        JPasswordField t = new JPasswordField();
        t.setFont(new Font("SansSerif", Font.PLAIN, 14));
        t.setBorder(campoBorda());
        t.setPreferredSize(new Dimension(250, 40));
        return t;
    }

    private static Border campoBorda() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDA),
                BorderFactory.createEmptyBorder(8, 10, 8, 10));
    }

    public static void configurarTabela(JTable tabela) {
        tabela.setRowHeight(30);
        tabela.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tabela.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        tabela.getTableHeader().setBackground(new Color(249, 250, 251));
        tabela.getTableHeader().setForeground(TEXTO);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.setShowVerticalLines(false);
        tabela.setGridColor(new Color(229, 231, 235));
    }

    public static JPanel painelBranco() {
        JPanel p = new JPanel();
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createLineBorder(new Color(229, 231, 235)));
        return p;
    }

    public static void erro(Component parent, String mensagem) {
        JOptionPane.showMessageDialog(parent, mensagem, "Atenção", JOptionPane.WARNING_MESSAGE);
    }

    public static void sucesso(Component parent, String mensagem) {
        JOptionPane.showMessageDialog(parent, mensagem, "BiblioTech", JOptionPane.INFORMATION_MESSAGE);
    }
}
