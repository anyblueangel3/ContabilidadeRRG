package com.rrg.contabilidade;

import com.rrg.contabilidade.controller.EmpresaController;
import com.rrg.contabilidade.model.Empresa;
import com.rrg.contabilidade.util.InicializadorDeBancoDeDadosEmpresa;

import javax.swing.*;
import java.awt.*;
import java.util.Optional;

/**
 *
 * @author Ronaldo Rodrigues Godoi e Chat GPT
 *
 * Painel de cadastro de empresas MVC: View + Controller
 */
public class CadastroDeEmpresa extends JPanel {

    private ProgramaPrincipal programaPrincipal;
    private Empresa empresa;
    private EmpresaController controller;

    private JTextField tfCnpj;
    private JTextField tfRazao;
    private JTextField tfEndereco;
    private JTextField tfResponsavel;
    private JTextField tfTelEmpresa;
    private JTextField tfTelResponsavel;
    private JButton btSalvar;
    private JButton btAlterar;
    private JButton btSair;

    public CadastroDeEmpresa(ProgramaPrincipal programaPrincipal, Empresa empresa) {
        this.programaPrincipal = programaPrincipal;
        this.empresa = empresa != null ? empresa : new Empresa();
        this.controller = new EmpresaController();

        inicializarComponentes();
    }

    private void inicializarComponentes() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lbTitulo = new JLabel("Cadastro de Empresa");
        lbTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(lbTitulo, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;

        // CNPJ
        add(new JLabel("CNPJ:"), gbc);
        tfCnpj = new JTextField(20);
        tfCnpj.setText(empresa.getCnpj() != null ? empresa.getCnpj() : "");
        gbc.gridx = 1;
        add(tfCnpj, gbc);

        // Razão Social
        gbc.gridx = 0;
        gbc.gridy++;
        add(new JLabel("Razão Social:"), gbc);
        tfRazao = new JTextField(20);
        tfRazao.setText(empresa.getRazao() != null ? empresa.getRazao() : "");
        gbc.gridx = 1;
        add(tfRazao, gbc);

        // Endereço
        gbc.gridx = 0;
        gbc.gridy++;
        add(new JLabel("Endereço:"), gbc);
        tfEndereco = new JTextField(20);
        tfEndereco.setText(empresa.getEndereco() != null ? empresa.getEndereco() : "");
        gbc.gridx = 1;
        add(tfEndereco, gbc);

        // Responsável
        gbc.gridx = 0;
        gbc.gridy++;
        add(new JLabel("Responsável:"), gbc);
        tfResponsavel = new JTextField(20);
        tfResponsavel.setText(empresa.getResponsavel() != null ? empresa.getResponsavel() : "");
        gbc.gridx = 1;
        add(tfResponsavel, gbc);

        // Telefone Empresa
        gbc.gridx = 0;
        gbc.gridy++;
        add(new JLabel("Telefone Empresa:"), gbc);
        tfTelEmpresa = new JTextField(20);
        tfTelEmpresa.setText(empresa.getTelefoneEmpresa() != null ? empresa.getTelefoneEmpresa() : "");
        gbc.gridx = 1;
        add(tfTelEmpresa, gbc);

        // Telefone Responsável
        gbc.gridx = 0;
        gbc.gridy++;
        add(new JLabel("Telefone Responsável:"), gbc);
        tfTelResponsavel = new JTextField(20);
        tfTelResponsavel.setText(empresa.getTelefoneResponsavel() != null ? empresa.getTelefoneResponsavel() : "");
        gbc.gridx = 1;
        add(tfTelResponsavel, gbc);

        // Botões
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        btSalvar = new JButton("Salvar");
        add(btSalvar, gbc);

        gbc.gridy++;
        btAlterar = new JButton("Alterar");
        add(btAlterar, gbc);

        gbc.gridy++;
        btSair = new JButton("Sair do Cadastro de Empresas");
        add(btSair, gbc);

        definirEventos();
    }

    private void definirEventos() {
        btSalvar.addActionListener(e -> {
            String cnpj = tfCnpj.getText().trim();
            if (cnpj.isEmpty()) {
                JOptionPane.showMessageDialog(this, "CNPJ é obrigatório.", "Atenção", JOptionPane.WARNING_MESSAGE);
                return;
            }

            empresa.setCnpj(cnpj);
            empresa.setRazao(tfRazao.getText().trim());
            empresa.setEndereco(tfEndereco.getText().trim());
            empresa.setResponsavel(tfResponsavel.getText().trim());
            empresa.setTelefoneEmpresa(tfTelEmpresa.getText().trim());
            empresa.setTelefoneResponsavel(tfTelResponsavel.getText().trim());

            if (tfCnpj.isEditable()) { // inserção
                Optional<Empresa> existente = controller.buscarEmpresaPorCnpj(cnpj);
                if (existente.isPresent()) {
                    JOptionPane.showMessageDialog(this, "Empresa já existe. Use Alterar.", "Atenção", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                controller.inserirEmpresa(empresa);
                InicializadorDeBancoDeDadosEmpresa.verificarOuCriarBancoEmpresa(cnpj);
            } else { // atualização
                controller.atualizarEmpresa(empresa);
                // não cria banco, só atualiza
            }

            JOptionPane.showMessageDialog(this, "Operação concluída com sucesso.");
        });

        btAlterar.addActionListener(e -> {
            String cnpj = JOptionPane.showInputDialog(
                    this,
                    "Informe o CNPJ da empresa que deseja alterar:",
                    "Alterar Empresa",
                    JOptionPane.QUESTION_MESSAGE
            );

            if (cnpj == null || cnpj.trim().isEmpty()) {
                return; // cancelou
            }

            Optional<Empresa> existente = controller.buscarEmpresaPorCnpj(cnpj.trim());
            if (existente.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Empresa não encontrada.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            this.empresa = existente.get();
            preencherCamposParaAlteracao(empresa);
        });

        btSair.addActionListener(e -> programaPrincipal.abrirTelaPrincipal());
    }

    private void preencherCamposParaAlteracao(Empresa empresa) {
        tfCnpj.setText(empresa.getCnpj());
        tfCnpj.setEditable(false); // não permite alterar CNPJ
        tfRazao.setText(empresa.getRazao());
        tfEndereco.setText(empresa.getEndereco());
        tfResponsavel.setText(empresa.getResponsavel());
        tfTelEmpresa.setText(empresa.getTelefoneEmpresa());
        tfTelResponsavel.setText(empresa.getTelefoneResponsavel());
    }

}
