package view;

import model.Livro;
import model.Usuario;
import service.BibliotecaService;
import util.UIUtils;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class EmprestimoDialog extends JDialog {
    private final JComboBox<Livro> cmbLivro = new JComboBox<>();
    private final JComboBox<Usuario> cmbLeitor = new JComboBox<>();
    private final JTextField txtDevolucao = UIUtils.campo();
    private boolean salvo;

    public EmprestimoDialog(Window owner, BibliotecaService service) {
        super(owner, "Registrar Novo Empréstimo", ModalityType.APPLICATION_MODAL);
        setSize(540, 390);
        setResizable(false);
        setLocationRelativeTo(owner);

        service.getLivros().stream().filter(Livro::isDisponivel).forEach(cmbLivro::addItem);
        service.getUsuarios().forEach(cmbLeitor::addItem);
        txtDevolucao.setText(LocalDate.now().plusDays(14).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        JPanel principal = new JPanel(new BorderLayout(0, 18));
        principal.setBackground(Color.WHITE);
        principal.setBorder(BorderFactory.createEmptyBorder(22, 24, 22, 24));
        principal.add(UIUtils.titulo("Registrar Novo Empréstimo"), BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(7, 0, 7, 0);
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1;
        adicionar(form, g, 0, "Livro *", cmbLivro);
        adicionar(form, g, 1, "Leitor *", cmbLeitor);
        adicionar(form, g, 2, "Data de Devolução * (dd/MM/aaaa)", txtDevolucao);
        principal.add(form, BorderLayout.CENTER);

        JButton cancelar = UIUtils.botaoSecundario("Cancelar");
        JButton salvar = UIUtils.botaoSucesso("Confirmar Empréstimo");
        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        botoes.setOpaque(false);
        botoes.add(cancelar);
        botoes.add(salvar);
        principal.add(botoes, BorderLayout.SOUTH);

        cancelar.addActionListener(e -> dispose());
        salvar.addActionListener(e -> {
            if (cmbLivro.getSelectedItem() == null || cmbLeitor.getSelectedItem() == null) {
                UIUtils.erro(this, "Selecione o livro e o leitor.");
                return;
            }
            try {
                LocalDate data = LocalDate.parse(txtDevolucao.getText().trim(), DateTimeFormatter.ofPattern("dd/MM/uuuu"));
                service.registrarEmprestimo((Livro) cmbLivro.getSelectedItem(),
                        (Usuario) cmbLeitor.getSelectedItem(), data);
                salvo = true;
                UIUtils.sucesso(this, "Empréstimo registrado com sucesso!");
                dispose();
            } catch (DateTimeParseException ex) {
                UIUtils.erro(this, "Data inválida. Use dd/MM/aaaa.");
            } catch (IllegalArgumentException | IllegalStateException ex) {
                UIUtils.erro(this, ex.getMessage());
            }
        });
        setContentPane(principal);
        getRootPane().setDefaultButton(salvar);
    }

    private void adicionar(JPanel p, GridBagConstraints g, int linha, String rotulo, JComponent campo) {
        g.gridx = 0; g.gridy = linha * 2;
        JLabel l = new JLabel(rotulo);
        l.setFont(new Font("SansSerif", Font.BOLD, 13));
        p.add(l, g);
        g.gridy = linha * 2 + 1;
        p.add(campo, g);
    }

    public boolean isSalvo() { return salvo; }
}
