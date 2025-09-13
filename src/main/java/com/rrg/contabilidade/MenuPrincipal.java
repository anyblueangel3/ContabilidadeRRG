package com.rrg.contabilidade;

import com.rrg.contabilidade.model.Usuario;
import com.rrg.contabilidade.util.SessaoDeUsuario;
import javax.swing.*;

/**
 *
 * @author Ronaldo Rodrigues Godoi e Chat GPT
 *
 * JMenuBar do sistema, chama diretamente os painéis de cadastro existentes.
 * Barra de menu principal.
 *
 */
public class MenuPrincipal extends JMenuBar {

    private ProgramaPrincipal programaPrincipal;

    public MenuPrincipal(ProgramaPrincipal programaPrincipal) {
        this.programaPrincipal = programaPrincipal;

        // ===== Menu Cadastros =====
        JMenu menuCadastros = new JMenu("Cadastros");

        JMenuItem miUsuarios = new JMenuItem("Cadastro de Usuários");
        miUsuarios.addActionListener(e -> {
            CadastroDeUsuarios cadastroUsuarios = new CadastroDeUsuarios(programaPrincipal, null);
            programaPrincipal.setPainelCentral(cadastroUsuarios);
        });

        JMenuItem miEmpresas = new JMenuItem("Cadastro de Empresas");
        miEmpresas.addActionListener(e -> {
            CadastroDeEmpresa cadastroEmpresa = new CadastroDeEmpresa(programaPrincipal, null);
            programaPrincipal.setPainelCentral(cadastroEmpresa);
        });

        menuCadastros.add(miUsuarios);
        menuCadastros.add(miEmpresas);

// ===== Item Sair =====
        JMenuItem miSair = new JMenuItem("Sair");
        miSair.addActionListener(e -> programaPrincipal.sairDoSistema());
        
        menuCadastros.addSeparator();
        menuCadastros.add(miSair);

        // ===== Menu Operações =====
        JMenu menuOperacoes = new JMenu("Operações");

        JMenuItem miLancamentos = new JMenuItem("Lançamentos");
        miLancamentos.addActionListener(e -> {
            // TODO: implementar classe de lançamentos
            // programaPrincipal.setPainelCentral(new LancamentosPanel());
        });

        JMenuItem miAre = new JMenuItem("ARE");
        miAre.addActionListener(e -> {
            // TODO: implementar classe ARE
            // programaPrincipal.setPainelCentral(new ArePanel());
        });

        menuOperacoes.add(miLancamentos);
        menuOperacoes.add(miAre);

        // ===== Menu Relatórios =====
        JMenu menuRelatorios = new JMenu("Relatórios");

        JMenuItem miBalancete = new JMenuItem("Balancete");
        miBalancete.addActionListener(e -> {
            // TODO: implementar relatório
        });

        JMenuItem miBalanco = new JMenuItem("Balanço");
        miBalanco.addActionListener(e -> {
            // TODO: implementar relatório
        });

        JMenuItem miDre = new JMenuItem("DRE");
        miDre.addActionListener(e -> {
            // TODO: implementar relatório
        });

        JMenuItem miPlanoContas = new JMenuItem("Plano de Contas");
        miPlanoContas.addActionListener(e -> {
            // TODO: implementar relatório
        });

        menuRelatorios.add(miBalancete);
        menuRelatorios.add(miBalanco);
        menuRelatorios.add(miDre);
        menuRelatorios.add(miPlanoContas);

        // ===== Adiciona menus à barra =====
        add(menuCadastros);
        add(menuOperacoes);
        add(menuRelatorios);
    }

}
