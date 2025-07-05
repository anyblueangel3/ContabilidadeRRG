package com.rrg.contabilidade;

import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import com.rrg.contabilidade.InicializadorDeBancoDeDados;

/**
 *
 * @author Ronaldo R. Godoi
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
        
        add(tfLogin);
        add(lbSenha);
        add(lbLogin);
        add(btLogar);
        add(btCancelar);
        add(pfSenha);
        
    }
    
    private void definirEventos() {
        
    }
    
    public static void main(String[] args) {
        
        InicializadorDeBancoDeDados.verificarOuCriarBancoGeral();
        
        SwingUtilities.invokeLater(new Runnable() { 
            public void run() {
                janela = new Contabilidade();
                janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                Dimension tela = Toolkit.getDefaultToolkit().getScreenSize();
                janela.setLocation((tela.width - janela.getSize().width) / 2,
                        (tela.height - janela.getSize().height) / 2);
                janela.setVisible(true);
            }
        });
        
    }
    
}