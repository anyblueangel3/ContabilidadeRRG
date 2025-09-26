package com.rrg.contabilidade.util;

import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JOptionPane;

/**
 * Classe responsável por gerar as operações padrão no banco de dados
 * com base no enum OperacoesDoSistema.
 * 
 * @author Ronaldo Rodrigues Godoi e Chat GPT
 */
public class GeradorDasOperacoes {

    private Statement stmt;

    public GeradorDasOperacoes(Statement stmt) {
        this.stmt = stmt;
    }

    public void gerarOperacoes() {
        try {
            for (OperacoesDoSistema operacaoEnum : OperacoesDoSistema.values()) {
                String codigo = operacaoEnum.getOperacao();
                String descricao = operacaoEnum.getDescricao();

                // Verifica se a operação já existe para evitar duplicidade
                String sqlVerifica = "SELECT COUNT(*) FROM operacoes WHERE operacao='" + codigo + "'";
                try (var rs = stmt.executeQuery(sqlVerifica)) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        System.out.println("Operação '" + codigo + "' já existe, pulando.");
                        continue; // já existe, pula
                    }
                }

                // Insere a operação
                String sqlInserir = "INSERT INTO operacoes (operacao, descricao) VALUES ('" 
                        + codigo + "', '" + descricao + "')";
                stmt.executeUpdate(sqlInserir);
                System.out.println("Operação '" + codigo + "' inserida com sucesso.");
            }

            JOptionPane.showMessageDialog(null, "Operações padrão geradas com sucesso.");

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao gerar operações padrão: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
