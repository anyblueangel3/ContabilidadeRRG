package com.rrg.contabilidade;

import com.rrg.contabilidade.controller.EmpresaController;
import com.rrg.contabilidade.controller.PeriodoController;
import com.rrg.contabilidade.controller.UsuarioController;
import com.rrg.contabilidade.model.Empresa;
import com.rrg.contabilidade.model.Periodo;
import com.rrg.contabilidade.model.Usuario;
import com.rrg.contabilidade.util.AbreBancoEmpresa;
import com.rrg.contabilidade.util.PasswordUtils;
import com.rrg.contabilidade.util.SessaoDeUsuario;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class LoginUsuarioEmpresaPeriodo extends JDialog {

    private JTextField tfLogin;
    private JPasswordField pfSenha;
    private JComboBox<Empresa> cbEmpresas;
    private JComboBox<Periodo> cbPeriodos;
    private JButton btConfirmar;
    private JButton btCancelar;

    public LoginUsuarioEmpresaPeriodo(JFrame parent) {
        super(parent, "Login do Usuário / Empresa / Período", true);
        inicializarComponentes();
        definirEventos();
        pack();
        setLocationRelativeTo(parent);
    }

    private void inicializarComponentes() {
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.fill = GridBagConstraints.HORIZONTAL;

        // Login
        c.gridx = 0;
        c.gridy = 0;
        add(new JLabel("Login:"), c);
        tfLogin = new JTextField(15);
        c.gridx = 1;
        add(tfLogin, c);

        // Senha
        c.gridx = 0;
        c.gridy = 1;
        add(new JLabel("Senha:"), c);
        pfSenha = new JPasswordField(15);
        c.gridx = 1;
        add(pfSenha, c);

        // Empresas
        c.gridx = 0;
        c.gridy = 2;
        add(new JLabel("Empresa:"), c);
        cbEmpresas = new JComboBox<>();
        cbEmpresas.setEnabled(false);
        c.gridx = 1;
        add(cbEmpresas, c);

        // Períodos
        c.gridx = 0;
        c.gridy = 3;
        add(new JLabel("Período:"), c);
        cbPeriodos = new JComboBox<>();
        cbPeriodos.setEnabled(false);
        c.gridx = 1;
        add(cbPeriodos, c);

        // Botões
        JPanel painelBotoes = new JPanel();
        btConfirmar = new JButton("Confirmar");
        btCancelar = new JButton("Cancelar");
        painelBotoes.add(btConfirmar);
        painelBotoes.add(btCancelar);

        c.gridx = 0;
        c.gridy = 4;
        c.gridwidth = 2;
        add(painelBotoes, c);
    }

    private void definirEventos() {
        btConfirmar.addActionListener(e -> {
            String login = tfLogin.getText().trim();
            String senha = new String(pfSenha.getPassword());

            if (login.isEmpty() || senha.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                        "Informe login e senha.", "Atenção",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            UsuarioController usuarioController = new UsuarioController();
            Usuario usuario = usuarioController.buscarUsuarioPorLogin(login);

            if (usuario == null || !PasswordUtils.verificar(senha,
                    usuario.getSenha())) {
                JOptionPane.showMessageDialog(this,
                        "Usuário ou senha inválidos.", "Erro",
                        JOptionPane.ERROR_MESSAGE);
                SessaoDeUsuario.logout();
                cbEmpresas.setEnabled(false);
                cbEmpresas.removeAllItems();
                cbPeriodos.setEnabled(false);
                cbPeriodos.removeAllItems();
                return;
            }

            // Usuário válido
            SessaoDeUsuario.logar(usuario);
            carregarEmpresas(); // habilita empresas e limpa períodos
        });

        btCancelar.addActionListener(e -> dispose());

        cbEmpresas.addActionListener(e -> {
            Empresa empresaSelecionada = (Empresa) cbEmpresas.getSelectedItem();
            if (empresaSelecionada != null) {
                SessaoDeUsuario.logarEmpresa(empresaSelecionada);
                carregarPeriodos(); // carrega períodos da empresa logada
            } else {
                SessaoDeUsuario.logoutEmpresa();
                cbPeriodos.setEnabled(false);
                cbPeriodos.removeAllItems();
            }
        });

        cbPeriodos.addActionListener(e -> {
            Periodo periodoSelecionado = (Periodo) cbPeriodos.getSelectedItem();
            if (periodoSelecionado != null) {
                SessaoDeUsuario.logarPeriodo(periodoSelecionado);
            } else {
                SessaoDeUsuario.logoutPeriodo();
            }
        });
    }

    private void carregarEmpresas() {
        EmpresaController empresaController = new EmpresaController();
        List<Empresa> empresas = empresaController.listarTodasEmpresas(); // sem filtro por usuário
        cbEmpresas.removeAllItems();
        for (Empresa e : empresas) {
            cbEmpresas.addItem(e);
        }
        cbEmpresas.setEnabled(!empresas.isEmpty());

        // Limpa períodos
        cbPeriodos.setEnabled(false);
        cbPeriodos.removeAllItems();
        SessaoDeUsuario.logoutEmpresa();
        SessaoDeUsuario.logoutPeriodo();
    }

    private void carregarPeriodos() {
        cbPeriodos.removeAllItems();
        cbPeriodos.setEnabled(false);

        Empresa empresaLogada = SessaoDeUsuario.getEmpresaLogada();
        if (empresaLogada == null) return;

        try (Connection conn = AbreBancoEmpresa.obterConexao()) {
            PeriodoController periodoController = new PeriodoController(conn);
            List<Periodo> periodos = periodoController.listarAbertos();
            for (Periodo p : periodos) {
                cbPeriodos.addItem(p);
            }
            cbPeriodos.setEnabled(!periodos.isEmpty());
            if (periodos.isEmpty()) {
                SessaoDeUsuario.logoutPeriodo();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao acessar o banco de dados empresa:\n" + e,
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}

