package com.rrg.contabilidade;

import com.rrg.contabilidade.model.Empresa;
import com.rrg.contabilidade.model.Periodo;
import com.rrg.contabilidade.model.Usuario;
import com.rrg.contabilidade.util.SessaoDeUsuario;

import javax.swing.*;
import java.awt.*;

/**
 *
 * @author Ronaldo Rodrigues Godoi e Chat GPT
 */
public class Lancamentos extends JPanel {

    private ProgramaPrincipal programaPrincipal;
    private Usuario usuario;
    private Empresa empresa;
    private Periodo periodo;

    private JButton btAbrirPeriodo;
    private JButton btSair;

    public Lancamentos(ProgramaPrincipal pP) {
        this.programaPrincipal = pP;
        this.usuario = SessaoDeUsuario.getUsuarioLogado();
        this.empresa = SessaoDeUsuario.getEmpresaLogada();
        this.periodo = SessaoDeUsuario.getPeriodoLogado();

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

    setLayout(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(10, 10, 10, 10);
    gbc.fill = GridBagConstraints.HORIZONTAL;

    JLabel lbTitulo = new JLabel("Lançamentos");
    lbTitulo.setFont(new Font("Arial", Font.BOLD, 18));
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 2;
    add(lbTitulo, gbc);

    gbc.gridy++;
    gbc.gridwidth = 1;
    btAbrirPeriodo = new JButton("Abrir Período");
    add(btAbrirPeriodo, gbc);

    gbc.gridy++;
    btSair = new JButton("Sair dos Lançamentos");
    add(btSair, gbc);

    definirEventos();
}


    private void definirEventos() {
        btAbrirPeriodo.addActionListener(e -> {
            PeriodoJDialog dialog = new PeriodoJDialog(programaPrincipal);
            dialog.setVisible(true);
        });

        btSair.addActionListener(e -> programaPrincipal.abrirTelaPrincipal());
    }
}
