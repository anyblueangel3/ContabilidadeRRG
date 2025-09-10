package com.rrg.contabilidade.model.dao;

import com.rrg.contabilidade.model.OperacaoDoPapel;
import com.rrg.contabilidade.util.AbreBancoGeral;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author Ronaldo Rodrigues Godoi e Chat GPT
 *
 * DAO para operações de PermissoesDosPapeis.
 */
public class PermissaoDoPapelDAO {

    // Inserir nova permissão de papel
    public void inserir(OperacaoDoPapel permissaoPapel) {
        String sql = "INSERT INTO permissoes_papel (id_papel, id_permissao) VALUES (?, ?)";
        try (Connection conexao = AbreBancoGeral.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, permissaoPapel.getIdPapel());
            ps.setInt(2, permissaoPapel.getIdOperacao());

            int linhas = ps.executeUpdate();
            if (linhas > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        permissaoPapel.setId(rs.getInt(1));
                    }
                }
                JOptionPane.showMessageDialog(null, "Permissão de papel inserida com sucesso.");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao inserir permissão de papel: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Atualizar permissão de papel
    public void atualizar(OperacaoDoPapel permissaoPapel) {
        String sql = "UPDATE permissoes_papel SET id_papel=?, id_permissao=? WHERE id=?";
        try (Connection conexao = AbreBancoGeral.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(sql)) {

            ps.setInt(1, permissaoPapel.getIdPapel());
            ps.setInt(2, permissaoPapel.getIdOperacao());
            ps.setInt(3, permissaoPapel.getId());

            int linhas = ps.executeUpdate();
            if (linhas > 0) {
                JOptionPane.showMessageDialog(null, "Permissão de papel atualizada com sucesso.");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao atualizar permissão de papel: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Deletar permissão de papel
    public void deletar(int id) {
        String sql = "DELETE FROM permissoes_papel WHERE id=?";
        try (Connection conexao = AbreBancoGeral.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(sql)) {

            ps.setInt(1, id);
            int linhas = ps.executeUpdate();
            if (linhas > 0) {
                JOptionPane.showMessageDialog(null, "Permissão de papel deletada com sucesso.");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao deletar permissão de papel: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Buscar permissão de papel por ID
    public OperacaoDoPapel buscarPorId(int id) {
        String sql = "SELECT * FROM permissoes_papel WHERE id=?";
        try (Connection conexao = AbreBancoGeral.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearPermissaoPapel(rs);
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao buscar permissão de papel: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // Listar todas as permissões de papel
    public List<OperacaoDoPapel> listarTodos() {
        List<OperacaoDoPapel> lista = new ArrayList<>();
        String sql = "SELECT * FROM permissoes_papel ORDER BY id";
        try (Connection conexao = AbreBancoGeral.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearPermissaoPapel(rs));
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao listar permissões de papéis: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    // Mapeia ResultSet para objeto PermissoesDosPapeis
    private OperacaoDoPapel mapearPermissaoPapel(ResultSet rs) throws SQLException {
        return new OperacaoDoPapel(
                rs.getInt("id"),
                rs.getInt("id_papel"),
                rs.getInt("id_permissao")
        );
    }
}
