package com.rrg.contabilidade;

import com.rrg.contabilidade.controller.ContaController;
import com.rrg.contabilidade.controller.EmpresaController;
import com.rrg.contabilidade.controller.PlanoDeContasController;
import com.rrg.contabilidade.model.Conta;
import com.rrg.contabilidade.model.Empresa;
import com.rrg.contabilidade.model.PlanoDeContas;
import com.rrg.contabilidade.util.SessaoDeUsuario;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class CadastroDePlanosDeContasEContas extends JPanel {

    private ProgramaPrincipal programaPrincipal;
    private PlanoDeContasController planoController;
    private ContaController contaController;
    private EmpresaController empresaController;

    private JButton btBancoGeral;
    private JButton btBancoEmpresa;
    private JComboBox<PlanoDeContas> cbPlanos;
    private DefaultListModel<Conta> listModelContas;
    private JList<Conta> listContas;

    private JButton btInserirPlano;
    private JButton btExcluirPlano;
    private JButton btCadastroContas;
    private JButton btVoltar;

    private boolean usarBancoEmpresa;

    public CadastroDePlanosDeContasEContas(ProgramaPrincipal programaPrincipal) {
        this.programaPrincipal = programaPrincipal;
        this.planoController = new PlanoDeContasController();
        this.contaController = new ContaController();
        this.empresaController = new EmpresaController();
        this.usarBancoEmpresa = false;

        inicializarComponentes();
        carregarPlanos();
        atualizarEstadoBotoes();
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout());

        // ===== Painel de banco =====
        JPanel painelBancos = new JPanel();
        btBancoGeral = new JButton("Banco Geral");
        btBancoEmpresa = new JButton("Banco Empresa");
        painelBancos.add(btBancoGeral);
        painelBancos.add(btBancoEmpresa);
        add(painelBancos, BorderLayout.NORTH);

        // ===== Painel centro: planos e contas =====
        JPanel painelCentro = new JPanel(new BorderLayout());

        cbPlanos = new JComboBox<>();
        cbPlanos.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel lbl = new JLabel();
            if (value != null) {
                lbl.setText(value.getNome());
            }
            if (isSelected) {
                lbl.setOpaque(true);
                lbl.setBackground(list.getSelectionBackground());
                lbl.setForeground(list.getSelectionForeground());
            }
            return lbl;
        });
        painelCentro.add(cbPlanos, BorderLayout.NORTH);

        listModelContas = new DefaultListModel<>();
        listContas = new JList<>(listModelContas);
        listContas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listContas.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel lbl = new JLabel();
            if (value != null) {
                lbl.setText(value.getId() + " - " + value.getDescricao());
            }
            if (isSelected) {
                lbl.setOpaque(true);
                lbl.setBackground(list.getSelectionBackground());
                lbl.setForeground(list.getSelectionForeground());
            }
            return lbl;
        });
        JScrollPane scrollContas = new JScrollPane(listContas);
        painelCentro.add(scrollContas, BorderLayout.CENTER);

        JPanel painelAcoes = new JPanel();
        btInserirPlano = new JButton("Inserir Plano");
        btExcluirPlano = new JButton("Excluir Plano");
        btCadastroContas = new JButton("Cadastro de Contas");
        btVoltar = new JButton("Voltar");
        painelAcoes.add(btInserirPlano);
        painelAcoes.add(btExcluirPlano);
        painelAcoes.add(btCadastroContas);
        painelAcoes.add(btVoltar);
        painelCentro.add(painelAcoes, BorderLayout.SOUTH);

        add(painelCentro, BorderLayout.CENTER);

        definirEventos();
    }

    private void definirEventos() {
        btBancoGeral.addActionListener(e -> {
            usarBancoEmpresa = false;
            SessaoDeUsuario.logoutEmpresa();
            carregarPlanos();
            atualizarEstadoBotoes();
            JOptionPane.showMessageDialog(this, "Banco Geral selecionado.");
        });

        btBancoEmpresa.addActionListener(e -> {
            Empresa empresaSelecionada = selecionarEmpresa();
            if (empresaSelecionada != null) {
                SessaoDeUsuario.logarEmpresa(empresaSelecionada);
                usarBancoEmpresa = true;
                carregarPlanos();
                atualizarEstadoBotoes();
                JOptionPane.showMessageDialog(this, "Banco Empresa selecionado: " + empresaSelecionada.getRazao());
            }
        });

        cbPlanos.addActionListener(e -> {
            carregarContasDoPlano();
            atualizarEstadoBotoes();
        });

        listContas.addListSelectionListener(e -> atualizarEstadoBotoes());

        btInserirPlano.addActionListener(e -> inserirPlano());
        btExcluirPlano.addActionListener(e -> excluirPlano());

        btCadastroContas.addActionListener(e -> abrirCadastroContas());

        btVoltar.addActionListener(e -> programaPrincipal.abrirTelaPrincipal());
    }

    private void atualizarEstadoBotoes() {
        PlanoDeContas planoSelecionado = (PlanoDeContas) cbPlanos.getSelectedItem();
        listContas.setEnabled(planoSelecionado != null);
        btCadastroContas.setEnabled(planoSelecionado != null);
        btExcluirPlano.setEnabled(planoSelecionado != null);
    }

    private Empresa selecionarEmpresa() {
        List<Empresa> empresas = empresaController.listarTodasEmpresas();
        if (empresas == null || empresas.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nenhuma empresa cadastrada no banco geral.");
            return null;
        }

        JComboBox<Empresa> cbEmpresas = new JComboBox<>(empresas.toArray(new Empresa[0]));
        cbEmpresas.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel lbl = new JLabel();
            if (value != null) {
                lbl.setText(value.getCnpj() + " - " + value.getRazao());
            }
            if (isSelected) {
                lbl.setOpaque(true);
                lbl.setBackground(list.getSelectionBackground());
                lbl.setForeground(list.getSelectionForeground());
            }
            return lbl;
        });

        int opcao = JOptionPane.showConfirmDialog(this, cbEmpresas,
                "Selecione a empresa", JOptionPane.OK_CANCEL_OPTION);

        if (opcao == JOptionPane.OK_OPTION) {
            return (Empresa) cbEmpresas.getSelectedItem();
        }
        return null;
    }

    private void carregarPlanos() {
        cbPlanos.removeAllItems();
        List<PlanoDeContas> planos = planoController.listarTodos(usarBancoEmpresa);
        if (planos != null) {
            planos.forEach(cbPlanos::addItem);
        }
        carregarContasDoPlano();
        atualizarEstadoBotoes();
    }

    private void carregarContasDoPlano() {
        listModelContas.clear();
        PlanoDeContas plano = (PlanoDeContas) cbPlanos.getSelectedItem();
        if (plano == null) return;

        List<Conta> contas = contaController.listarContasPorPlano(plano.getId(), usarBancoEmpresa);
        if (contas != null) {
            contas.forEach(listModelContas::addElement);
        }
        atualizarEstadoBotoes();
    }

    private void inserirPlano() {
        String nome = JOptionPane.showInputDialog(this, "Informe o nome do novo plano:");
        if (nome == null || nome.trim().isEmpty()) return;

        String descricao = JOptionPane.showInputDialog(this, "Informe a descrição do plano:");
        if (descricao == null) descricao = "";

        PlanoDeContas plano = new PlanoDeContas();
        plano.setNome(nome.trim());
        plano.setDescricao(descricao.trim());

        if (planoController.salvarPlano(plano, usarBancoEmpresa)) {
            carregarPlanos();
            JOptionPane.showMessageDialog(this, "Plano inserido com sucesso!");
        } else {
            JOptionPane.showMessageDialog(this, "Erro ao inserir plano.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void excluirPlano() {
        PlanoDeContas plano = (PlanoDeContas) cbPlanos.getSelectedItem();
        if (plano == null) return;

        int opcao = JOptionPane.showConfirmDialog(this,
                "Deseja excluir o plano " + plano.getNome() + "?",
                "Confirmação", JOptionPane.YES_NO_OPTION);

        if (opcao == JOptionPane.YES_OPTION) {
            if (planoController.excluirPlano(plano.getId(), usarBancoEmpresa)) {
                carregarPlanos();
                JOptionPane.showMessageDialog(this, "Plano excluído com sucesso!");
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao excluir plano.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void abrirCadastroContas() {
        PlanoDeContas planoSelecionado = (PlanoDeContas) cbPlanos.getSelectedItem();
        Conta contaSelecionada = listContas.getSelectedValue();

        if (planoSelecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione um plano primeiro.");
            return;
        }

        CadastroDeContasJDialog dialog = new CadastroDeContasJDialog(
                SwingUtilities.getWindowAncestor(this),
                planoSelecionado,
                contaSelecionada,
                contaController,
                usarBancoEmpresa
        );
        dialog.setVisible(true);

        // Após fechar o diálogo, recarrega as contas
        carregarContasDoPlano();
    }
}
