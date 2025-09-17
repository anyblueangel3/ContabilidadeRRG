package com.rrg.contabilidade.model.dao;

import com.rrg.contabilidade.model.Operacao;
import com.rrg.contabilidade.util.AbreBancoGeral;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/*
 *
 * @author Ronaldo Rodrigues Godoi e Chat GPT
 *
 * DAO do Gestor...
*/

public class GestorDasOperacoesDosUsuariosDAO {

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

    public List<Integer> listarOperacoesDoUsuario(int usuarioId) throws SQLException {
        List<Integer> ids = new ArrayList<>();
        String sql = "SELECT id_operacao FROM operacoes_usuarios WHERE id_usuario = ?";

        try (Connection conn = AbreBancoGeral.obterConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, usuarioId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getInt("id_operacao"));
                }
            }
        }
        return ids;
    }

    public void removerOperacoesDoUsuario(int usuarioId) throws SQLException {
        String sql = "DELETE FROM operacoes_usuarios WHERE id_usuario = ?";
        try (Connection conn = AbreBancoGeral.obterConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            ps.executeUpdate();
        }
    }

    public void adicionarOperacaoAoUsuario(int usuarioId, int operacaoId) throws SQLException {
        String sql = "INSERT INTO operacoes_usuarios (id_usuario, id_operacao) VALUES (?, ?)";
        try (Connection conn = AbreBancoGeral.obterConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            ps.setInt(2, operacaoId);
            ps.executeUpdate();
        }
    }
}
