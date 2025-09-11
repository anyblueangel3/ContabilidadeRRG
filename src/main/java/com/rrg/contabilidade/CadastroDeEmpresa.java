package com.rrg.contabilidade;

import com.rrg.contabilidade.model.Empresa;
import com.rrg.contabilidade.model.dao.EmpresaDAO;
import com.rrg.contabilidade.util.InicializadorDeBancoDeDadosEmpresa;

import javax.swing.*;
import java.awt.*;

/**
 * Painel de cadastro de empresas MVC: View + Controller leve
 */
public class CadastroDeEmpresa extends JPanel {

    private ProgramaPrincipal programaPrincipal;
    private Empresa empresa;

    private JTextField tfCnpj;
    private JTextField tfRazao;
    private JTextField tfEndereco;
    private JTextField tfResponsavel;
    private JTextField tfTelEmpresa;
    private JTextField tfTelResponsavel;
    private JButton btSalvar;

    public CadastroDeEmpresa(ProgramaPrincipal programaPrincipal, Empresa empresa) {
        this.programaPrincipal = programaPrincipal;
        this.empresa = empresa != null ? empresa : new Empresa();

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

        add(new JLabel("CNPJ:"), gbc);
        tfCnpj = new JTextField(20);
        tfCnpj.setText(empresa.getCnpj() != null ? empresa.getCnpj() : "");
        gbc.gridx = 1;
        add(tfCnpj, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        add(new JLabel("Razão Social:"), gbc);
        tfRazao = new JTextField(20);
        tfRazao.setText(empresa.getRazao() != null ? empresa.getRazao() : "");
        gbc.gridx = 1;
        add(tfRazao, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        add(new JLabel("Endereço:"), gbc);
        tfEndereco = new JTextField(20);
        tfEndereco.setText(empresa.getEndereco() != null ? empresa.getEndereco() : "");
        gbc.gridx = 1;
        add(tfEndereco, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        add(new JLabel("Responsável:"), gbc);
        tfResponsavel = new JTextField(20);
        tfResponsavel.setText(empresa.getResponsavel() != null ? empresa.getResponsavel() : "");
        gbc.gridx = 1;
        add(tfResponsavel, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        add(new JLabel("Telefone Empresa:"), gbc);
        tfTelEmpresa = new JTextField(20);
        tfTelEmpresa.setText(empresa.getTelefoneEmpresa() != null ? empresa.getTelefoneEmpresa() : "");
        gbc.gridx = 1;
        add(tfTelEmpresa, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        add(new JLabel("Telefone Responsável:"), gbc);
        tfTelResponsavel = new JTextField(20);
        tfTelResponsavel.setText(empresa.getTelefoneResponsavel() != null ? empresa.getTelefoneResponsavel() : "");
        gbc.gridx = 1;
        add(tfTelResponsavel, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        btSalvar = new JButton("Salvar");
        add(btSalvar, gbc);

        definirEventos();
    }

    private void definirEventos() {
        btSalvar.addActionListener(e -> {
            String cnpj = tfCnpj.getText().trim();
            String razao = tfRazao.getText().trim();
            String endereco = tfEndereco.getText().trim();
            String responsavel = tfResponsavel.getText().trim();
            String telEmpresa = tfTelEmpresa.getText().trim();
            String telResponsavel = tfTelResponsavel.getText().trim();

            if (cnpj.isEmpty() || razao.isEmpty()) {
                JOptionPane.showMessageDialog(this, "CNPJ e Razão Social são obrigatórios.", "Atenção", JOptionPane.WARNING_MESSAGE);
                return;
            }

            empresa.setCnpj(cnpj);
            empresa.setRazao(razao);
            empresa.setEndereco(endereco);
            empresa.setResponsavel(responsavel);
            empresa.setTelefoneEmpresa(telEmpresa);
            empresa.setTelefoneResponsavel(telResponsavel);

            EmpresaDAO dao = new EmpresaDAO();
            if (dao.buscarPorCnpj(cnpj) == null) {
                dao.inserir(empresa);

                // Criar banco da empresa com o mesmo CNPJ
                InicializadorDeBancoDeDadosEmpresa.verificarOuCriarBancoEmpresa(cnpj);

            } else {
                dao.atualizar(empresa);
            }

            programaPrincipal.abrirTelaPrincipal(null);
        });
    }
}
