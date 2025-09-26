package com.rrg.contabilidade;

import com.rrg.contabilidade.controller.ContaController;
import com.rrg.contabilidade.model.Classificacao;
import com.rrg.contabilidade.model.Conta;
import com.rrg.contabilidade.model.Natureza;
import com.rrg.contabilidade.model.PlanoDeContas;
import com.rrg.contabilidade.util.ValidadorDeContas;

import javax.swing.*;
import java.awt.*;

public class CadastroDeContasJDialog extends JDialog {

    private ContaController contaController;
    private boolean usarBancoEmpresa;
    private PlanoDeContas plano;
    private Conta conta; // Se for edição, a conta vem preenchida

    private JTextField tfId;
    private JTextField tfCodigoSped;
    private JTextField tfDescricao;
    private JComboBox<Classificacao> cbClassificacao;
    private JComboBox<Natureza> cbNatureza;
    private JCheckBox cbObrigatorioECD;
    private JCheckBox cbObrigatorioECF;

    private JButton btSalvar;
    private JButton btExcluir;
    private JButton btCancelar;

    public CadastroDeContasJDialog(Window owner,
            PlanoDeContas plano,
            Conta conta,
            ContaController contaController,
            boolean usarBancoEmpresa) {
        super(owner, "Cadastro de Conta", ModalityType.APPLICATION_MODAL);
        this.plano = plano;
        this.conta = conta;
        this.contaController = contaController;
        this.usarBancoEmpresa = usarBancoEmpresa;

        inicializarComponentes();
        carregarDados();
        pack();
        setLocationRelativeTo(owner);
    }

    private void inicializarComponentes() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // ID
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Código da Conta:"), gbc);
        tfId = new JTextField(20);
        gbc.gridx = 1;
        add(tfId, gbc);

        // Código SPED
        gbc.gridx = 0;
        gbc.gridy++;
        add(new JLabel("Código SPED:"), gbc);
        tfCodigoSped = new JTextField(20);
        gbc.gridx = 1;
        add(tfCodigoSped, gbc);

        // Descrição
        gbc.gridx = 0;
        gbc.gridy++;
        add(new JLabel("Descrição:"), gbc);
        tfDescricao = new JTextField(30);
        gbc.gridx = 1;
        add(tfDescricao, gbc);

        // Classificação
        gbc.gridx = 0;
        gbc.gridy++;
        add(new JLabel("Classificação:"), gbc);
        cbClassificacao = new JComboBox<>(Classificacao.values());
        gbc.gridx = 1;
        add(cbClassificacao, gbc);

        // Natureza
        gbc.gridx = 0;
        gbc.gridy++;
        add(new JLabel("Natureza:"), gbc);
        cbNatureza = new JComboBox<>(Natureza.values());
        gbc.gridx = 1;
        add(cbNatureza, gbc);

        // Obrigatório ECD
        gbc.gridx = 0;
        gbc.gridy++;
        add(new JLabel("Obrigatório ECD:"), gbc);
        cbObrigatorioECD = new JCheckBox();
        cbObrigatorioECD.setSelected(true);
        gbc.gridx = 1;
        add(cbObrigatorioECD, gbc);

        // Obrigatório ECF
        gbc.gridx = 0;
        gbc.gridy++;
        add(new JLabel("Obrigatório ECF:"), gbc);
        cbObrigatorioECF = new JCheckBox();
        cbObrigatorioECF.setSelected(true);
        gbc.gridx = 1;
        add(cbObrigatorioECF, gbc);

        // Botões
        JPanel painelBotoes = new JPanel();
        btSalvar = new JButton("Salvar");
        btExcluir = new JButton("Excluir");
        btCancelar = new JButton("Cancelar");
        painelBotoes.add(btSalvar);
        painelBotoes.add(btExcluir);
        painelBotoes.add(btCancelar);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        add(painelBotoes, gbc);

        definirEventos();
    }

    private void carregarDados() {
        if (conta != null) {
            tfId.setText(conta.getId());
            tfId.setEnabled(false); // Não permite alterar o id da conta
            tfCodigoSped.setText(String.valueOf(conta.getCodigoSPED()));
            tfDescricao.setText(conta.getDescricao());
            cbClassificacao.setSelectedItem(conta.getClassificacao());
            cbNatureza.setSelectedItem(conta.getNatureza());
            cbObrigatorioECD.setSelected(conta.isObrigatorioECD());
            cbObrigatorioECF.setSelected(conta.isObrigatorioECF());
        } else {
            btExcluir.setEnabled(false); // Só pode excluir se for edição
        }
    }

    private void definirEventos() {
        btSalvar.addActionListener(e -> {
            if (validarCampos()) {
                if (conta == null) {
                    conta = new Conta();
                    conta.setIdPlano(plano.getId());
                }
                conta.setId(tfId.getText().trim());
                conta.setCodigoSPED(Integer.parseInt(tfCodigoSped.getText().trim()));
                conta.setDescricao(tfDescricao.getText().trim());
                conta.setClassificacao((Classificacao) cbClassificacao.getSelectedItem());
                conta.setNatureza((Natureza) cbNatureza.getSelectedItem());
                conta.setObrigatorioECD(cbObrigatorioECD.isSelected());
                conta.setObrigatorioECF(cbObrigatorioECF.isSelected());

                boolean sucesso;
                if (btExcluir.isEnabled()) {
                    sucesso = contaController.alterarConta(conta, usarBancoEmpresa);
                } else {
                    sucesso = contaController.inserirConta(conta, usarBancoEmpresa);
                }

                if (sucesso) {
                    JOptionPane.showMessageDialog(this, "Conta salva com sucesso!");
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Erro ao salvar conta.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btExcluir.addActionListener(e -> {
            if (conta != null) {
                int opcao = JOptionPane.showConfirmDialog(this,
                        "Deseja excluir a conta " + conta.getDescricao() + "?",
                        "Confirmação",
                        JOptionPane.YES_NO_OPTION);
                if (opcao == JOptionPane.YES_OPTION) {
                    boolean sucesso = contaController.excluirConta(
                            conta.getId(), conta.getIdPlano(), usarBancoEmpresa);
                    if (sucesso) {
                        JOptionPane.showMessageDialog(this, "Conta excluída com sucesso!");
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(this, "Erro ao excluir conta.", "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        btCancelar.addActionListener(e -> dispose());
    }

    private boolean validarCampos() {
        if (tfId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Informe o código da conta.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (tfCodigoSped.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Informe o código SPED.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (tfDescricao.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Informe a descrição da conta.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        try {
            Integer.parseInt(tfCodigoSped.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Código SPED deve ser numérico.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (tfId.isEnabled()) {

            // Validação da estrutura hierárquica da conta
            ValidadorDeContas validador = new ValidadorDeContas(contaController, usarBancoEmpresa);
            String idConta = tfId.getText().trim();

            if (!validador.podeCadastrarConta(idConta, plano.getId())) {
                JOptionPane.showMessageDialog(this,
                        "Não é possível cadastrar a conta " + idConta + ".\n"
                        + "Certifique-se de que todas as contas de nível superior já estão cadastradas.",
                        "Conta inválida",
                        JOptionPane.WARNING_MESSAGE);
                return false;
            }

        }

        return true;
    }
}
