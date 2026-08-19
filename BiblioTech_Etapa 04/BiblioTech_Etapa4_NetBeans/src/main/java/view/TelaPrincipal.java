package view;

import service.BibliotecaService;
import util.UIUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class TelaPrincipal extends JFrame {
    private final BibliotecaService service;
    private final CardLayout cards = new CardLayout();
    private final JPanel conteudoCards = new JPanel(cards);
    private final DashboardPanel dashboard;
    private final AcervoPanel acervo;
    private final LeitoresPanel leitores;
    private final EmprestimosPanel emprestimos;
    private final JTextField pesquisaGlobal = UIUtils.campo();
    private final JButton btnInicio;
    private final JButton btnAcervo;
    private final JButton btnLeitores;
    private final JButton btnEmprestimos;
    private final Runnable onLogout;

    public TelaPrincipal(BibliotecaService service, Runnable onLogout) {
        this.service = service;
        this.onLogout = onLogout;
        setTitle("BiblioTech - Sistema de Gestão de Biblioteca");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1100, 700));
        setSize(1280, 800);
        setLocationRelativeTo(null);

        dashboard = new DashboardPanel(service);
        acervo = new AcervoPanel(service);
        leitores = new LeitoresPanel(service);
        emprestimos = new EmprestimosPanel(service);

        acervo.setOnDadosAlterados(this::atualizarTudo);
        leitores.setOnDadosAlterados(this::atualizarTudo);
        emprestimos.setOnDadosAlterados(this::atualizarTudo);

        btnInicio = criarNav("⌂  Início", "dashboard");
        btnAcervo = criarNav("▣  Acervo", "acervo");
        btnLeitores = criarNav("♟  Leitores", "leitores");
        btnEmprestimos = criarNav("↻  Empréstimos", "emprestimos");

        setLayout(new BorderLayout());
        add(criarSidebar(), BorderLayout.WEST);
        add(criarAreaPrincipal(), BorderLayout.CENTER);
        mostrar("dashboard");
    }

    private JPanel criarSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(250, 0));
        sidebar.setBackground(UIUtils.AZUL);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createEmptyBorder(0, 12, 16, 12));

        JPanel marca = new JPanel();
        marca.setOpaque(false);
        marca.setLayout(new BoxLayout(marca, BoxLayout.Y_AXIS));
        marca.setBorder(BorderFactory.createEmptyBorder(22, 12, 22, 12));
        JLabel nome = new JLabel("BiblioTech");
        nome.setForeground(Color.WHITE);
        nome.setFont(new Font("SansSerif", Font.BOLD, 22));
        JLabel versao = new JLabel("v2.0 • MySQL");
        versao.setForeground(new Color(210, 214, 245));
        versao.setFont(new Font("SansSerif", Font.PLAIN, 12));
        nome.setAlignmentX(Component.LEFT_ALIGNMENT);
        versao.setAlignmentX(Component.LEFT_ALIGNMENT);
        marca.add(nome);
        marca.add(Box.createVerticalStrut(4));
        marca.add(versao);
        sidebar.add(marca);

        sidebar.add(btnInicio);
        sidebar.add(Box.createVerticalStrut(6));
        sidebar.add(btnAcervo);
        sidebar.add(Box.createVerticalStrut(6));
        sidebar.add(btnLeitores);
        sidebar.add(Box.createVerticalStrut(6));
        sidebar.add(btnEmprestimos);
        sidebar.add(Box.createVerticalGlue());

        JLabel rodape = new JLabel("Sistema Inteligente de Gestão");
        rodape.setForeground(new Color(190, 195, 235));
        rodape.setFont(new Font("SansSerif", Font.PLAIN, 11));
        rodape.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 0));
        sidebar.add(rodape);
        return sidebar;
    }

    private JButton criarNav(String texto, String card) {
        JButton b = new JButton(texto);
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setFont(new Font("SansSerif", Font.PLAIN, 14));
        b.setForeground(new Color(225, 228, 250));
        b.setBackground(UIUtils.AZUL);
        b.setBorder(BorderFactory.createEmptyBorder(11, 14, 11, 14));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addActionListener(e -> mostrar(card));
        return b;
    }

    private JPanel criarAreaPrincipal() {
        JPanel area = new JPanel(new BorderLayout());
        area.add(criarHeader(), BorderLayout.NORTH);
        conteudoCards.add(dashboard, "dashboard");
        conteudoCards.add(acervo, "acervo");
        conteudoCards.add(leitores, "leitores");
        conteudoCards.add(emprestimos, "emprestimos");
        area.add(conteudoCards, BorderLayout.CENTER);
        return area;
    }

    private JPanel criarHeader() {
        JPanel header = new JPanel(new BorderLayout(18, 0));
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(229, 231, 235)),
                BorderFactory.createEmptyBorder(14, 28, 14, 28)));

        pesquisaGlobal.setToolTipText("Pesquisar livro, autor, ID ou ISBN");
        pesquisaGlobal.setPreferredSize(new Dimension(520, 42));
        pesquisaGlobal.addActionListener(this::pesquisarGlobalmente);
        header.add(pesquisaGlobal, BorderLayout.CENTER);

        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        acoes.setOpaque(false);
        JButton buscar = UIUtils.botaoPrimario("Pesquisar");
        buscar.addActionListener(this::pesquisarGlobalmente);
        JButton sair = UIUtils.botaoSecundario("Sair");
        sair.addActionListener(e -> logout());
        acoes.add(buscar);
        acoes.add(sair);
        header.add(acoes, BorderLayout.EAST);
        return header;
    }

    private void pesquisarGlobalmente(ActionEvent e) {
        mostrar("acervo");
        acervo.pesquisar(pesquisaGlobal.getText());
    }

    private void mostrar(String card) {
        cards.show(conteudoCards, card);
        if ("dashboard".equals(card)) dashboard.atualizar();
        if ("acervo".equals(card)) acervo.atualizar();
        if ("leitores".equals(card)) leitores.atualizar();
        if ("emprestimos".equals(card)) emprestimos.atualizar();
        atualizarNav(card);
    }

    private void atualizarNav(String atual) {
        JButton[] botoes = {btnInicio, btnAcervo, btnLeitores, btnEmprestimos};
        String[] ids = {"dashboard", "acervo", "leitores", "emprestimos"};
        for (int i = 0; i < botoes.length; i++) {
            boolean ativo = ids[i].equals(atual);
            botoes[i].setBackground(ativo ? UIUtils.AZUL_CLARO : UIUtils.AZUL);
            botoes[i].setForeground(ativo ? Color.WHITE : new Color(225, 228, 250));
        }
    }

    private void atualizarTudo() {
        dashboard.atualizar();
        acervo.atualizar();
        leitores.atualizar();
        emprestimos.atualizar();
    }

    private void logout() {
        int r = JOptionPane.showConfirmDialog(this, "Deseja sair do BiblioTech?", "Sair",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (r == JOptionPane.YES_OPTION) {
            dispose();
            if (onLogout != null) onLogout.run();
        }
    }
}
