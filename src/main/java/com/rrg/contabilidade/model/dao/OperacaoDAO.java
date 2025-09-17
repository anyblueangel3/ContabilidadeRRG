package com.rrg.contabilidade.model.dao;

import com.rrg.contabilidade.model.Operacao;
import com.rrg.contabilidade.util.AbreBancoGeral;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * DAO de operações
 * @author Ronaldo Rodrigues Godoi e Chat GPT
 * 
 */
public class OperacaoDAO {

    /**
     * Busca todas as operações de um usuário (via tabela operacoes_usuarios).
     *
     * @param usuarioId ID do usuário
     * @return lista de operações
     * @throws SQLException em caso de erro de banco
     */
    public List<Operacao> buscarOperacoesPorUsuario(Integer usuarioId) throws SQLException {
        List<Operacao> operacoes = new ArrayList<>();

        String sql = """
            SELECT o.id, o.operacao, o.descricao
            FROM operacoes o
            INNER JOIN operacoes_usuarios ou ON o.id = ou.id_operacao
            WHERE ou.id_usuario = ?
            ORDER BY o.operacao
        """;

        try (Connection conexao = AbreBancoGeral.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(sql)) {

            ps.setInt(1, usuarioId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Operacao op = new Operacao();
                    op.setId(rs.getInt("id"));
                    op.setOperacao(rs.getString("operacao"));
                    op.setDescricao(rs.getString("descricao"));
                    operacoes.add(op);
                }
            }
        }

        return operacoes;
    }
    
    public List<Operacao> listarTodas() throws SQLException {
        List<Operacao> lista = new ArrayList<>();
        String sql = "SELECT * FROM operacoes ORDER BY operacao";

        try (Connection conexao = AbreBancoGeral.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Operacao op = new Operacao(rs.getInt("id"), rs.getString("operacao"));
                op.setDescricao(rs.getString("descricao"));
                lista.add(op);
            }
        }
        return lista;
    }
    
}

