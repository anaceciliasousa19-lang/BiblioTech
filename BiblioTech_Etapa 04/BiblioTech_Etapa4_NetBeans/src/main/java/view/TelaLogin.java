package view;

import service.BibliotecaService;
import util.UIUtils;

import javax.swing.*;
import java.awt.*;

public class TelaLogin extends JFrame {
    private final BibliotecaService service;
    private final JTextField txtUsuario = UIUtils.campo();
    private final JPasswordField txtSenha = UIUtils.senha();

    public TelaLogin(BibliotecaService service) {
        this.service = service;
        setTitle("BiblioTech - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(620, 600);
        setResizable(false);
        setLocationRelativeTo(null);
        setContentPane(criarConteudo());
    }

    private JPanel criarConteudo() {
        JPanel fundo = new JPanel(new GridBagLayout());
        fundo.setBackground(UIUtils.AZUL);

        JPanel card = new JPanel();
        card.setPreferredSize(new Dimension(430, 445));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createEmptyBorder(38, 42, 34, 42));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel icone = new JLabel("▣", SwingConstants.CENTER);
        icone.setOpaque(true);
        icone.setBackground(UIUtils.AZUL);
        icone.setForeground(Color.WHITE);
        icone.setFont(new Font("SansSerif", Font.BOLD, 32));
        icone.setMaximumSize(new Dimension(64, 64));
        icone.setPreferredSize(new Dimension(64, 64));
        icone.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titulo = new JLabel("BiblioTech");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 28));
        titulo.setForeground(UIUtils.TEXTO);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = new JLabel("Sistema Inteligente de Gestão");
        sub.setFont(new Font("SansSerif", Font.PLAIN, 14));
        sub.setForeground(UIUtils.CINZA);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(icone);
        card.add(Box.createVerticalStrut(18));
        card.add(titulo);
        card.add(Box.createVerticalStrut(5));
        card.add(sub);
        card.add(Box.createVerticalStrut(28));

        JLabel lu = new JLabel("E-mail ou Utilizador");
        lu.setFont(new Font("SansSerif", Font.BOLD, 13));
        lu.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtUsuario.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtUsuario.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        txtUsuario.setToolTipText("Usuário inicial do banco: admin ou admin@bibliotech.com");
        card.add(lu);
        card.add(Box.createVerticalStrut(7));
        card.add(txtUsuario);
        card.add(Box.createVerticalStrut(17));

        JLabel ls = new JLabel("Palavra-passe");
        ls.setFont(new Font("SansSerif", Font.BOLD, 13));
        ls.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtSenha.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtSenha.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        txtSenha.setToolTipText("Senha inicial cadastrada no banco: 1234");
        card.add(ls);
        card.add(Box.createVerticalStrut(7));
        card.add(txtSenha);
        card.add(Box.createVerticalStrut(22));

        JButton entrar = UIUtils.botaoPrimario("ENTRAR");
        entrar.setAlignmentX(Component.LEFT_ALIGNMENT);
        entrar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        entrar.addActionListener(e -> entrar());
        card.add(entrar);
        card.add(Box.createVerticalGlue());

        JLabel dica = new JLabel("Acesso de demonstração: admin / 1234");
        dica.setFont(new Font("SansSerif", Font.PLAIN, 11));
        dica.setForeground(UIUtils.CINZA);
        dica.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(dica);
        card.add(Box.createVerticalStrut(8));
        JLabel rodape = new JLabel("© 2026 BiblioTech - Todos os direitos reservados");
        rodape.setFont(new Font("SansSerif", Font.PLAIN, 10));
        rodape.setForeground(UIUtils.CINZA);
        rodape.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(rodape);

        fundo.add(card);
        getRootPane().setDefaultButton(entrar);
        return fundo;
    }

    private void entrar() {
        String usuario = txtUsuario.getText().trim();
        String senha = new String(txtSenha.getPassword());
        if (usuario.isEmpty() || senha.isEmpty()) {
            UIUtils.erro(this, "Informe o utilizador e a palavra-passe.");
            return;
        }
        try {
            if (!service.autenticar(usuario, senha)) {
                UIUtils.erro(this, "Utilizador ou palavra-passe inválidos.");
                txtSenha.setText("");
                txtSenha.requestFocus();
                return;
            }
            dispose();
            new TelaPrincipal(service, () -> new TelaLogin(service).setVisible(true)).setVisible(true);
        } catch (IllegalStateException ex) {
            UIUtils.erro(this, ex.getMessage());
        }
    }
}
