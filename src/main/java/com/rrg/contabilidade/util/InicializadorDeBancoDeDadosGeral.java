package com.rrg.contabilidade.util;

import com.rrg.contabilidade.util.ConfiguracaoBanco;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JOptionPane;

/**
 *
 * @author Chat GPT e Ronaldo Rodrigues Godoi
 *
 * Classe responsável por verificar e criar o banco de dados "Geral" no MySQL.
 *
 * Verifica se o banco 'Geral' existe. Se não existir, cria e estrutura o banco.
 *
 */
public class InicializadorDeBancoDeDadosGeral {

    public static void verificarOuCriarBancoGeral() {
        boolean bancoFoiCriado = false;

        // Retira o nome do banco da URL para se conectar apenas ao servidor
        String urlSemBanco = ConfiguracaoBanco.getUrl().replaceFirst("/Geral", "/");
        JOptionPane.showMessageDialog(null, "URL de conexão: " + urlSemBanco);

        try (Connection conexao = DriverManager.getConnection(urlSemBanco,
                ConfiguracaoBanco.getUsuario(),
                ConfiguracaoBanco.getSenha()); Statement stmt = conexao.createStatement()) {

            ResultSet resultado = stmt.executeQuery(
                    "SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = 'Geral'"
            );

            if (!resultado.next()) {
                stmt.executeUpdate("CREATE DATABASE Geral");
                JOptionPane.showMessageDialog(null, "Banco 'Geral' criado.");
                bancoFoiCriado = true;
            } else {
                JOptionPane.showMessageDialog(null, "Banco 'Geral' já existe.");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro ao criar/verificar banco: " + e.getMessage());
            e.printStackTrace();
        }

        if (bancoFoiCriado) {
            // Conecta agora já no banco 'Geral'
            String urlComBanco = ConfiguracaoBanco.getUrl(); // já tem /Geral
            try (Connection conexaoGeral = DriverManager.getConnection(
                    urlComBanco,
                    ConfiguracaoBanco.getUsuario(),
                    ConfiguracaoBanco.getSenha()); Statement stmtGeral = conexaoGeral.createStatement()) {

                CriadorDasTabelasDoBancoDeDadosGeral CTBDGeral;
                CTBDGeral = new CriadorDasTabelasDoBancoDeDadosGeral(conexaoGeral, stmtGeral);
                CTBDGeral.criarTabelas();

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Erro ao criar tabelas no banco 'Geral': " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
