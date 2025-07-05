package com.rrg.contabilidade;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author Chat GPT e Ronaldo Rodrigues Godoi
 * 
 * Responsável por criar as tabelas do banco 'Geral'.
 * 
 */
public class CriaTabelasDoBancoDeDadosGeral {

    public static void criarTabelas() {
        try (Connection conexao = AbreBancoGeral.obterConexao();
             Statement stmt = conexao.createStatement()) {

            criarTabelaUsuarios(stmt);
            // Outras tabelas futuras...

            System.out.println("Tabelas criadas com sucesso.");

        } catch (SQLException e) {
            System.err.println("Erro ao criar tabelas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void criarTabelaUsuarios(Statement stmt) throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS usuarios (
                id INT AUTO_INCREMENT PRIMARY KEY,
                nome VARCHAR(100) NOT NULL,
                senha VARCHAR(255) NOT NULL
            )
        """;
        stmt.executeUpdate(sql);
        System.out.println("Tabela 'usuarios' criada.");
    }
}
