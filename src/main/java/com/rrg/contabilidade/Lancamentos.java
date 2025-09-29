package com.rrg.contabilidade;

import com.rrg.contabilidade.model.Empresa;
import com.rrg.contabilidade.model.Periodo;
import com.rrg.contabilidade.model.Usuario;
import com.rrg.contabilidade.util.SessaoDeUsuario;
import com.rrg.contabilidade.util.SessaoListener;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;

/**
 *
 * @author Ronaldo Rodrigues Godoi e Chat GPT
 */
public class Lancamentos extends JPanel implements SessaoListener {

    private ProgramaPrincipal programaPrincipal;
    private Usuario usuario;
    private Empresa empresa;
    private Periodo periodo;

    private JButton btAbrirPeriodo;
    private JButton btSair;

    // labels informativas
    private JLabel lbUsuarioLogado;
    private JLabel lbEmpresaLogada;
    private JLabel lbPeriodoLogado;

    private final SimpleDateFormat formatoData = new SimpleDateFormat("dd/MM/yyyy");

    public Lancamentos(ProgramaPrincipal pP) {
        // logo no início da classe
        lbUsuarioLogado = new JLabel("Usuário Logado: ");
        lbEmpresaLogada = new JLabel("Empresa Logada: ");
        lbPeriodoLogado = new JLabel("Período Logado: ");

        this.programaPrincipal = pP;
        this.programaPrincipal.setMenuAtivo(false);

        this.usuario = SessaoDeUsuario.getUsuarioLogado();

        this.empresa = SessaoDeUsuario.getEmpresaLogada();

        this.periodo = SessaoDeUsuario.getPeriodoLogado();

        // registra como listener para atualizar informações quando a sessão mudar
        SessaoDeUsuario.addListener(this);

        inicializarComponentes();
    }

    private void inicializarComponentes() {
        // Teste de login de usuário e empresa
        if (usuario == null || empresa == null) {
            JOptionPane.showMessageDialog(this,
                    "Por favor, faça login no menu Operações → Login Usuário + Empresa + Período.",
                    "Login necessário",
                    JOptionPane.WARNING_MESSAGE);

            // Volta imediatamente para ProgramaPrincipal
            SwingUtilities.invokeLater(() -> programaPrincipal.abrirTelaPrincipal());
            return; // sai do método, não inicializa mais a tela
        }

        setLayout(new BorderLayout(10, 10));

        // título no topo
        JLabel lbTitulo = new JLabel("Lançamentos", SwingConstants.CENTER);
        lbTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        add(lbTitulo, BorderLayout.NORTH);

        // painel inferior com informações (esquerda) e botões (direita)
        JPanel painelRodape = new JPanel(new BorderLayout());

        // painel inferior esquerdo → informações da sessão
        JPanel painelInfo = new JPanel(new GridLayout(3, 1));
        painelInfo.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        lbUsuarioLogado = new JLabel();
        lbEmpresaLogada = new JLabel();
        lbPeriodoLogado = new JLabel();
        painelInfo.add(lbUsuarioLogado);
        painelInfo.add(lbEmpresaLogada);
        painelInfo.add(lbPeriodoLogado);

        painelRodape.add(painelInfo, BorderLayout.WEST);

        // painel inferior direito → botões
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        btAbrirPeriodo = new JButton("Abrir Período");
        btSair = new JButton("Sair dos Lançamentos");
        painelBotoes.add(btAbrirPeriodo);
        painelBotoes.add(btSair);

        painelRodape.add(painelBotoes, BorderLayout.EAST);

        add(painelRodape, BorderLayout.SOUTH);

        atualizarInfoSessao();
        definirEventos();
    }

    private void definirEventos() {
        btAbrirPeriodo.addActionListener(e -> {
            PeriodoJDialog dialog = new PeriodoJDialog(programaPrincipal);
            dialog.setVisible(true);
            // após fechar o dialog, SessaoDeUsuario deve notificar os listeners
        });

        btSair.addActionListener(e -> {
            programaPrincipal.setMenuAtivo(true);
            programaPrincipal.abrirTelaPrincipal();
        });
    }

    private void atualizarInfoSessao() {
        this.usuario = SessaoDeUsuario.getUsuarioLogado();
        this.empresa = SessaoDeUsuario.getEmpresaLogada();
        this.periodo = SessaoDeUsuario.getPeriodoLogado();

        String usuarioText = (usuario != null) ? ("Usuário Logado: " + usuario.getNome()) : "Usuário Logado: null";
        String empresaText = (empresa != null) ? ("Empresa Logada: " + empresa.getRazao()) : "Empresa Logada: null";
        String periodoText;
        if (periodo != null && periodo.getInicio() != null && periodo.getFim() != null) {
            periodoText = "Período Logado: " + formatoData.format(periodo.getInicio()) + " até " + formatoData.format(periodo.getFim());
        } else {
            periodoText = "Período Logado: null";
        }

        lbUsuarioLogado.setText(usuarioText);
        lbEmpresaLogada.setText(empresaText);
        lbPeriodoLogado.setText(periodoText);
    }

    // SessaoListener callbacks
    @Override
    public void aoLogar() {
        SwingUtilities.invokeLater(this::atualizarInfoSessao);
    }

    @Override
    public void aoLogout() {
        SwingUtilities.invokeLater(this::atualizarInfoSessao);
    }
}
