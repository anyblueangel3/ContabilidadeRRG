package com.rrg.contabilidade;

import com.rrg.contabilidade.controller.GestorDasOperacoesDosUsuariosController;
import com.rrg.contabilidade.model.Operacao;
import com.rrg.contabilidade.model.Usuario;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * Gerencia as operações permitidas pelo usuário.
 *
 * @author Ronaldo Rodrigues Godoi e Chat GPT
 *
 */
public class GestorDasOperacoesDosUsuarios extends JPanel {

    private final ProgramaPrincipal programaPrincipal;
    private final GestorDasOperacoesDosUsuariosController controller;
    private final Usuario usuario;

    private JLabel lbNomeUsuario;
    private JButton btSalvar;
    private JButton btSair;
    private final List<JCheckBox> checkBoxes;

    private CadastroDeUsuarios cadastroDeUsuarios;

    public GestorDasOperacoesDosUsuarios(ProgramaPrincipal programaPrincipal, Usuario usuario) {
        this(programaPrincipal, usuario, null);
    }

    public GestorDasOperacoesDosUsuarios(ProgramaPrincipal programaPrincipal, Usuario usuario, CadastroDeUsuarios cadastroDeUsuarios) {
        this.programaPrincipal = programaPrincipal;
        this.usuario = usuario;
        this.cadastroDeUsuarios = cadastroDeUsuarios;
        this.controller = new GestorDasOperacoesDosUsuariosController();
        this.checkBoxes = new ArrayList<>();

        inicializarComponentes();
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout());

        lbNomeUsuario = new JLabel("Operações do Usuário: " + usuario.getLogin());
        lbNomeUsuario.setFont(new Font("Arial", Font.BOLD, 16));
        add(lbNomeUsuario, BorderLayout.NORTH);

        JPanel painelCheck = new JPanel();
        painelCheck.setLayout(new BoxLayout(painelCheck, BoxLayout.Y_AXIS));

        List<Operacao> todasOperacoes = controller.listarTodasOperacoes();
        List<Integer> idsDoUsuario = controller.listarOperacoesDoUsuario(usuario.getId());

        for (Operacao op : todasOperacoes) {
            JCheckBox cb = new JCheckBox(op.getDescricao());
            if (idsDoUsuario.contains(op.getId())) cb.setSelected(true);
            cb.setName(String.valueOf(op.getId()));
            checkBoxes.add(cb);
            painelCheck.add(cb);
        }

        JScrollPane scrollPane = new JScrollPane(painelCheck);
        add(scrollPane, BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel();
        btSalvar = new JButton("Salvar");
        btSair = new JButton("Sair");

        btSalvar.addActionListener(e -> salvarOperacoes());

        btSair.addActionListener(e -> {
            if (cadastroDeUsuarios != null) {
                programaPrincipal.setPainelCentral(cadastroDeUsuarios);
            } else {
                programaPrincipal.voltarPainelAnterior();
            }
        });

        painelBotoes.add(btSalvar);
        painelBotoes.add(btSair);
        add(painelBotoes, BorderLayout.SOUTH);
    }

    private void salvarOperacoes() {
        List<Integer> selecionadas = new ArrayList<>();
        for (JCheckBox cb : checkBoxes) {
            if (cb.isSelected()) selecionadas.add(Integer.parseInt(cb.getName()));
        }
        controller.salvarOperacoesDoUsuario(usuario.getId(), selecionadas);
        JOptionPane.showMessageDialog(this,"Operações salvas com sucesso!");
    }
}
