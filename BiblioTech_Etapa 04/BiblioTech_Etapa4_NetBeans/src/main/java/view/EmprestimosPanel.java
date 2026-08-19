package view;

import model.Emprestimo;
import service.BibliotecaService;
import util.UIUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;

public class EmprestimosPanel extends JPanel {
    private final BibliotecaService service;
    private final DefaultTableModel modelo;
    private final JTable tabela;
    private Runnable onDadosAlterados;

    public EmprestimosPanel(BibliotecaService service) {
        this.service = service;
        setLayout(new BorderLayout(0, 18));
        setBackground(UIUtils.FUNDO);
        setBorder(BorderFactory.createEmptyBorder(28, 32, 28, 32));

        JPanel cabecalho = new JPanel(new BorderLayout());
        cabecalho.setOpaque(false);
        cabecalho.add(UIUtils.titulo("Gestão de Empréstimos"), BorderLayout.WEST);
        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        acoes.setOpaque(false);
        JButton devolver = UIUtils.botaoSecundario("Registrar Devolução");
        JButton novo = UIUtils.botaoSucesso("+  Novo Empréstimo");
        acoes.add(devolver);
        acoes.add(novo);
        cabecalho.add(acoes, BorderLayout.EAST);
        add(cabecalho, BorderLayout.NORTH);

        modelo = new DefaultTableModel(new Object[]{"ID", "Livro", "Leitor", "Data Empréstimo", "Data Devolução", "Status"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tabela = new JTable(modelo);
        UIUtils.configurarTabela(tabela);
        add(new JScrollPane(tabela), BorderLayout.CENTER);

        novo.addActionListener(e -> {
            EmprestimoDialog d = new EmprestimoDialog(SwingUtilities.getWindowAncestor(this), service);
            d.setVisible(true);
            if (d.isSalvo()) {
                atualizar();
                if (onDadosAlterados != null) onDadosAlterados.run();
            }
        });

        devolver.addActionListener(e -> {
            int linha = tabela.getSelectedRow();
            if (linha < 0) {
                UIUtils.erro(this, "Selecione um empréstimo na tabela.");
                return;
            }
            int modelRow = tabela.convertRowIndexToModel(linha);
            int id = Integer.parseInt(modelo.getValueAt(modelRow, 0).toString());
            Emprestimo emp = service.getEmprestimos().stream()
                    .filter(x -> x.getIdEmprestimo() == id).findFirst().orElse(null);
            try {
                service.registrarDevolucao(emp);
                UIUtils.sucesso(this, "Devolução registrada com sucesso!");
                atualizar();
                if (onDadosAlterados != null) onDadosAlterados.run();
            } catch (IllegalArgumentException | IllegalStateException ex) {
                UIUtils.erro(this, ex.getMessage());
            }
        });
        atualizar();
    }

    public void atualizar() {
        modelo.setRowCount(0);
        DateTimeFormatter f = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        for (Emprestimo e : service.getEmprestimos()) {
            modelo.addRow(new Object[]{
                    e.getIdEmprestimo(), e.getLivro().getTitulo(), e.getUsuario().getNome(),
                    e.getDataEmprestimo().format(f), e.getDataDevolucao().format(f), e.getStatus()
            });
        }
    }

    public void setOnDadosAlterados(Runnable r) { this.onDadosAlterados = r; }
}
