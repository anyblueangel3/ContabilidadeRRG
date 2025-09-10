package com.rrg.contabilidade.model.dao;

import com.rrg.contabilidade.model.Operacao;
import com.rrg.contabilidade.util.AbreBancoGeral;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author Ronaldo Rodrigues Godoi e Chat GPT
 *
 * DAO para operações de Permissoes.
 */
public class PermissaoDAO {

    public void inserir(Operacao permissao) {
        String sql = "INSERT INTO permissoes (operacao) VALUES (?)";
        try (Connection conexao = AbreBancoGeral.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, permissao.getOperacao());

            int linhas = ps.executeUpdate();
            if (linhas > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        permissao.setId(rs.getInt(1));
                    }
                }
                JOptionPane.showMessageDialog(null, "Permissão inserida com sucesso.");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao inserir permissão: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void atualizar(Operacao permissao) {
        String sql = "UPDATE permissoes SET operacao=? WHERE id=?";
        try (Connection conexao = AbreBancoGeral.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(sql)) {

            ps.setString(1, permissao.getOperacao());
            ps.setInt(2, permissao.getId());

            int linhas = ps.executeUpdate();
            if (linhas > 0) {
                JOptionPane.showMessageDialog(null, "Permissão atualizada com sucesso.");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao atualizar permissão: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void deletar(int id) {
        String sql = "DELETE FROM permissoes WHERE id=?";
        try (Connection conexao = AbreBancoGeral.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(sql)) {

            ps.setInt(1, id);
            int linhas = ps.executeUpdate();
            if (linhas > 0) {
                JOptionPane.showMessageDialog(null, "Permissão deletada com sucesso.");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao deletar permissão: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Operacao buscarPorId(int id) {
        String sql = "SELECT * FROM permissoes WHERE id=?";
        try (Connection conexao = AbreBancoGeral.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Operacao(rs.getInt("id"), rs.getString("operacao"));
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao buscar permissão: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public List<Operacao> listarTodos() {
        List<Operacao> lista = new ArrayList<>();
        String sql = "SELECT * FROM permissoes ORDER BY operacao";
        try (Connection conexao = AbreBancoGeral.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new Operacao(rs.getInt("id"), rs.getString("operacao")));
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao listar permissões: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }
}
