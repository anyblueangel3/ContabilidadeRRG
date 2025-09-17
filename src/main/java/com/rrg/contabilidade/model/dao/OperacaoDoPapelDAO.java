package com.rrg.contabilidade.model.dao;

import com.rrg.contabilidade.model.OperacaoDoPapel;
import com.rrg.contabilidade.util.AbreBancoGeral;
import com.rrg.contabilidade.model.Operacao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para OperacaoDoPapel.
 * Apenas executa consultas, lançando SQLException.
 * @author Ronaldo Rodrigues Godoi e Chat GPT
 */
public class OperacaoDoPapelDAO {

    /**
     * Lista todas as operações associadas a um papel.
     */
    public List<Operacao> listarOperacaoPorPapel(int idPapel) throws SQLException {
        List<Operacao> operacoes = new ArrayList<>();
        String sql = """
            SELECT o.id, o.operacao, o.descricao
            FROM operacoes_papeis op
            JOIN operacoes o ON o.id = op.id_operacao
            WHERE op.id_papel = ?
            ORDER BY o.id
        """;

        try (Connection conexao = AbreBancoGeral.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(sql)) {

            ps.setInt(1, idPapel);
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

}
