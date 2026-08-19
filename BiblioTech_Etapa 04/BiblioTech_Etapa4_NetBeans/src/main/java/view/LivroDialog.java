package view;

import service.BibliotecaService;
import util.UIUtils;

import javax.swing.*;
import java.awt.*;

public class LivroDialog extends JDialog {
    private final JTextField txtTitulo = UIUtils.campo();
    private final JTextField txtAutor = UIUtils.campo();
    private final JTextField txtAno = UIUtils.campo();
    private final JTextField txtEditora = UIUtils.campo();
    private final JTextField txtIsbn = UIUtils.campo();
    private boolean salvo;

    public LivroDialog(Window owner, BibliotecaService service) {
        super(owner, "Adicionar Novo Livro", ModalityType.APPLICATION_MODAL);
        setSize(520, 470);
        setResizable(false);
        setLocationRelativeTo(owner);

        JPanel principal = new JPanel(new BorderLayout(0, 18));
        principal.setBackground(Color.WHITE);
        principal.setBorder(BorderFactory.createEmptyBorder(22, 24, 22, 24));
        principal.add(UIUtils.titulo("Adicionar Novo Livro"), BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(7, 0, 7, 0);
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1;
        adicionar(form, g, 0, "Título do Livro *", txtTitulo);
        adicionar(form, g, 1, "Autor(es) *", txtAutor);
        adicionar(form, g, 2, "Ano *", txtAno);
        adicionar(form, g, 3, "Editora", txtEditora);
        adicionar(form, g, 4, "ISBN", txtIsbn);
        principal.add(form, BorderLayout.CENTER);

        JButton cancelar = UIUtils.botaoSecundario("Cancelar");
        JButton salvar = UIUtils.botaoPrimario("Salvar no Acervo");
        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        botoes.setOpaque(false);
        botoes.add(cancelar);
        botoes.add(salvar);
        principal.add(botoes, BorderLayout.SOUTH);

        cancelar.addActionListener(e -> dispose());
        salvar.addActionListener(e -> {
            String titulo = txtTitulo.getText().trim();
            String autor = txtAutor.getText().trim();
            String anoTexto = txtAno.getText().trim();
            if (titulo.isEmpty() || autor.isEmpty() || anoTexto.isEmpty()) {
                UIUtils.erro(this, "Preencha título, autor e ano.");
                return;
            }
            try {
                int ano = Integer.parseInt(anoTexto);
                if (ano < 1000 || ano > java.time.Year.now().getValue()) {
                    UIUtils.erro(this, "Informe um ano válido.");
                    return;
                }
                service.adicionarLivro(titulo, autor, ano, txtEditora.getText(), txtIsbn.getText());
                salvo = true;
                UIUtils.sucesso(this, "Livro adicionado com sucesso!");
                dispose();
            } catch (NumberFormatException ex) {
                UIUtils.erro(this, "O ano deve conter apenas números.");
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
