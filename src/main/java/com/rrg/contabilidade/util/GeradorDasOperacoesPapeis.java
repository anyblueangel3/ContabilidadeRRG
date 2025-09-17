package com.rrg.contabilidade.util;

import com.rrg.contabilidade.model.Papel;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author Usuario
 */
class GeradorDasOperacoesPapeis {

    Statement stmt;

    public GeradorDasOperacoesPapeis(Statement stmt) {

        this.stmt = stmt;

    }

    void gerarOperacoes() throws SQLException {

        String sql = "SELECT * FROM papeis";

        try {

            var rs = stmt.executeQuery(sql);
            Papel papel;
            ArrayList<Papel> papeis = new ArrayList<>();

            while (rs.next()) {
                papel = new Papel();
                papel.setId(rs.getInt("id"));
                papel.setNomePapel(rs.getString("nome_papel"));
                papeis.add(papel);
            }

            for (var auxPapel : papeis) {
                if ("ADMIN".equals(auxPapel.getNomePapel())) {
                    populaAdmin(auxPapel);
                } else if ("AUDITOR".equals(auxPapel.getNomePapel())) {
                    populaAuditor(auxPapel);
                } else if ("CONTADOR".equals(auxPapel.getNomePapel())) {
                    populaContador(auxPapel);
                } else if ("USUARIO".equals(auxPapel.getNomePapel())) {
                    populaUsuario(auxPapel);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Erro ao executar SELECT:\n" + e.getMessage(),
                    "Erro de Banco de Dados",
                    JOptionPane.ERROR_MESSAGE);
        }

    }

    private void populaAdmin(Papel papel) throws SQLException {

        String[] sql = {"INSERT INTO operacoes_papeis (id_papel, id_operacao) VALUES (" + papel.getId() + ", 1)",
            "INSERT INTO operacoes_papeis (id_papel, id_operacao) VALUES (" + papel.getId() + ", 2)",
            "INSERT INTO operacoes_papeis (id_papel, id_operacao) VALUES (" + papel.getId() + ", 3)",
            "INSERT INTO operacoes_papeis (id_papel, id_operacao) VALUES (" + papel.getId() + ", 4)",
            "INSERT INTO operacoes_papeis (id_papel, id_operacao) VALUES (" + papel.getId() + ", 5)",
            "INSERT INTO operacoes_papeis (id_papel, id_operacao) VALUES (" + papel.getId() + ", 6)",
        };

        try {
            for (String auxSql : sql) {
                stmt.executeUpdate(auxSql);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Erro ao executar INSERT:\n" + e.getMessage(),
                    "Erro de Banco de Dados",
                    JOptionPane.ERROR_MESSAGE);
        };

    }

    private void populaAuditor(Papel papel) throws SQLException {

        String[] sql = {"INSERT INTO operacoes_papeis (id_papel, id_operacao) "
                + "VALUES (" + papel.getId() + ", 5)",
            "INSERT INTO operacoes_papeis (id_papel, id_operacao)"
                + " VALUES (" + papel.getId() + ", 3)"};

        try {
            for (String auxSql : sql) {
                stmt.executeUpdate(auxSql);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Erro ao executar INSERT:\n" + e.getMessage(),
                    "Erro de Banco de Dados",
                    JOptionPane.ERROR_MESSAGE);
        }

    }

    private void populaContador(Papel papel) throws SQLException {

        String[] sql = {"INSERT INTO operacoes_papeis (id_papel, id_operacao)"
                + " VALUES (" + papel.getId() + ", 5)",
            "INSERT INTO operacoes_papeis (id_papel, id_operacao)"
                + " VALUES (" + papel.getId() + ", 3)"};

        try {
            for (String auxSql : sql) {
                stmt.executeUpdate(auxSql);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Erro ao executar INSERT:\n" + e.getMessage(),
                    "Erro de Banco de Dados",
                    JOptionPane.ERROR_MESSAGE);
        }

    }

    private void populaUsuario(Papel papel) throws SQLException {

        String[] sql = {"INSERT INTO operacoes_papeis (id_papel, id_operacao)"
                + " VALUES (" + papel.getId() + ", 1)",
            "INSERT INTO operacoes_papeis (id_papel, id_operacao)"
                + " VALUES (" + papel.getId() + ", 5)"};

        try {
            for (String auxSql : sql) {
                stmt.executeUpdate(auxSql);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Erro ao executar INSERT:\n" + e.getMessage(),
                    "Erro de Banco de Dados",
                    JOptionPane.ERROR_MESSAGE);
        }

    }

}
