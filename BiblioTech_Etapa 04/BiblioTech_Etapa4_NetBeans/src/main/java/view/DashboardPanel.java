package view;

import model.Emprestimo;
import service.BibliotecaService;
import util.UIUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class DashboardPanel extends JPanel {
    private final BibliotecaService service;
    private final JLabel lblLivros = new JLabel();
    private final JLabel lblEmprestimos = new JLabel();
    private final JLabel lblAtrasados = new JLabel();
    private final DefaultTableModel modelo;

    public DashboardPanel(BibliotecaService service) {
        this.service = service;
        setLayout(new BorderLayout(0, 22));
        setBackground(UIUtils.FUNDO);
        setBorder(BorderFactory.createEmptyBorder(28, 32, 28, 32));

        JLabel bemVinda = new JLabel("BEM-VINDA, ANA CECÍLIA!");
        bemVinda.setFont(new Font("SansSerif", Font.BOLD, 24));
        bemVinda.setForeground(UIUtils.TEXTO);

        JPanel topo = new JPanel();
        topo.setOpaque(false);
        topo.setLayout(new BoxLayout(topo, BoxLayout.Y_AXIS));
        topo.add(bemVinda);
        topo.add(Box.createVerticalStrut(22));

        JPanel cards = new JPanel(new GridLayout(1, 3, 18, 0));
        cards.setOpaque(false);
        cards.add(criarCard("LIVROS TOTAIS", lblLivros, UIUtils.AZUL));
        cards.add(criarCard("EMPRÉSTIMOS", lblEmprestimos, UIUtils.VERDE));
        cards.add(criarCard("ATRASADOS ⚠", lblAtrasados, UIUtils.LARANJA));
        topo.add(cards);
        add(topo, BorderLayout.NORTH);

        modelo = new DefaultTableModel(new Object[]{"Livro", "Leitor", "Devolução", "Status"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable tabela = new JTable(modelo);
        UIUtils.configurarTabela(tabela);

        JPanel tabelaPainel = UIUtils.painelBranco();
        tabelaPainel.setLayout(new BorderLayout());
        JLabel titulo = new JLabel("Livros Emprestados Recentemente");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 16));
        titulo.setBorder(BorderFactory.createEmptyBorder(16, 16, 12, 16));
        tabelaPainel.add(titulo, BorderLayout.NORTH);
        tabelaPainel.add(new JScrollPane(tabela), BorderLayout.CENTER);
        add(tabelaPainel, BorderLayout.CENTER);
        atualizar();
    }

    private JPanel criarCard(String titulo, JLabel valor, Color corValor) {
        JPanel card = UIUtils.painelBranco();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(229, 231, 235)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));
        JLabel t = new JLabel(titulo);
        t.setForeground(UIUtils.CINZA);
        t.setFont(new Font("SansSerif", Font.BOLD, 12));
        valor.setFont(new Font("SansSerif", Font.BOLD, 34));
        valor.setForeground(corValor);
        t.setAlignmentX(Component.LEFT_ALIGNMENT);
        valor.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(t);
        card.add(Box.createVerticalStrut(8));
        card.add(valor);
        return card;
    }

    public void atualizar() {
        lblLivros.setText(String.valueOf(service.getLivros().size()));
        lblEmprestimos.setText(String.valueOf(service.totalEmprestimosAtivos()));
        lblAtrasados.setText(String.valueOf(service.totalAtrasados()));
        modelo.setRowCount(0);
        DateTimeFormatter f = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        List<Emprestimo> recentes = service.getEmprestimos().stream()
                .filter(Emprestimo::isAtivo)
                .sorted((a, b) -> b.getDataEmprestimo().compareTo(a.getDataEmprestimo()))
                .limit(8)
                .collect(Collectors.toList());
        for (Emprestimo e : recentes) {
            modelo.addRow(new Object[]{
                    e.getLivro().getTitulo(),
                    e.getUsuario().getNome(),
                    e.getDataDevolucao().format(f),
                    e.getStatus()
            });
        }
    }
}
