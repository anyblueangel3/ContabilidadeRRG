package com.rrg.contabilidade.model.dao;

import com.rrg.contabilidade.model.Papel;
import com.rrg.contabilidade.util.AbreBancoGeral;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author Ronaldo Rodrigues Godoi e Chat GPT
 *
 * DAO para operações de Papeis.
 */
public class PapelDAO {

    public void inserir(Papel papel) {
        String sql = "INSERT INTO papeis (nome_papel) VALUES (?)";
        try (Connection conexao = AbreBancoGeral.obterConexao(); PreparedStatement ps = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, papel.getNomePapel());

            int linhas = ps.executeUpdate();
            if (linhas > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        papel.setId(rs.getInt(1));
                    }
                }
                JOptionPane.showMessageDialog(null, "Papel inserido com sucesso.");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao inserir papel: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void atualizar(Papel papel) {
        String sql = "UPDATE papeis SET nome_papel=? WHERE id=?";
        try (Connection conexao = AbreBancoGeral.obterConexao(); PreparedStatement ps = conexao.prepareStatement(sql)) {

            ps.setString(1, papel.getNomePapel());
            ps.setInt(2, papel.getId());

            int linhas = ps.executeUpdate();
            if (linhas > 0) {
                JOptionPane.showMessageDialog(null, "Papel atualizado com sucesso.");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao atualizar papel: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void deletar(int id) {
        String sql = "DELETE FROM papeis WHERE id=?";
        try (Connection conexao = AbreBancoGeral.obterConexao(); PreparedStatement ps = conexao.prepareStatement(sql)) {

            ps.setInt(1, id);
            int linhas = ps.executeUpdate();
            if (linhas > 0) {
                JOptionPane.showMessageDialog(null, "Papel deletado com sucesso.");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao deletar papel: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Papel buscarPorId(int id) {
        String sql = "SELECT * FROM papeis WHERE id=?";
        try (Connection conexao = AbreBancoGeral.obterConexao(); PreparedStatement ps = conexao.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Papel(rs.getInt("id"), rs.getString("nome_papel"));
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao buscar papel: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public List<Papel> listarTodos() {
        List<Papel> lista = new ArrayList<>();
        String sql = "SELECT * FROM papeis ORDER BY nome_papel";
        try (Connection conexao = AbreBancoGeral.obterConexao(); PreparedStatement ps = conexao.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new Papel(rs.getInt("id"), rs.getString("nome_papel")));
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao listar papeis: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    public Papel buscarPorNome(String nomePapel) {
        String sql = "SELECT * FROM papeis WHERE UPPER(nome_papel) = UPPER(?)";
        try (Connection conexao = AbreBancoGeral.obterConexao(); PreparedStatement ps = conexao.prepareStatement(sql)) {

            ps.setString(1, nomePapel);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Papel(rs.getInt("id"), rs.getString("nome_papel"));
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao buscar papel por nome: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

}
