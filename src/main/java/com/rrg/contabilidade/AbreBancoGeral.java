package com.rrg.contabilidade;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * 
 * @author Chat GPT e Ronaldo Rodrigues Godoi
 * 
 * Classe responsável por fornecer uma conexão com o banco 'Geral'
 * usando as informações carregadas do arquivo de propriedades.
 */
public class AbreBancoGeral {

    public static Connection obterConexao() throws SQLException {
        return DriverManager.getConnection(
            ConfiguracaoBanco.getUrl(),
            ConfiguracaoBanco.getUsuario(),
            ConfiguracaoBanco.getSenha()
        );
    }
}
