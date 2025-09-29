package com.rrg.contabilidade;

import com.rrg.contabilidade.controller.PapelController;
import com.rrg.contabilidade.model.Papel;
import com.rrg.contabilidade.model.Usuario;

import javax.swing.*;
import java.awt.*;

/**
 *
 * @author Ronaldo Rodrigues Godoi e Chat GPT
 *
 * Painel para cadastro de novos papeis e gerenciamento de operações.
 */
public class CadastroDePapeis extends JPanel {

    private ProgramaPrincipal programaPrincipal;
    private PapelController controller;
    private Usuario usuario;

    private JTextField tfNomePapel;
    private JButton btSalvar;
    private JButton btSair;
    private JButton btAlterarOperacoes;

    public CadastroDePapeis(ProgramaPrincipal programaPrincipal, Usuario usuario) {
        this.programaPrincipal = programaPrincipal;
        this.programaPrincipal.setMenuAtivo(false);
        this.controller = new PapelController();
        this.usuario = usuario;

        inicializarComponentes();
    }

    private void inicializarComponentes() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lbTitulo = new JLabel("Cadastro de Papel");
        lbTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(lbTitulo, gbc);

        gbc.gridy++;
        gbc.gridwidth = 1;
        add(new JLabel("Nome do Papel:"), gbc);
        tfNomePapel = new JTextField(20);
        gbc.gridx = 1;
        add(tfNomePapel, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        btSalvar = new JButton("Salvar");
        add(btSalvar, gbc);

        gbc.gridy++;
        btAlterarOperacoes = new JButton("Alterar Operações do Papel");
        add(btAlterarOperacoes, gbc);

        gbc.gridy++;
        btSair = new JButton("Sair do Cadastro de Papeis");
        add(btSair, gbc);

        definirEventos();
    }

    private void definirEventos() {
        btSalvar.addActionListener(e -> {
            String nome = tfNomePapel.getText().trim();
            if (nome.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Informe o nome do papel.",
                        "Atenção",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            Papel papel = new Papel();
            papel.setNomePapel(nome);
            controller.criarPapel(papel);
            tfNomePapel.setText(""); // limpa campo após salvar
        });

        btAlterarOperacoes.addActionListener(e -> {
            String nomePapel = JOptionPane.showInputDialog(this,
                    "Informe o nome do papel que deseja alterar as operações:");
            if (nomePapel == null || nomePapel.trim().isEmpty()) {
                return;
            }

            Papel papel = controller.buscarPorNome(nomePapel.trim());
            if (papel == null) {
                JOptionPane.showMessageDialog(this,
                        "Papel não encontrado.",
                        "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            GestorDasOperacoesDosPapeis gestor
                    = new GestorDasOperacoesDosPapeis(programaPrincipal,
                            papel, this);
            programaPrincipal.setPainelCentral(gestor);

        });

        btSair.addActionListener(e -> {
            programaPrincipal.setMenuAtivo(true);
            programaPrincipal.abrirTelaPrincipal();
        });
    }
}
