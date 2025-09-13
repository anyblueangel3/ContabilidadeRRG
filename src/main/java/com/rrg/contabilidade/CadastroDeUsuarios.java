package com.rrg.contabilidade;

import com.rrg.contabilidade.controller.PapelController;
import com.rrg.contabilidade.controller.UsuarioController;
import com.rrg.contabilidade.model.Papel;
import com.rrg.contabilidade.model.Usuario;
import com.rrg.contabilidade.model.dao.PapelDAO;
import com.rrg.contabilidade.util.PasswordUtils;
import com.rrg.contabilidade.util.SessaoDeUsuario;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/*
 * @author Ronaldo Rodrigues Godoi e Chat GPT
 *
 * Cadastro de usuários...
 */
public class CadastroDeUsuarios extends JPanel {
    
    private ProgramaPrincipal programaPrincipal;
    private Usuario usuario;
    private UsuarioController controller;
    
    private JTextField tfNome;
    private JTextField tfLogin;
    private JPasswordField pfSenha;
    private JComboBox<Papel> cbPapel;
    private JButton btSalvar;
    private JButton btAlterar;
    private JButton btSair;
    
    public CadastroDeUsuarios(ProgramaPrincipal programaPrincipal, Usuario usuario) {
        this.programaPrincipal = programaPrincipal;
        // this.primeiroAcesso = usuario != null ? false : true;
        this.usuario = usuario != null ? usuario : new Usuario();
        this.controller = new UsuarioController();
        
        inicializarComponentes();
    }
    
    private void inicializarComponentes() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Título
        JLabel lbTitulo = new JLabel("Cadastro de Usuário");
        lbTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(lbTitulo, gbc);
        
        gbc.gridwidth = 1;
        gbc.gridy++;

        // Nome
        add(new JLabel("Nome:"), gbc);
        tfNome = new JTextField(20);
        tfNome.setText(usuario.getNome() != null ? usuario.getNome() : "");
        gbc.gridx = 1;
        add(tfNome, gbc);

        // Login
        gbc.gridx = 0;
        gbc.gridy++;
        add(new JLabel("Login:"), gbc);
        tfLogin = new JTextField(20);
        tfLogin.setText(usuario.getLogin() != null ? usuario.getLogin() : "");
        gbc.gridx = 1;
        add(tfLogin, gbc);

        // Senha
        gbc.gridx = 0;
        gbc.gridy++;
        add(new JLabel("Senha:"), gbc);
        pfSenha = new JPasswordField(20);
        pfSenha.setText(""); // Não exibe a senha
        gbc.gridx = 1;
        add(pfSenha, gbc);

        // Papel
        gbc.gridx = 0;
        gbc.gridy++;
        add(new JLabel("Papel:"), gbc);
        cbPapel = new JComboBox<>();
        preencherPapeis();
        if (usuario.getPapel() != null) {
            selecionarPapel(usuario.getPapel());
        }
        gbc.gridx = 1;
        add(cbPapel, gbc);

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
        btSair = new JButton("Sair do Cadastro de Usuários");
        add(btSair, gbc);
        
        definirEventos();
    }
    
    private void preencherPapeis() {
        PapelController controller = new PapelController();
        List<Papel> papeis = controller.listarTodosPapeis(); // usa controller agora
        DefaultComboBoxModel<Papel> model = new DefaultComboBoxModel<>();
        for (Papel p : papeis) {
            model.addElement(p);
        }
        cbPapel.setModel(model);
        cbPapel.setRenderer((list, value, index, isSelected, cellHasFocus)
                -> new JLabel(value != null ? value.getNomePapel() : ""));
    }
    
    private void selecionarPapel(Integer papelId) {
        if (papelId != null) {
            for (int i = 0; i < cbPapel.getItemCount(); i++) {
                Papel p = cbPapel.getItemAt(i);
                if (p.getId().equals(papelId)) {
                    cbPapel.setSelectedIndex(i);
                    break;
                }
            }
        } else {
            cbPapel.setSelectedIndex(-1);
        }
    }
    
    private void definirEventos() {
        // Salvar ou criar usuário
        btSalvar.addActionListener(e -> {
            String nome = tfNome.getText().trim();
            String login = tfLogin.getText().trim();
            String senha = new String(pfSenha.getPassword()).trim();
            Papel papelSelecionado = (Papel) cbPapel.getSelectedItem();
            
            if (nome.isEmpty()
                    || login.isEmpty()
                    || senha.isEmpty() || papelSelecionado == null) {
                JOptionPane.showMessageDialog(this,
                        "Preencha todos os campos.",
                        "Atenção", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            usuario.setNome(nome);
            usuario.setLogin(login);
            usuario.setSenha(PasswordUtils.hash(senha));
            usuario.setPapel(papelSelecionado.getId());
            usuario.setStatus("ATIVO");
            
            if (usuario.getId() == null) {
                controller.criarUsuario(usuario);
            } else {
                controller.atualizarUsuario(usuario);
            }
        });

        // Alterar usuário existente
        btAlterar.addActionListener(e -> {
            String login = JOptionPane.showInputDialog(this,
                    "Informe o login do usuário que deseja alterar:",
                    "Alterar Usuário", JOptionPane.QUESTION_MESSAGE);
            if (login == null || login.trim().isEmpty()) {
                return;
            }
            
            Usuario usuarioExistente = controller.buscarUsuarioPorLogin(login.trim());
            if (usuarioExistente == null) {
                JOptionPane.showMessageDialog(this,
                        "Usuário não encontrado.",
                        "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            this.usuario = usuarioExistente;
            tfNome.setText(usuarioExistente.getNome());
            tfLogin.setText(usuarioExistente.getLogin());
            pfSenha.setText("");
            selecionarPapel(usuarioExistente.getPapel());
        });

        // Sair do painel
        btSair.addActionListener(e -> {            
            programaPrincipal.abrirTelaPrincipal();
            if (!SessaoDeUsuario.isLogado()) {
                SessaoDeUsuario.logar(usuario);
            }
        });
    }
}
