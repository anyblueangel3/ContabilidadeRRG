package com.rrg.contabilidade;

import com.rrg.contabilidade.controller.*;
import com.rrg.contabilidade.model.*;
import com.rrg.contabilidade.util.PasswordUtils;
import com.rrg.contabilidade.util.SessaoDeUsuario;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Tela de cadastro de usuários.
 * 
 * @author Ronaldo Rodrigues Godoi e Chat GPT
 * 
 */
public class CadastroDeUsuarios extends JPanel {

    private final ProgramaPrincipal programaPrincipal;
    private Usuario usuario;
    private final UsuarioController usuarioController;
    private final PapelController papelController;
    private final OperacaoController operacaoController;
    private final boolean primeiroAcesso;

    private JTextField tfNome;
    private JTextField tfLogin;
    private JPasswordField pfSenha;
    private JComboBox<Papel> cbPapel;
    private JButton btSalvar;
    private JButton btSair;
    private JButton btAlterarOperacoesUsuario;

    public CadastroDeUsuarios(ProgramaPrincipal programaPrincipal, Usuario usuario, boolean primeiroAcesso) {
        this.programaPrincipal = programaPrincipal;
        this.usuario = usuario != null ? usuario : new Usuario();
        this.usuarioController = new UsuarioController();
        this.papelController = new PapelController();
        this.operacaoController = new OperacaoController();
        this.primeiroAcesso = primeiroAcesso;

        inicializarComponentes();
        carregarDadosNosCampos();
    }

    private void inicializarComponentes() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lbTitulo = new JLabel("Cadastro de Usuário");
        lbTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 3;
        add(lbTitulo, gbc);
        gbc.gridwidth = 1;

        // Campos
        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Nome:"), gbc);
        tfNome = new JTextField(20);
        gbc.gridx = 1; gbc.gridwidth = 2;
        add(tfNome, gbc);

        gbc.gridwidth = 1; gbc.gridx = 0; gbc.gridy = 2;
        add(new JLabel("Login:"), gbc);
        tfLogin = new JTextField(20);
        if (primeiroAcesso) tfLogin.setText(usuario.getLogin());
        gbc.gridx = 1; gbc.gridwidth = 2;
        add(tfLogin, gbc);

        gbc.gridwidth = 1; gbc.gridx = 0; gbc.gridy = 3;
        add(new JLabel("Senha:"), gbc);
        pfSenha = new JPasswordField(20);
        if (primeiroAcesso) pfSenha.setText(usuario.getSenha());
        gbc.gridx = 1; gbc.gridwidth = 2;
        add(pfSenha, gbc);

