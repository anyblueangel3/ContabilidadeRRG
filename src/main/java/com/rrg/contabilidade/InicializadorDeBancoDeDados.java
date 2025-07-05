package com.rrg.contabilidade;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * 
 * @author Chat GPT e Ronaldo Rodrigues Godoi
 * 
 * Classe responsável por verificar e criar o banco de dados "Geral" no MySQL.
 *
 * Verifica se o banco 'Geral' existe. Se não existir, cria e estrutura o banco.
 * 
 */
public class InicializadorDeBancoDeDados {

    public static void verificarOuCriarBancoGeral() {
        boolean bancoFoiCriado = false;

        // Retira o nome do banco da URL para se conectar apenas ao servidor
        String urlSemBanco = ConfiguracaoBanco.getUrl().replaceFirst("/Geral", "/");

        try (Connection conexao = DriverManager.getConnection(urlSemBanco,
                                                               ConfiguracaoBanco.getUsuario(),
                                                               ConfiguracaoBanco.getSenha());
             Statement stmt = conexao.createStatement()) {

            ResultSet resultado = stmt.executeQuery(
                "SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = 'Geral'"
            );

            if (!resultado.next()) {
                stmt.executeUpdate("CREATE DATABASE Geral");
                System.out.println("Banco 'Geral' criado.");
                bancoFoiCriado = true;
            } else {
                System.out.println("Banco 'Geral' já existe.");
            }

        } catch (Exception e) {
            System.err.println("Erro ao criar/verificar banco: " + e.getMessage());
            e.printStackTrace();
        }

        if (bancoFoiCriado) {
            CriaTabelasDoBancoDeDadosGeral.criarTabelas();
        }
    }
}
