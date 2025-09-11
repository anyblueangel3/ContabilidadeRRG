package com.rrg.contabilidade;

import com.rrg.contabilidade.model.Usuario;
import javax.swing.*;
import java.awt.*;

/**
 * 
 * @author Ronaldo Rodrigues Godoi e Chat GPT
 * 
 * JFrame principal do sistema, contendo todos os painéis de trabalho.
 * MVC: ProgramaPrincipal = View/Controller, painéis = View
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
     * Abre a tela principal do sistema ou cadastro de usuários
     * dependendo se o usuário possui ID (novo usuário) ou não.
     */
    public void abrirTelaPrincipal(Usuario usuario) {
        this.usuarioLogado = usuario;

        if (usuarioLogado.getId() == null) {
            // Primeiro acesso ou criação de ADMIN
            CadastroDeUsuarios cadastroPanel = new CadastroDeUsuarios(this, usuarioLogado);
            setPainelCentral(cadastroPanel);
        } else {
            // Usuário existente, abre painel de trabalho principal
            JPanel painelTrabalho = criarPainelPrincipal(usuarioLogado);
            setPainelCentral(painelTrabalho);

            // Inicializa menu
            MenuPrincipal menu = new MenuPrincipal(this);
            setJMenuBar(menu);
        }

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
}
