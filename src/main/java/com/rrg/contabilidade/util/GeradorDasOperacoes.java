package com.rrg.contabilidade.util;

import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JOptionPane;

/**
 *
 * @author Ronaldo Rodrigues Godoi e Chat GPT
 *
 * Classe responsável por gerar as operações padrão no banco de dados.
 */
public class GeradorDasOperacoes {

    private Statement stmt;

    public GeradorDasOperacoes(Statement stmt) {
        this.stmt = stmt;
    }

    public void gerarOperacoes() {
        try {
            // Operações a serem inseridas
            String[] operacoes = {
                "FAZER_LANCAMENTOS",
                "FAZER_MANUTENCAO_DOS_USUARIOS",
                "EMITIR_DRE",
                "EMITIR_BALANÇO",
                "EMITIR_BALANCETE",
                "FAZER_ARE"
            };

            for (String operacao : operacoes) {
                // Verifica se a operação já existe para evitar duplicidade
                String sqlVerifica = "SELECT COUNT(*) FROM operacoes WHERE operacao='" + operacao + "'";
                try (var rs = stmt.executeQuery(sqlVerifica)) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        System.out.println("Operação '" + operacao + "' já existe, pulando.");
                        continue; // já existe, pula
                    }
                }

                // Insere a operação
                String sqlInserir = "INSERT INTO operacoes (operacao) VALUES ('" + operacao + "')";
                stmt.executeUpdate(sqlInserir);
                System.out.println("Operação '" + operacao + "' inserida com sucesso.");
            }

            JOptionPane.showMessageDialog(null, "Operações padrão geradas com sucesso.");

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao gerar operações padrão: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
