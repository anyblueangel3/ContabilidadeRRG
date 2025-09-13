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

    public ProgramaPrincipal() {
        setTitle("Sistema de Contabilidade");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Evita fechamento automático pelo "X" da janela
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        // Garante que o fechamento pelo "X" passe pelo método sairDoSistema()
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                sairDoSistema();
            }
        });

    }

    /**
     * Permite trocar o painel central do JFrame
     */
    public void setPainelCentral(JPanel painel) {
        if (painelAtual != null) {
            remove(painelAtual);
        }
        painelAtual = painel;
        add(painelAtual, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    /**
     * Abre a tela principal do sistema ou cadastro de usuários dependendo se o
     * usuário possui ID (novo usuário) ou não.
     */
    public void abrirTelaPrincipal() {
        if (!SessaoDeUsuario.isLogado()) {
            JOptionPane.showMessageDialog(null, "Nenhum usuário logado. Faça login novamente.");
            dispose();
            return;
        }

        Usuario usuario = SessaoDeUsuario.getUsuarioLogado();

        // Atualiza título com o usuário logado
        setTitle("Sistema Contabilidade - Usuário: " + usuario.getLogin());

        // Define a barra de menus principal
        setJMenuBar(new MenuPrincipal(this));

        // Carrega o painel principal (bem-vindo + conteúdo inicial)
        JPanel painel = criarPainelPrincipal(usuario);
        setPainelCentral(painel);

        setVisible(true);
    }

    /**
     * Cria o painel principal do sistema (painel de boas-vindas e menu básico)
     */
    private JPanel criarPainelPrincipal(Usuario usuario) {
        JPanel painel = new JPanel(new BorderLayout());

        JLabel lbBemVindo = new JLabel(
                "Bem-vindo, " + usuario.getNome() + " (" + usuario.getPapel() + ")"
        );
        lbBemVindo.setHorizontalAlignment(SwingConstants.CENTER);
        lbBemVindo.setFont(new Font("Arial", Font.BOLD, 18));
        painel.add(lbBemVindo, BorderLayout.NORTH);

        // Painel central para futuras funcionalidades
        JPanel painelCentro = new JPanel();
        painelCentro.add(new JLabel("Aqui ficará o conteúdo principal do sistema."));
        painel.add(painelCentro, BorderLayout.CENTER);

        return painel;
    }

    /**
     * Abre o painel de cadastro de usuários dentro do JFrame principal.
     */
    public void abrirCadastroDeUsuarios(Usuario usuario) {
        CadastroDeUsuarios painelCadastro = new CadastroDeUsuarios(this, usuario);
        setPainelCentral(painelCadastro);
        setTitle("Cadastro de Usuário");
        setVisible(true);
    }

    /**
     * Fecha o sistema de forma padronizada
     */
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
            dispose(); // fecha janela principal
            System.exit(0); // encerra aplicação
        }
    }

}
