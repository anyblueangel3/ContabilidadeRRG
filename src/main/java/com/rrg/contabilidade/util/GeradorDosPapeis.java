package com.rrg.contabilidade.util;

import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JOptionPane;

/**
 *
 * @author Ronaldo Rodrigues Godoi e Chat GPT
 *
 * Classe responsável por gerar os papéis padrão no banco de dados.
 */
public class GeradorDosPapeis {

    private Statement stmt;

    public GeradorDosPapeis(Statement stmt) {
        this.stmt = stmt;
    }

    public void gerarPapeisPadrao() {
        try {
            // Papéis a serem inseridos
            String[] papeis = {"ADMIN", "CONTADOR", "AUDITOR", "USUARIO", "VAZIO"};

            for (String papel : papeis) {
                // Verifica se o papel já existe para evitar duplicidade
                String sqlVerifica = "SELECT COUNT(*) FROM papeis WHERE nome_papel='" + papel + "'";
                try (var rs = stmt.executeQuery(sqlVerifica)) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        continue; // já existe, pula
                    }
                }

                // Insere o papel
                String sqlInserir = "INSERT INTO papeis (nome_papel) VALUES ('" + papel + "')";
                stmt.executeUpdate(sqlInserir);
            }

            JOptionPane.showMessageDialog(null, "Papéis padrão gerados com sucesso.");

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao gerar papéis padrão: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
