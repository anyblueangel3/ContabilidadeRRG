package com.rrg.contabilidade.model.dao;

import com.rrg.contabilidade.model.Operacao;
import com.rrg.contabilidade.util.AbreBancoGeral;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Ronaldo Rodrigues Godoi e Chat GPT
 */
public class GestorDasOperacoesDosPapeisDAO {

    // Listar todas as operações
    public List<Operacao> listarTodasOperacoes() throws SQLException {
        List<Operacao> operacoes = new ArrayList<>();
        String sql = "SELECT id, operacao, descricao FROM operacoes ORDER BY descricao ASC";

        try (Connection conn = AbreBancoGeral.obterConexao();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Operacao op = new Operacao();
                op.setId(rs.getInt("id"));
                op.setOperacao(rs.getString("operacao"));
                op.setDescricao(rs.getString("descricao"));
                operacoes.add(op);
            }
        }
        return operacoes;
    }

    // Listar IDs das operações atribuídas a um papel
    public List<Integer> listarOperacoesDoPapel(int papelId) throws SQLException {
        List<Integer> ids = new ArrayList<>();
        String sql = "SELECT id_operacao FROM operacoes_papeis WHERE id_papel = ?";

        try (Connection conn = AbreBancoGeral.obterConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, papelId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getInt("id_operacao"));
                }
            }
        }
        return ids;
    }

    // Remover operações de um papel
    public void removerOperacoesDoPapel(int papelId) throws SQLException {
        String sql = "DELETE FROM operacoes_papeis WHERE id_papel = ?";
        try (Connection conn = AbreBancoGeral.obterConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, papelId);
            ps.executeUpdate();
        }
    }

    // Adicionar operação a um papel
    public void adicionarOperacaoAoPapel(int papelId, int operacaoId) throws SQLException {
        String sql = "INSERT INTO operacoes_papeis (id_papel, id_operacao) VALUES (?, ?)";
        try (Connection conn = AbreBancoGeral.obterConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, papelId);
            ps.setInt(2, operacaoId);
            ps.executeUpdate();
        }
    }
}
