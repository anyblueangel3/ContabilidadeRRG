package com.rrg.contabilidade;

import com.rrg.contabilidade.controller.GestorDasOperacoesDosPapeisController;
import com.rrg.contabilidade.model.Operacao;
import com.rrg.contabilidade.model.Papel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Ronaldo Rodrigues Godo e Chat GPT
 *
 * Gerencia as operações permitidas pelo papel
 */
public class GestorDasOperacoesDosPapeis extends JPanel {

    private ProgramaPrincipal programaPrincipal;
    private GestorDasOperacoesDosPapeisController controller;
    private Papel papel;

    private JLabel lbNomePapel;
    private JButton btSalvar;
    private JButton btSair;
    private List<JCheckBox> checkBoxes;

    private CadastroDePapeis cadastroDePapeis; // novo atributo

    public GestorDasOperacoesDosPapeis(ProgramaPrincipal programaPrincipal, Papel papel, CadastroDePapeis cadastroDePapeis) {
        this.programaPrincipal = programaPrincipal;
        this.papel = papel;
        this.cadastroDePapeis = cadastroDePapeis; // guarda referência do chamador
        this.controller = new GestorDasOperacoesDosPapeisController();
        this.checkBoxes = new ArrayList<>();

        inicializarComponentes();
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout());

        lbNomePapel = new JLabel("Operações do Papel: " + papel.getNomePapel());
        lbNomePapel.setFont(new Font("Arial", Font.BOLD, 16));
        add(lbNomePapel, BorderLayout.NORTH);

        JPanel painelCheck = new JPanel();
        painelCheck.setLayout(new BoxLayout(painelCheck, BoxLayout.Y_AXIS));

        // Lista todas as operações e cria checkboxes
        List<Operacao> todasOperacoes = controller.listarTodasOperacoes();
        List<Integer> idsDoPapel = controller.listarOperacoesDoPapel(papel.getId());

        for (Operacao op : todasOperacoes) {
            JCheckBox cb = new JCheckBox(op.getDescricao());
            if (idsDoPapel.contains(op.getId())) {
                cb.setSelected(true);
            }
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
            programaPrincipal.setPainelCentral(cadastroDePapeis);
        });

        painelBotoes.add(btSalvar);
        painelBotoes.add(btSair);

        add(painelBotoes, BorderLayout.SOUTH);
    }

    private void salvarOperacoes() {
        List<Integer> selecionadas = new ArrayList<>();
        for (JCheckBox cb : checkBoxes) {
            if (cb.isSelected()) {
                selecionadas.add(Integer.parseInt(cb.getName()));
            }
        }
        controller.salvarOperacoesDoPapel(papel.getId(), selecionadas);
    }
}
