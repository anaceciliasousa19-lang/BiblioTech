package view;

import model.Livro;
import service.BibliotecaService;
import util.UIUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.time.format.DateTimeFormatter;

public class AcervoPanel extends JPanel {
    private final BibliotecaService service;
    private final DefaultTableModel modelo;
    private final JTable tabela;
    private final JTextField txtBusca = UIUtils.campo();
    private final TableRowSorter<DefaultTableModel> sorter;
    private Runnable onDadosAlterados;

    public AcervoPanel(BibliotecaService service) {
        this.service = service;
        setLayout(new BorderLayout(0, 18));
        setBackground(UIUtils.FUNDO);
        setBorder(BorderFactory.createEmptyBorder(28, 32, 28, 32));

        JPanel cabecalho = new JPanel(new BorderLayout(15, 0));
        cabecalho.setOpaque(false);
        cabecalho.add(UIUtils.titulo("Acervo de Livros"), BorderLayout.WEST);
        JButton adicionar = UIUtils.botaoPrimario("+  Adicionar Livro");
        cabecalho.add(adicionar, BorderLayout.EAST);

        JPanel topo = new JPanel();
        topo.setOpaque(false);
        topo.setLayout(new BoxLayout(topo, BoxLayout.Y_AXIS));
        topo.add(cabecalho);
        topo.add(Box.createVerticalStrut(16));

        JPanel busca = new JPanel(new BorderLayout(10, 0));
        busca.setOpaque(false);
        JLabel lbl = new JLabel("Pesquisar:");
        lbl.setFont(new Font("SansSerif", Font.BOLD, 13));
        txtBusca.setToolTipText("Pesquise por título, autor, ID ou ISBN");
        busca.add(lbl, BorderLayout.WEST);
        busca.add(txtBusca, BorderLayout.CENTER);
        topo.add(busca);
        add(topo, BorderLayout.NORTH);

        modelo = new DefaultTableModel(new Object[]{"ID", "Título", "Autor", "Ano", "Status", "Data Devolução", "ISBN"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tabela = new JTable(modelo);
        UIUtils.configurarTabela(tabela);
        sorter = new TableRowSorter<>(modelo);
        tabela.setRowSorter(sorter);

        JPanel tabelaPainel = UIUtils.painelBranco();
        tabelaPainel.setLayout(new BorderLayout());
        JLabel t = new JLabel("Lista Completa de Livros");
        t.setFont(new Font("SansSerif", Font.BOLD, 16));
        t.setBorder(BorderFactory.createEmptyBorder(14, 16, 12, 16));
        tabelaPainel.add(t, BorderLayout.NORTH);
        tabelaPainel.add(new JScrollPane(tabela), BorderLayout.CENTER);
        add(tabelaPainel, BorderLayout.CENTER);

        adicionar.addActionListener(e -> {
            LivroDialog d = new LivroDialog(SwingUtilities.getWindowAncestor(this), service);
            d.setVisible(true);
            if (d.isSalvo()) {
                atualizar();
                if (onDadosAlterados != null) onDadosAlterados.run();
            }
        });

        txtBusca.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filtrar(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filtrar(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filtrar(); }
        });
        atualizar();
    }

    private void filtrar() {
        String texto = txtBusca.getText().trim();
        sorter.setRowFilter(texto.isEmpty() ? null : RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(texto)));
    }

    public void pesquisar(String texto) {
        txtBusca.setText(texto == null ? "" : texto);
        filtrar();
    }

    public void atualizar() {
        modelo.setRowCount(0);
        DateTimeFormatter f = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        for (Livro l : service.getLivros()) {
            modelo.addRow(new Object[]{
                    l.getIdLivro(), l.getTitulo(), l.getAutor(), l.getAno(),
                    l.isDisponivel() ? "Disponível" : "Emprestado",
                    l.getDataDevolucao() == null ? "—" : l.getDataDevolucao().format(f),
                    l.getIsbn().isEmpty() ? "—" : l.getIsbn()
            });
        }
    }

    public void setOnDadosAlterados(Runnable r) { this.onDadosAlterados = r; }
}
