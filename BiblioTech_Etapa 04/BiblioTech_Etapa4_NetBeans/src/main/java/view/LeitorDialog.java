package view;

import service.BibliotecaService;
import util.UIUtils;

import javax.swing.*;
import java.awt.*;

public class LeitorDialog extends JDialog {
    private final JTextField txtNome = UIUtils.campo();
    private final JTextField txtEmail = UIUtils.campo();
    private final JTextField txtTelefone = UIUtils.campo();
    private boolean salvo;

    public LeitorDialog(Window owner, BibliotecaService service) {
        super(owner, "Cadastrar Novo Leitor", ModalityType.APPLICATION_MODAL);
        setSize(510, 380);
        setResizable(false);
        setLocationRelativeTo(owner);

        JPanel principal = new JPanel(new BorderLayout(0, 18));
        principal.setBackground(Color.WHITE);
        principal.setBorder(BorderFactory.createEmptyBorder(22, 24, 22, 24));
        principal.add(UIUtils.titulo("Cadastrar Novo Leitor"), BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(7, 0, 7, 0);
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1;
        adicionar(form, g, 0, "Nome Completo *", txtNome);
        adicionar(form, g, 1, "E-mail *", txtEmail);
        adicionar(form, g, 2, "Telefone", txtTelefone);
        principal.add(form, BorderLayout.CENTER);

        JButton cancelar = UIUtils.botaoSecundario("Cancelar");
        JButton salvar = UIUtils.botaoSucesso("Salvar Leitor");
        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        botoes.setOpaque(false);
        botoes.add(cancelar);
        botoes.add(salvar);
        principal.add(botoes, BorderLayout.SOUTH);

        cancelar.addActionListener(e -> dispose());
        salvar.addActionListener(e -> {
            String nome = txtNome.getText().trim();
            String email = txtEmail.getText().trim();
            String telefone = txtTelefone.getText().trim();
            if (nome.isEmpty() || email.isEmpty()) {
                UIUtils.erro(this, "Preencha nome e e-mail.");
                return;
            }
            if (!email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
                UIUtils.erro(this, "Informe um e-mail válido.");
                return;
            }
            if (!telefone.isEmpty() && !telefone.matches("\\(\\d{2}\\) \\d{4,5}-\\d{4}")) {
                UIUtils.erro(this, "Telefone inválido. Use (00) 00000-0000.");
                return;
            }
            service.adicionarUsuario(nome, email, telefone);
            salvo = true;
            UIUtils.sucesso(this, "Leitor cadastrado com sucesso!");
            dispose();
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
