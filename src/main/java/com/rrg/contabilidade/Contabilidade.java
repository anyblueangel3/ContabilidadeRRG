package com.rrg.contabilidade;

import com.rrg.contabilidade.model.Usuario;
import com.rrg.contabilidade.model.Papel;
import com.rrg.contabilidade.model.dao.UsuarioDAO;
import com.rrg.contabilidade.model.dao.PapelDAO;
import com.rrg.contabilidade.util.InicializadorDeBancoDeDadosGeral;
import com.rrg.contabilidade.util.PasswordUtils;
import com.rrg.contabilidade.util.SessaoDeUsuario;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 *
 * @author Ronaldo R. Godoi e Chat GPT
 *
 * Login no sistema de Contabilidade
 *
 */
public class Contabilidade extends JFrame {

    private JTextField tfLogin;
    private JLabel lbSenha;
    private JLabel lbLogin;
    private JButton btLogar;
    private JButton btCancelar;
    private JPasswordField pfSenha;
    private static Contabilidade janela;

    public Contabilidade() {
        inicializarComponentes();
        definirEventos();
    }

    private void inicializarComponentes() {
        setTitle("Login no Sistema");
        setBounds(0, 0, 400, 200);
        setLayout(null);

        lbLogin = new JLabel("Login: ");
        tfLogin = new JTextField(160);

        lbSenha = new JLabel("Senha: ");
        pfSenha = new JPasswordField(5);

        btLogar = new JButton("Logar");
        btCancelar = new JButton("Cancelar");

        lbLogin.setBounds(30, 30, 80, 25);
        tfLogin.setBounds(100, 30, 260, 25);

        lbSenha.setBounds(30, 75, 80, 25);
        pfSenha.setBounds(100, 75, 120, 25);

        btLogar.setBounds(20, 120, 100, 25);
        btCancelar.setBounds(125, 120, 100, 25);

        add(lbLogin);
        add(tfLogin);
        add(lbSenha);
        add(pfSenha);
        add(btLogar);
        add(btCancelar);
    }

    private void definirEventos() {
        btLogar.addActionListener(e -> {
            String login = tfLogin.getText().trim();
            String senha = new String(pfSenha.getPassword());

            ProgramaPrincipal programa = new ProgramaPrincipal();

            if (login.isEmpty() || senha.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Informe login e senha.", "Atenção", JOptionPane.WARNING_MESSAGE);
                return;
            }

            UsuarioDAO usuarioDAO = new UsuarioDAO();
            List<Usuario> todosUsuarios = usuarioDAO.listarTodos();

            PapelDAO papelDAO = new PapelDAO();
            Papel papelAdmin = papelDAO.buscarPorNome("ADMIN");

            if (papelAdmin == null) {
                JOptionPane.showMessageDialog(this, "O papel ADMIN não existe. Cadastre-o primeiro na tabela de papeis.");
                return;
            }

            boolean existeAdmin = todosUsuarios.stream()
                    .anyMatch(u -> u.getPapel() != null && u.getPapel().equals(papelAdmin.getId()));

            if (!existeAdmin) {
                // Primeiro acesso: não há ADMIN
                Usuario primeiroUsuario = new Usuario();
                primeiroUsuario.setLogin(login);
                primeiroUsuario.setPapel(papelAdmin.getId());
                primeiroUsuario.setSenha(senha); // será tratado depois no CRUD

                JOptionPane.showMessageDialog(this, "Nenhum ADMIN encontrado. A tela de cadastro de usuário será aberta.");
                programa.abrirCadastroDeUsuarios(primeiroUsuario);
                this.dispose();
                return;
            }

            // Há ADMIN, valida usuário
            Usuario usuario = usuarioDAO.buscarPorLogin(login);

            if (usuario == null) {
                JOptionPane.showMessageDialog(this, "Usuário não encontrado.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!PasswordUtils.verificar(senha, usuario.getSenha())) {
                JOptionPane.showMessageDialog(this, "Senha incorreta.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Login e senha corretos
            JOptionPane.showMessageDialog(this, "Login efetuado com sucesso!");
            SessaoDeUsuario.logar(usuario);
            programa.abrirTelaPrincipal();
            this.dispose();
        });

        btCancelar.addActionListener(e -> System.exit(0));
    }

    public static void main(String[] args) {
        InicializadorDeBancoDeDadosGeral.verificarOuCriarBancoGeral();

        SwingUtilities.invokeLater(() -> {
            janela = new Contabilidade();
            janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            Dimension tela = Toolkit.getDefaultToolkit().getScreenSize();
            janela.setLocation((tela.width - janela.getSize().width) / 2,
                    (tela.height - janela.getSize().height) / 2);
            janela.setVisible(true);
        });
    }
}
