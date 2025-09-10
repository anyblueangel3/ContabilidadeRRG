package com.rrg.contabilidade.util;

import com.rrg.contabilidade.util.ConfiguracaoBanco;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 *
 * @author Chat GPT e Ronaldo Rodrigues Godoi
 *
 * Classe responsável por fornecer uma conexão com o banco 'Geral' usando as
 * informações carregadas do arquivo de propriedades.
 */
public class AbreBancoGeral {

    public static Connection obterConexao() throws SQLException {

        try {
            
            Class.forName("com.mysql.cj.jdbc.Driver");
            JOptionPane.showMessageDialog(null, "Driver JDBC carregado com sucesso!");

        } catch (ClassNotFoundException e) {

            JOptionPane.showMessageDialog(null, "Driver JDBC não encontrado no classpath!");
            throw new SQLException("Driver JDBC não encontrado no classpath!", e);
        }

        return DriverManager.getConnection(
                ConfiguracaoBanco.getUrl(),
                ConfiguracaoBanco.getUsuario(),
                ConfiguracaoBanco.getSenha()
        );
    }
}
