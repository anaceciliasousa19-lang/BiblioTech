package view;

import model.Usuario;
import service.BibliotecaService;
import util.UIUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.regex.Pattern;

public class LeitoresPanel extends JPanel {
    private final BibliotecaService service;
    private final DefaultTableModel modelo;
    private final TableRowSorter<DefaultTableModel> sorter;
    private final JTextField txtBusca = UIUtils.campo();
    private String filtroSituacao = "Todos";
    private Runnable onDadosAlterados;

    public LeitoresPanel(BibliotecaService service) {
        this.service = service;
        setLayout(new BorderLayout(0, 18));
        setBackground(UIUtils.FUNDO);
        setBorder(BorderFactory.createEmptyBorder(28, 32, 28, 32));

        JPanel cabecalho = new JPanel(new BorderLayout());
        cabecalho.setOpaque(false);
        cabecalho.add(UIUtils.titulo("Gestão de Leitores"), BorderLayout.WEST);
        JButton novo = UIUtils.botaoSucesso("+  Novo Leitor");
        cabecalho.add(novo, BorderLayout.EAST);

        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filtros.setOpaque(false);
        JButton todos = UIUtils.botaoSecundario("Todos");
        JButton atrasos = UIUtils.botaoSecundario("Com Atrasos");
        JButton bloqueados = UIUtils.botaoSecundario("Bloqueados");
        filtros.add(todos);
        filtros.add(atrasos);
        filtros.add(bloqueados);
        filtros.add(Box.createHorizontalStrut(20));
        JLabel buscar = new JLabel("Procurar:");
        buscar.setFont(new Font("SansSerif", Font.BOLD, 13));
        txtBusca.setPreferredSize(new Dimension(280, 40));
        filtros.add(buscar);
        filtros.add(txtBusca);

        JPanel topo = new JPanel();
        topo.setOpaque(false);
        topo.setLayout(new BoxLayout(topo, BoxLayout.Y_AXIS));
        topo.add(cabecalho);
        topo.add(Box.createVerticalStrut(16));
        topo.add(filtros);
        add(topo, BorderLayout.NORTH);

        modelo = new DefaultTableModel(new Object[]{"ID", "Nome", "Contato", "Telefone", "Livros em Posse", "Situação"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable tabela = new JTable(modelo);
        UIUtils.configurarTabela(tabela);
        sorter = new TableRowSorter<>(modelo);
        tabela.setRowSorter(sorter);
        add(new JScrollPane(tabela), BorderLayout.CENTER);

        novo.addActionListener(e -> {
            LeitorDialog d = new LeitorDialog(SwingUtilities.getWindowAncestor(this), service);
            d.setVisible(true);
            if (d.isSalvo()) {
                atualizar();
                if (onDadosAlterados != null) onDadosAlterados.run();
            }
        });
        todos.addActionListener(e -> { filtroSituacao = "Todos"; aplicarFiltro(); });
        atrasos.addActionListener(e -> { filtroSituacao = "Pendente"; aplicarFiltro(); });
        bloqueados.addActionListener(e -> { filtroSituacao = "Bloqueado"; aplicarFiltro(); });
        txtBusca.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { aplicarFiltro(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { aplicarFiltro(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { aplicarFiltro(); }
        });
        atualizar();
    }

    private void aplicarFiltro() {
        java.util.List<RowFilter<Object, Object>> filtros = new java.util.ArrayList<>();
        String busca = txtBusca.getText().trim();
        if (!busca.isEmpty()) filtros.add(RowFilter.regexFilter("(?i)" + Pattern.quote(busca), 1, 2, 3));
        if (!"Todos".equals(filtroSituacao)) filtros.add(RowFilter.regexFilter("^" + Pattern.quote(filtroSituacao) + "$", 5));
        sorter.setRowFilter(filtros.isEmpty() ? null : RowFilter.andFilter(filtros));
    }

    public void atualizar() {
        modelo.setRowCount(0);
        for (Usuario u : service.getUsuarios()) {
            modelo.addRow(new Object[]{
                    String.format("%05d", u.getIdUsuario()), u.getNome(), u.getContato(),
                    u.getTelefone().isEmpty() ? "—" : u.getTelefone(),
                    u.getLivrosEmPosse(), u.getSituacao()
            });
        }
        aplicarFiltro();
    }

    public void setOnDadosAlterados(Runnable r) { this.onDadosAlterados = r; }
}
