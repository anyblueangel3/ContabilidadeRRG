package com.rrg.contabilidade.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JOptionPane;

/*
 *
 * Criador Ronaldo Rodrigues Godoi e Chat GPT
 * 
 */
public class GeradorDosPlanosDeContasPadrao {

    Connection conexao;
    Statement stmt;

    public GeradorDosPlanosDeContasPadrao(Connection conexao) {

        this.conexao = conexao;

    }

    public void gerarPlanosPadrao(Statement stmt) {

        this.stmt = stmt;

        gerarIndustrial();
        gerarComercial();
        gerarPrestacaoDeServico();
        //gerarPrestacaoDeServico();

    }

    public void gerarIndustrial() {
        try {
            // 1. Cria o plano "Industrial"
            String sqlPlano = """
            INSERT INTO planos_de_contas (nome, descricao)
            VALUES ('Industrial', 'Plano de contas para empresa industrial')
        """;
            stmt.executeUpdate(sqlPlano, Statement.RETURN_GENERATED_KEYS);

            int idPlano = -1;
            try (var rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    idPlano = rs.getInt(1);
                }
            }

            if (idPlano == -1) {
                throw new SQLException("Falha ao obter ID do plano de contas Industrial.");
            }

            // 2. Inserir contas do plano industrial
            inserirConta("1", 1, "Ativo", "S", "DEVEDORA", idPlano, true, true);
            inserirConta("1.1", 1, "Ativo Circulante", "S", "DEVEDORA", idPlano, true, true);
            inserirConta("1.1.1", 1, "Caixa", "A", "DEVEDORA", idPlano, true, true);
            inserirConta("1.1.2", 1, "Bancos Conta Movimento", "A", "DEVEDORA", idPlano, true, true);
            inserirConta("1.1.3", 1, "Clientes", "A", "DEVEDORA", idPlano, true, true);

            inserirConta("2", 2, "Passivo", "S", "CREDORA", idPlano, true, true);
            inserirConta("2.1", 2, "Passivo Circulante", "S", "CREDORA", idPlano, true, true);
            inserirConta("2.1.1", 2, "Fornecedores", "A", "CREDORA", idPlano, true, true);
            inserirConta("2.1.2", 2, "Empréstimos e Financiamentos", "A", "CREDORA", idPlano, true, true);

            inserirConta("3", 3, "Patrimônio Líquido", "S", "CREDORA", idPlano, true, true);
            inserirConta("3.1", 3, "Capital Social", "A", "CREDORA", idPlano, true, true);

            inserirConta("4", 4, "Receita Operacional", "S", "CREDORA", idPlano, true, true);
            inserirConta("4.1", 4, "Receita Bruta de Vendas", "A", "CREDORA", idPlano, true, true);

            inserirConta("5", 5, "Custos dos Produtos Vendidos", "S", "DEVEDORA", idPlano, true, true);
            inserirConta("5.1", 5, "Matéria-Prima Consumida", "A", "DEVEDORA", idPlano, true, true);

            inserirConta("6", 6, "Despesas Operacionais", "S", "DEVEDORA", idPlano, true, true);
            inserirConta("6.1", 6, "Despesas Administrativas", "A", "DEVEDORA", idPlano, true, true);
            inserirConta("6.2", 6, "Despesas Comerciais", "A", "DEVEDORA", idPlano, true, true);

            JOptionPane.showMessageDialog(null, "Plano de contas Industrial gerado com sucesso.");

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao gerar plano de contas Industrial:\n " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Método auxiliar para inserir contas no banco.
     */
    private void inserirConta(String id, int codigoSped, String descricao,
            String classificacao, String natureza, int idPlano,
            boolean obrigatorioEcd, boolean obrigatorioEcf) throws SQLException {
        String sqlConta = """
        INSERT INTO contas (id, codigo_sped, descricao, classificacao,
                          natureza, id_plano, obrigatorio_ecd, obrigatorio_ecf)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?)
    """;

        try (var ps = conexao.prepareStatement(sqlConta)) {
            ps.setString(1, id);
            ps.setInt(2, codigoSped);
            ps.setString(3, descricao);
            ps.setString(4, classificacao);
            ps.setString(5, natureza);
            ps.setInt(6, idPlano);
            ps.setBoolean(7, obrigatorioEcd);
            ps.setBoolean(8, obrigatorioEcf);
            ps.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Erro ao gerar plano de contas Industrial:\n " + e.getMessage());
            e.printStackTrace();
        }

    }

    public void gerarComercial() {
        try {
            // 1) Inserir o plano de contas Comercial
            String insertPlano = """
                INSERT INTO planos_de_contas (nome, descricao)
                VALUES ('Plano Comercial', 'Plano de contas para empresa comercial')
            """;
            stmt.executeUpdate(insertPlano, Statement.RETURN_GENERATED_KEYS);

            int idPlano = 0;
            try (var rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    idPlano = rs.getInt(1);
                }
            }

            // 2) Inserir contas principais usando o método inserirConta
            inserirConta("1", 1, "Ativo", "S", "DEVEDORA", idPlano, true, true);
            inserirConta("1.1", 1, "Ativo Circulante", "S", "DEVEDORA", idPlano, true, true);
            inserirConta("1.1.1", 1, "Caixa", "A", "DEVEDORA", idPlano, true, true);
            inserirConta("1.1.2", 1, "Bancos Conta Movimento", "A", "DEVEDORA", idPlano, true, true);
            inserirConta("1.1.3", 1, "Estoques de Mercadorias", "A", "DEVEDORA", idPlano, true, true);

            inserirConta("2", 2, "Passivo", "S", "CREDORA", idPlano, true, true);
            inserirConta("2.1", 2, "Passivo Circulante", "S", "CREDORA", idPlano, true, true);
            inserirConta("2.1.1", 2, "Fornecedores", "A", "CREDORA", idPlano, true, true);
            inserirConta("2.1.2", 2, "Salários a Pagar", "A", "CREDORA", idPlano, true, true);

            inserirConta("2.2", 2, "Patrimônio Líquido", "S", "CREDORA", idPlano, true, true);
            inserirConta("2.2.1", 2, "Capital Social", "A", "CREDORA", idPlano, true, true);

            inserirConta("3", 3, "Receitas Operacionais", "S", "CREDORA", idPlano, true, true);
            inserirConta("3.1", 3, "Receita de Vendas de Mercadorias", "A", "CREDORA", idPlano, true, true);

            inserirConta("4", 4, "Custos", "S", "DEVEDORA", idPlano, true, true);
            inserirConta("4.1", 4, "Custo das Mercadorias Vendidas (CMV)", "A", "DEVEDORA", idPlano, true, true);

            inserirConta("5", 5, "Despesas Operacionais", "S", "DEVEDORA", idPlano, true, true);
            inserirConta("5.1", 5, "Despesas Comerciais", "A", "DEVEDORA", idPlano, true, true);
            inserirConta("5.2", 5, "Despesas Administrativas", "A", "DEVEDORA", idPlano, true, true);

            JOptionPane.showMessageDialog(null, "Plano de contas Comercial gerado com sucesso.");

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Erro ao gerar plano de contas Comercial:\n " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void gerarPrestacaoDeServico() {
        try {
            // 1) Inserir o plano de contas Prestação de Serviço
            String sqlPlano = """
            INSERT INTO planos_de_contas (nome, descricao)
            VALUES ('Prestação de Serviço', 'Plano de contas para empresas prestadoras de serviço')
        """;
            stmt.executeUpdate(sqlPlano, Statement.RETURN_GENERATED_KEYS);

            int idPlano = -1;
            try (var rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    idPlano = rs.getInt(1);
                }
            }

            if (idPlano == -1) {
                throw new SQLException("Falha ao obter ID do plano de contas Prestação de Serviço.");
            }

            // 2) Inserir contas do plano Prestação de Serviço
            inserirConta("1", 1, "Ativo", "S", "DEVEDORA", idPlano, true, true);
            inserirConta("1.1", 1, "Ativo Circulante", "S", "DEVEDORA", idPlano, true, true);
            inserirConta("1.1.1", 1, "Caixa", "A", "DEVEDORA", idPlano, true, true);
            inserirConta("1.1.2", 1, "Bancos Conta Movimento", "A", "DEVEDORA", idPlano, true, true);
            inserirConta("1.1.3", 1, "Clientes", "A", "DEVEDORA", idPlano, true, true);

            inserirConta("2", 2, "Passivo", "S", "CREDORA", idPlano, true, true);
            inserirConta("2.1", 2, "Passivo Circulante", "S", "CREDORA", idPlano, true, true);
            inserirConta("2.1.1", 2, "Fornecedores", "A", "CREDORA", idPlano, true, true);
            inserirConta("2.1.2", 2, "Empréstimos e Financiamentos", "A", "CREDORA", idPlano, true, true);

            inserirConta("3", 3, "Patrimônio Líquido", "S", "CREDORA", idPlano, true, true);
            inserirConta("3.1", 3, "Capital Social", "A", "CREDORA", idPlano, true, true);

            inserirConta("4", 4, "Receita Operacional", "S", "CREDORA", idPlano, true, true);
            inserirConta("4.1", 4, "Receita de Serviços Prestados", "A", "CREDORA", idPlano, true, true);

            inserirConta("5", 5, "Custos dos Serviços Prestados", "S", "DEVEDORA", idPlano, true, true);
            inserirConta("5.1", 5, "Despesas com Pessoal", "A", "DEVEDORA", idPlano, true, true);
            inserirConta("5.2", 5, "Despesas Operacionais", "A", "DEVEDORA", idPlano, true, true);

            inserirConta("6", 6, "Despesas Administrativas", "S", "DEVEDORA", idPlano, true, true);
            inserirConta("6.1", 6, "Aluguel e Manutenção", "A", "DEVEDORA", idPlano, true, true);
            inserirConta("6.2", 6, "Serviços Terceirizados", "A", "DEVEDORA", idPlano, true, true);

            JOptionPane.showMessageDialog(null, "Plano de contas Prestação de Serviço gerado com sucesso.");

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Erro ao gerar plano de contas Prestação de Serviço:\n " + e.getMessage());
            e.printStackTrace();
        }
    }

}
