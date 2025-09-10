package com.rrg.contabilidade.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JOptionPane;

/**
 * @author Ronaldo Rodrigues Godoi e Chat GPT
 *
 * Classe responsável por criar/verificar o banco de dados de uma empresa.
 * O nome do banco é a string CNPJ.
 */
public class InicializadorDeBancoDeDadosEmpresa {

    /**
     * Verifica se o banco da empresa existe. Caso não exista, cria o banco e as tabelas básicas.
     * @param cnpj Nome do banco da empresa (string CNPJ)
     */
    public static void verificarOuCriarBancoEmpresa(String cnpj) {
        if (cnpj == null || cnpj.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "CNPJ inválido para criar banco da empresa.");
            return;
        }

        boolean bancoFoiCriado = false;

        // Retira o nome do banco da URL para se conectar apenas ao servidor
        String urlSemBanco = ConfiguracaoBanco.getUrl().replaceFirst("/Geral", "/");

        try (Connection conexao = DriverManager.getConnection(urlSemBanco,
                ConfiguracaoBanco.getUsuario(),
                ConfiguracaoBanco.getSenha());
             Statement stmt = conexao.createStatement()) {

            ResultSet resultado = stmt.executeQuery(
                    "SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = '" + cnpj + "'"
            );

            if (!resultado.next()) {
                stmt.executeUpdate("CREATE DATABASE `" + cnpj + "`");
                JOptionPane.showMessageDialog(null, "Banco da empresa '" + cnpj + "' criado.");
                bancoFoiCriado = true;
            } else {
                JOptionPane.showMessageDialog(null, "Banco da empresa '" + cnpj + "' já existe.");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro ao criar/verificar banco da empresa: " + e.getMessage());
            e.printStackTrace();
        }

        if (bancoFoiCriado) {
            // Conecta agora já no banco da empresa
            String urlComBanco = ConfiguracaoBanco.getUrl().replace("/Geral", "/" + cnpj);

            try (Connection conexaoEmpresa = DriverManager.getConnection(
                    urlComBanco,
                    ConfiguracaoBanco.getUsuario(),
                    ConfiguracaoBanco.getSenha());
                 Statement stmtEmpresa = conexaoEmpresa.createStatement()) {

                CriadorDasTabelasDoBancoDeDadosEmpresa criador = 
                        new CriadorDasTabelasDoBancoDeDadosEmpresa(conexaoEmpresa, stmtEmpresa);
                criador.criarTabelas();

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Erro ao criar tabelas no banco da empresa '" + cnpj + "': " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
