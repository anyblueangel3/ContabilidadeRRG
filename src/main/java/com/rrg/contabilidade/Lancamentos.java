package com.rrg.contabilidade;

import com.rrg.contabilidade.controller.ContaController;
import com.rrg.contabilidade.model.Conta;
import com.rrg.contabilidade.model.Empresa;
import com.rrg.contabilidade.model.Periodo;
import com.rrg.contabilidade.model.Usuario;
import com.rrg.contabilidade.util.SessaoDeUsuario;
import com.rrg.contabilidade.util.SessaoListener;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class Lancamentos extends JPanel implements SessaoListener {

    private ProgramaPrincipal programaPrincipal;
    private Usuario usuario;
    private Empresa empresa;
    private Periodo periodo;

    private JButton btAbrirPeriodo;
    private JButton btSair;
    private JButton btGravarLancamento;
    private JButton btAlterarLancamento;

    private JLabel lbUsuarioLogado;
    private JLabel lbEmpresaLogada;
    private JLabel lbPeriodoLogado;

    // Campos de lançamento
    private JComboBox<String> cbContaDebito;
    private JComboBox<String> cbContaCredito;
    private JTextField tfValorDebito;
    private JTextField tfValorCredito;
    private JTextField tfData;
    private JTextArea taHistorico;

    private final SimpleDateFormat formatoData = new SimpleDateFormat("ddMMyyyy");
    private final ContaController contaController = new ContaController();

    public Lancamentos(ProgramaPrincipal pP) {
        this.programaPrincipal = pP;
        this.programaPrincipal.setMenuAtivo(false);

        this.usuario = SessaoDeUsuario.getUsuarioLogado();
        this.empresa = SessaoDeUsuario.getEmpresaLogada();
        this.periodo = SessaoDeUsuario.getPeriodoLogado();

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
            SwingUtilities.invokeLater(() -> programaPrincipal.abrirTelaPrincipal());
            return;
        }

        setLayout(new BorderLayout(10, 10));

        JLabel lbTitulo = new JLabel("Lançamentos", SwingConstants.CENTER);
        lbTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        add(lbTitulo, BorderLayout.NORTH);

        // Painel central com campos de lançamento
        JPanel painelCentro = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 3, 3, 3);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Conta Débito
        gbc.gridx = 0;
        gbc.gridy = 0;
        painelCentro.add(new JLabel("Conta Débito:"), gbc);

        cbContaDebito = new JComboBox<>();
        cbContaDebito.setEditable(true);
        gbc.gridx = 1;
        painelCentro.add(cbContaDebito, gbc);

        // Conta Crédito
        gbc.gridx = 0;
        gbc.gridy = 1;
        painelCentro.add(new JLabel("Conta Crédito:"), gbc);

        cbContaCredito = new JComboBox<>();
        cbContaCredito.setEditable(true);
        gbc.gridx = 1;
        painelCentro.add(cbContaCredito, gbc);

        // Valor Débito
        gbc.gridx = 0;
        gbc.gridy = 2;
        painelCentro.add(new JLabel("Valor Débito:"), gbc);

        tfValorDebito = new JTextField(10);
        gbc.gridx = 1;
        painelCentro.add(tfValorDebito, gbc);

        // Valor Crédito
        gbc.gridx = 0;
        gbc.gridy = 3;
        painelCentro.add(new JLabel("Valor Crédito:"), gbc);

        tfValorCredito = new JTextField(10);
        gbc.gridx = 1;
        painelCentro.add(tfValorCredito, gbc);

        // Data
        gbc.gridx = 0;
        gbc.gridy = 4;
        painelCentro.add(new JLabel("Data (ddMMyyyy):"), gbc);

        tfData = new JTextField(10);
        gbc.gridx = 1;
        painelCentro.add(tfData, gbc);

        // Histórico
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 1;
        painelCentro.add(new JLabel("Histórico:"), gbc);

        taHistorico = new JTextArea(3, 20);
        JScrollPane scrollHistorico = new JScrollPane(taHistorico);
        gbc.gridx = 1;
        painelCentro.add(scrollHistorico, gbc);

        add(painelCentro, BorderLayout.CENTER);

        // Painel inferior
        JPanel painelRodape = new JPanel(new BorderLayout());

        JPanel painelInfo = new JPanel(new GridLayout(3, 1));
        lbUsuarioLogado = new JLabel();
        lbEmpresaLogada = new JLabel();
        lbPeriodoLogado = new JLabel();
        painelInfo.add(lbUsuarioLogado);
        painelInfo.add(lbEmpresaLogada);
        painelInfo.add(lbPeriodoLogado);
        painelRodape.add(painelInfo, BorderLayout.WEST);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        btAbrirPeriodo = new JButton("Abrir Período");
        btSair = new JButton("Sair dos Lançamentos");
        btGravarLancamento = new JButton("Gravar Lançamento");
        btAlterarLancamento = new JButton("Alterar Lançamento");

        btGravarLancamento.setEnabled(false);

        painelBotoes.add(btAbrirPeriodo);
        painelBotoes.add(btSair);
        painelBotoes.add(btGravarLancamento);
        painelBotoes.add(btAlterarLancamento);

        painelRodape.add(painelBotoes, BorderLayout.EAST);
        add(painelRodape, BorderLayout.SOUTH);

        atualizarInfoSessao();
        definirEventos();
        carregarContas();
    }

    private void definirEventos() {
        btAbrirPeriodo.addActionListener(e -> {
            PeriodoJDialog dialog = new PeriodoJDialog(programaPrincipal);
            dialog.setVisible(true);
        });

        btSair.addActionListener(e -> {
            programaPrincipal.setMenuAtivo(true);
            programaPrincipal.abrirTelaPrincipal();
        });

        btGravarLancamento.addActionListener(e -> {
            // TODO: implementar gravação de lançamento no DAO
            JOptionPane.showMessageDialog(this, "Lançamento gravado (simulação).");
            limparCampos();
        });

        btAlterarLancamento.addActionListener(e -> {
            // TODO: implementar alteração com verificação de permissões
            JOptionPane.showMessageDialog(this, "Alterar lançamento (simulação).");
        });
    }

    private void carregarContas() {
        // Preencher JComboBox com contas do plano da empresa (simulação)
        List<Conta> contas = contaController.listarContasPorPlano(1, true); // ID do plano fixo para exemplo
        cbContaDebito.removeAllItems();
        cbContaCredito.removeAllItems();
        for (Conta c : contas) {
            String display = c.getId() + " - " + c.getDescricao();
            cbContaDebito.addItem(display);
            cbContaCredito.addItem(display);
        }
    }

    private void limparCampos() {
        cbContaDebito.setSelectedItem(null);
        cbContaCredito.setSelectedItem(null);
        tfValorDebito.setText("");
        tfValorCredito.setText("");
        tfData.setText("");
        taHistorico.setText("");
        btGravarLancamento.setEnabled(false);
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

    @Override
    public void aoLogar() {
        SwingUtilities.invokeLater(this::atualizarInfoSessao);
    }

    @Override
    public void aoLogout() {
        SwingUtilities.invokeLater(this::atualizarInfoSessao);
    }
}
