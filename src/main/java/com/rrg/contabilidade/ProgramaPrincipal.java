package com.rrg.contabilidade;

import com.rrg.contabilidade.model.Usuario;
import com.rrg.contabilidade.util.SessaoDeUsuario;

import javax.swing.*;
import java.awt.*;

/**
 *
 * @author Ronaldo Rodrigues Godoi e Chat GPT
 *
 * JFrame principal do sistema, contendo todos os painéis de trabalho. MVC:
 * ProgramaPrincipal = View/Controller, painéis = View
 */
public class ProgramaPrincipal extends JFrame {

    private Usuario usuarioLogado;
    private JPanel painelAtual;
    private JPanel painelAnterior;

    public ProgramaPrincipal() {
        setTitle("Sistema de Contabilidade");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                sairDoSistema();
            }
        });

        getContentPane().setLayout(new BorderLayout());
    }

    // substitui painel central de forma segura
    public void setPainelCentral(JPanel painel) {
        getContentPane().removeAll();
        painelAtual = painel;
        getContentPane().add(painelAtual, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    public void abrirTelaPrincipal() {
        if (!SessaoDeUsuario.isLogado()) {
            JOptionPane.showMessageDialog(null, "Nenhum usuário logado. Faça login novamente.");
            dispose();
            return;
        }

        Usuario usuario = SessaoDeUsuario.getUsuarioLogado();
        setTitle("Sistema Contabilidade - Usuário: " + usuario.getLogin());
        setJMenuBar(new MenuPrincipal(this));

        JPanel painel = criarPainelPrincipal(usuario);
        setPainelCentral(painel);
        setVisible(true);
    }

    private JPanel criarPainelPrincipal(Usuario usuario) {
        JPanel painel = new JPanel(new BorderLayout());
        JLabel lbBemVindo = new JLabel(
                "Bem-vindo, " + usuario.getNome() + " (" + usuario.getPapel() + ")"
        );
        lbBemVindo.setHorizontalAlignment(SwingConstants.CENTER);
        lbBemVindo.setFont(new Font("Arial", Font.BOLD, 18));
        painel.add(lbBemVindo, BorderLayout.NORTH);

        JPanel painelCentro = new JPanel();
        painelCentro.add(new JLabel("Aqui ficará o conteúdo principal do sistema."));
        painel.add(painelCentro, BorderLayout.CENTER);

        return painel;
    }

    public void abrirCadastroDeUsuarios(Usuario usuario, boolean primeiroAcesso) {
        CadastroDeUsuarios painelCadastro = new CadastroDeUsuarios(this, usuario, primeiroAcesso);
        setPainelCentral(painelCadastro);
        setTitle("Cadastro de Usuário");
        setVisible(true);
    }

    public void mostrarPainelTemporario(JPanel temporario) {
        painelAnterior = painelAtual;
        setPainelCentral(temporario);
    }

    public void voltarPainelAnterior() {
        if (painelAnterior != null) {
            setPainelCentral(painelAnterior);
            painelAnterior = null;
        }
    }

    public void sairDoSistema() {
        Object[] opcoes = {"Sim", "Não"};

        int opcao = JOptionPane.showOptionDialog(
                this,
                "Deseja realmente sair do sistema?",
                "Confirmação",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                opcoes,
                opcoes[0]
        );

        if (opcao == JOptionPane.YES_OPTION) {
            dispose();
            System.exit(0);
        }
    }

}
