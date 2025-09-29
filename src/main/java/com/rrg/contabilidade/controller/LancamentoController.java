package com.rrg.contabilidade.controller;

import com.rrg.contabilidade.model.Lancamento;
import com.rrg.contabilidade.model.dao.LancamentoDAO;
import com.rrg.contabilidade.util.AbreBancoEmpresa;

import javax.swing.*;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Controller responsável por abrir a conexão, controlar a transação
 * e delegar a persistência ao DAO (padrão: Controller controla transaction boundary).
 */
public class LancamentoController {

    private final LancamentoDAO dao;

    public LancamentoController() {
        this.dao = new LancamentoDAO();
    }

    /**
     * Salva um lançamento (assumimos que o objeto já foi montado/consistido).
     * Retorna true em caso de sucesso, false em caso de erro.
     */
    public boolean salvarLancamento(Lancamento lancamento) {
        if (lancamento == null) {
            throw new IllegalArgumentException("Lançamento não pode ser nulo");
        }

        // mínima verificação: deve haver itens
        if (lancamento.getLancamentoItens() == null || lancamento.getLancamentoItens().isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    "Lançamento sem itens. Informe pelo menos um débito e um crédito.",
                    "Atenção",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }

        try (Connection conn = AbreBancoEmpresa.obterConexao()) {
            try {
                conn.setAutoCommit(false);

                // 1) inserir registro principal e obter id
                long idGerado = dao.inserirLancamento(lancamento, conn);
                lancamento.setId(idGerado);

                // 2) inserir itens com o id gerado
                dao.inserirItensLancamento(lancamento, conn);

                // 3) commit (tudo OK)
                conn.commit();

                JOptionPane.showMessageDialog(null, "Lançamento gravado com sucesso.");
                return true;

            } catch (SQLException ex) {
                // tenta rollback e informa o usuário
                try {
                    conn.rollback();
                } catch (SQLException rbEx) {
                    rbEx.printStackTrace();
                }
                JOptionPane.showMessageDialog(null,
                        "Erro ao gravar lançamento: " + ex.getMessage(),
                        "Erro",
                        JOptionPane.ERROR_MESSAGE);
                return false;
            } finally {
                // restaura autoCommit por segurança
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException ignore) {}
            }
        } catch (SQLException connEx) {
            JOptionPane.showMessageDialog(null,
                    "Erro ao abrir conexão com o banco da empresa: " + connEx.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    /**
     * Busca um lançamento por id (abre/fecha conexão, delega ao DAO).
     */
    public Lancamento buscarPorId(long id) {
        try (Connection conn = AbreBancoEmpresa.obterConexao()) {
            return dao.buscarPorId(id, conn);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Erro ao buscar lançamento: " + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
}
