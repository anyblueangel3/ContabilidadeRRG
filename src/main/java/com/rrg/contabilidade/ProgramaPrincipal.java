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
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
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

}