        gbc.gridwidth = 1; gbc.gridx = 0; gbc.gridy = 4;
        add(new JLabel("Papel:"), gbc);
        cbPapel = new JComboBox<>();
        cbPapel.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Papel) setText(((Papel) value).getNomePapel());
                else setText("");
                return this;
            }
        });
        carregarPapeis();
        gbc.gridx = 1; gbc.gridwidth = 2;
        add(cbPapel, gbc);

        // Botões
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btSalvar = new JButton("Salvar");
        btAlterarOperacoesUsuario = new JButton("Alterar Operações do Usuário");
        btSair = new JButton("Sair do Cadastro de Usuários");
        painelBotoes.add(btSalvar);
        painelBotoes.add(btAlterarOperacoesUsuario);
        painelBotoes.add(btSair);

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 3;
        add(painelBotoes, gbc);

        // Eventos
        btSalvar.addActionListener(e -> salvarUsuario());
        btSair.addActionListener(e -> {
            if (!SessaoDeUsuario.isLogado() && usuario != null && usuario.getId() != null)
                SessaoDeUsuario.logar(usuario);
            programaPrincipal.abrirTelaPrincipal();
        });

        btAlterarOperacoesUsuario.addActionListener(e -> {
            String login = JOptionPane.showInputDialog(this,
                    "Informe o login do usuário para alterar suas operações:",
                    "Alterar Operações do Usuário",
                    JOptionPane.QUESTION_MESSAGE);
            if (login == null || login.trim().isEmpty()) return;

            Usuario u = usuarioController.buscarUsuarioPorLogin(login.trim());
            if (u == null) {
                JOptionPane.showMessageDialog(this,"Usuário não encontrado.","Erro",JOptionPane.ERROR_MESSAGE);
                return;
            }

            GestorDasOperacoesDosUsuarios gestor =
                    new GestorDasOperacoesDosUsuarios(programaPrincipal, u, this);
            programaPrincipal.mostrarPainelTemporario(gestor);
        });
    }

    private void carregarPapeis() {
        List<Papel> papeis = papelController.listarTodosPapeis();
        cbPapel.removeAllItems();
        if (papeis != null) for (Papel p: papeis) cbPapel.addItem(p);
    }

    private void carregarDadosNosCampos() {
        if (usuario.getId() != null) {
            tfNome.setText(usuario.getNome());
            tfLogin.setText(usuario.getLogin());
            pfSenha.setText("");
            selecionarPapel(usuario.getPapel());
        } else if (primeiroAcesso) {
            Papel papelAdmin = papelController.buscarPorNome("ADMIN");
            if (papelAdmin != null) {
                selecionarPapel(papelAdmin.getId());
                cbPapel.setEnabled(false);
            }
        }
    }

    private void selecionarPapel(Integer idPapel) {
        if (idPapel == null) return;
        for (int i=0;i<cbPapel.getItemCount();i++) {
            Papel p = cbPapel.getItemAt(i);
            if (p != null && p.getId() != null && p.getId().equals(idPapel)) {
                cbPapel.setSelectedIndex(i);
                break;
            }
        }
    }

    private void salvarUsuario() {
        String nome = tfNome.getText().trim();
        String login = tfLogin.getText().trim();
        String senhaRaw = new String(pfSenha.getPassword()).trim();
        Papel papel = (Papel) cbPapel.getSelectedItem();

        if (nome.isEmpty() || login.isEmpty() || (usuario.getId()==null && senhaRaw.isEmpty()) || papel==null) {
            JOptionPane.showMessageDialog(this,"Preencha todos os campos (senha obrigatória para novo usuário).","Atenção",JOptionPane.WARNING_MESSAGE);
            return;
        }

        usuario.setNome(nome);
        usuario.setLogin(login);
        if (!senhaRaw.isEmpty()) usuario.setSenha(PasswordUtils.hash(senhaRaw));
        usuario.setPapel(papel.getId());
        usuario.setStatus("ATIVO");

        if (usuario.getId()==null) {
            usuarioController.criarUsuario(usuario);

            if (primeiroAcesso && "ADMIN".equalsIgnoreCase(papel.getNomePapel())) {
                List<Operacao> todasOperacoes = operacaoController.listarTodas();
                usuario.setOperacoes(todasOperacoes);
                usuarioController.atribuirOperacoes(usuario);

                SessaoDeUsuario.logar(usuario);
                JOptionPane.showMessageDialog(this,"Primeiro ADMIN criado e logado com todas as operações atribuídas.");
                return;
            } else {
                OperacaoDoPapelController operacaoDoPapelController = new OperacaoDoPapelController();
                List<Operacao> operacoesDoPapel = operacaoDoPapelController.listarOperacaoPorPapel(papel.getId());
                usuario.setOperacoes(operacoesDoPapel);
                usuarioController.atribuirOperacoes(usuario);

                JOptionPane.showMessageDialog(this,"Usuário criado com sucesso.");
                programaPrincipal.abrirTelaPrincipal();
            }
        } else {
            usuarioController.atualizarUsuario(usuario);
            JOptionPane.showMessageDialog(this,"Usuário atualizado com sucesso.");
        }
    }
}
