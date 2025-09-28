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

    private JButton btLogarUsuario;
    private JButton btLogarEmpresa;
    private JButton btLogarPeriodo;
    private JButton btVoltarMenu;

    private ProgramaPrincipal pP;

    public LoginUsuarioEmpresaPeriodo(ProgramaPrincipal parent) {
        super(parent, "Login do Usuário / Empresa / Período", true);
        pP = parent;
        setMinimumSize(new Dimension(500, 250));
        inicializarComponentes();
        definirEventos();

        // Se já houver usuário logado, carrega lista de empresas automaticamente
        if (SessaoDeUsuario.isLogado()) {
            carregarEmpresas();
            btLogarEmpresa.setEnabled(true);

            // Se também já houver empresa logada
            if (SessaoDeUsuario.isEmpresaLogada()) {
                carregarPeriodos();
                btLogarPeriodo.setEnabled(true);
            }
        }

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

        // Botão Logar Usuário
        btLogarUsuario = new JButton("Logar Usuário");
        c.gridx = 2;
        add(btLogarUsuario, c);

        // Empresas
        c.gridx = 0;
        c.gridy = 2;
        add(new JLabel("Empresa:"), c);
        cbEmpresas = new JComboBox<>();
        cbEmpresas.setEnabled(false);
        c.gridx = 1;
        add(cbEmpresas, c);

        // Botão Logar Empresa
        btLogarEmpresa = new JButton("Logar Empresa");
        btLogarEmpresa.setEnabled(false);
        c.gridx = 2;
        add(btLogarEmpresa, c);

        // Períodos
        c.gridx = 0;
        c.gridy = 3;
        add(new JLabel("Período:"), c);
        cbPeriodos = new JComboBox<>();
        cbPeriodos.setEnabled(false);
        c.gridx = 1;
        add(cbPeriodos, c);

        // Botão Logar Período
        btLogarPeriodo = new JButton("Logar Período");
        btLogarPeriodo.setEnabled(false);
        c.gridx = 2;
        add(btLogarPeriodo, c);

        // Botão Voltar ao Menu Principal
        btVoltarMenu = new JButton("Voltar ao Menu Principal");
        JPanel painelVoltar = new JPanel();
        painelVoltar.add(btVoltarMenu);
        c.gridx = 0;
        c.gridy = 4;
        c.gridwidth = 3;
        c.anchor = GridBagConstraints.CENTER;
        add(painelVoltar, c);
    }

    private void definirEventos() {
        // Botão Logar Usuário
        btLogarUsuario.addActionListener(e -> {
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

            if (usuario == null || !PasswordUtils.verificar(senha, usuario.getSenha())) {
                JOptionPane.showMessageDialog(this,
                        "Usuário ou senha inválidos.", "Erro",
                        JOptionPane.ERROR_MESSAGE);
                SessaoDeUsuario.logout();
                cbEmpresas.setEnabled(false);
                cbEmpresas.removeAllItems();
                cbPeriodos.setEnabled(false);
                cbPeriodos.removeAllItems();
                btLogarEmpresa.setEnabled(false);
                btLogarPeriodo.setEnabled(false);
                return;
            }

            SessaoDeUsuario.logar(usuario);
            carregarEmpresas();
            btLogarEmpresa.setEnabled(true);
        });

        // Botão Logar Empresa
        btLogarEmpresa.addActionListener(e -> {
            Empresa empresaSelecionada = (Empresa) cbEmpresas.getSelectedItem();
            if (empresaSelecionada != null) {
                SessaoDeUsuario.logarEmpresa(empresaSelecionada);
                carregarPeriodos();
                btLogarPeriodo.setEnabled(true);
            } else {
                SessaoDeUsuario.logoutEmpresa();
                cbPeriodos.setEnabled(false);
                cbPeriodos.removeAllItems();
                btLogarPeriodo.setEnabled(false);
            }
        });

        // Botão Logar Período
        btLogarPeriodo.addActionListener(e -> {
            Periodo periodoSelecionado = (Periodo) cbPeriodos.getSelectedItem();
            if (periodoSelecionado != null) {
                SessaoDeUsuario.logarPeriodo(periodoSelecionado);
            } else {
                SessaoDeUsuario.logoutPeriodo();
            }
        });

        // Botão Voltar ao Menu Principal
        btVoltarMenu.addActionListener(e -> dispose());
    }

    private void carregarEmpresas() {
        EmpresaController empresaController = new EmpresaController();
        List<Empresa> empresas = empresaController.listarTodasEmpresas();
        cbEmpresas.removeAllItems();
        for (Empresa e : empresas) {
            cbEmpresas.addItem(e);
        }
        cbEmpresas.setEnabled(!empresas.isEmpty());

        cbEmpresas.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Empresa empresa) {
                    setText(empresa.getCnpj() + "-" + empresa.getRazao());
                }
                return this;
            }
        });

        // Limpa períodos
        cbPeriodos.removeAllItems();
        cbPeriodos.setEnabled(false);
        btLogarPeriodo.setEnabled(false);
    }

    private void carregarPeriodos() {
        List<Periodo> periodos = List.of(); // default vazio
        try (Connection conn = AbreBancoEmpresa.obterConexao()) {
            PeriodoController periodoController = new PeriodoController(conn);
            periodos = periodoController.listarAbertos();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao acessar o banco de dados empresa:\n" + e);
        }

        cbPeriodos.removeAllItems();
        for (Periodo p : periodos) {
            cbPeriodos.addItem(p);
        }
        cbPeriodos.setEnabled(!periodos.isEmpty());

        cbPeriodos.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Periodo periodo) {
                    setText(periodo.getInicio() + " " + periodo.getFim());
                }
                return this;
            }
        });
    }

//    @Override
//    public void dispose() {
//        super.dispose();
//        if (getParent() instanceof ProgramaPrincipal pp) {
//            if (SessaoDeUsuario.isLogado()) {
//                pp.abrirTelaPrincipal();
//            }
//        }
//    }
    
    @Override
    public void dispose() {
        super.dispose();
        pP.abrirTelaPrincipal();
    }

}
