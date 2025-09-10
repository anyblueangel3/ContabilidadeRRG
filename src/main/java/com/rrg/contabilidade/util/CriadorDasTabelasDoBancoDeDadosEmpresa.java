package com.rrg.contabilidade.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JOptionPane;

/**
 * @author Ronaldo Rodrigues Godoi e Chat GPT
 *
 * Classe responsável por criar as tabelas no banco de dados da empresa
 * copiando a estrutura do banco Geral.
 */
public class CriadorDasTabelasDoBancoDeDadosEmpresa {

    private Connection conexao;
    private Statement stmt;

    public CriadorDasTabelasDoBancoDeDadosEmpresa(Connection conexao, Statement stmt) {
        this.conexao = conexao;
        this.stmt = stmt;
    }

    public void criarTabelas() {
        try {
            // Lista de tabelas do banco Geral que serão copiadas
            String[] tabelasParaCopiar = {
                "periodos",
                "planos_de_contas",
                "contas",
                "lancamentos",
                "lancamentos_itens"
            };

            for (String tabela : tabelasParaCopiar) {
                String sql = "CREATE TABLE IF NOT EXISTS " + tabela + " LIKE Geral." + tabela;
                stmt.executeUpdate(sql);
                JOptionPane.showMessageDialog(null,
                        "Tabela '" + tabela + "' criada/copiada do banco Geral com sucesso.");
                System.out.println("Tabela '" + tabela + "' criada/copiada do banco Geral com sucesso.");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Erro ao criar tabelas no banco da empresa: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
