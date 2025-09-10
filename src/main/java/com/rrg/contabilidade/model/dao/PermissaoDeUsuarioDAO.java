package com.rrg.contabilidade.model.dao;

import com.rrg.contabilidade.model.OperacaoDeUsuario;
import com.rrg.contabilidade.util.AbreBancoGeral;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author Ronaldo Rodrigues Godoi e Chat GPT
 *
 * DAO para operações de PermissoesDeUsuarios.
 */
public class PermissaoDeUsuarioDAO {

    // Inserir nova permissão de usuário
    public void inserir(OperacaoDeUsuario permissaoUsuario) {
        String sql = "INSERT INTO permissoes_usuarios (id_usuario, id_permissao) VALUES (?, ?)";
        try (Connection conexao = AbreBancoGeral.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, permissaoUsuario.getIdUsuario());
            ps.setInt(2, permissaoUsuario.getIdOperacao());

            int linhas = ps.executeUpdate();
            if (linhas > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        permissaoUsuario.setId(rs.getInt(1));
                    }
                }
                JOptionPane.showMessageDialog(null, "Permissão de usuário inserida com sucesso.");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao inserir permissão de usuário: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Atualizar permissão de usuário
    public void atualizar(OperacaoDeUsuario permissaoUsuario) {
        String sql = "UPDATE permissoes_usuarios SET id_usuario=?, id_permissao=? WHERE id=?";
        try (Connection conexao = AbreBancoGeral.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(sql)) {

            ps.setInt(1, permissaoUsuario.getIdUsuario());
            ps.setInt(2, permissaoUsuario.getIdOperacao());
            ps.setInt(3, permissaoUsuario.getId());

            int linhas = ps.executeUpdate();
            if (linhas > 0) {
                JOptionPane.showMessageDialog(null, "Permissão de usuário atualizada com sucesso.");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao atualizar permissão de usuário: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Deletar permissão de usuário
    public void deletar(int id) {
        String sql = "DELETE FROM permissoes_usuarios WHERE id=?";
        try (Connection conexao = AbreBancoGeral.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(sql)) {

            ps.setInt(1, id);
            int linhas = ps.executeUpdate();
            if (linhas > 0) {
                JOptionPane.showMessageDialog(null, "Permissão de usuário deletada com sucesso.");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao deletar permissão de usuário: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Buscar permissão de usuário por ID
    public OperacaoDeUsuario buscarPorId(int id) {
        String sql = "SELECT * FROM permissoes_usuarios WHERE id=?";
        try (Connection conexao = AbreBancoGeral.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearPermissaoUsuario(rs);
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao buscar permissão de usuário: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // Listar todas as permissões de usuários
    public List<OperacaoDeUsuario> listarTodos() {
        List<OperacaoDeUsuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM permissoes_usuarios ORDER BY id";
        try (Connection conexao = AbreBancoGeral.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearPermissaoUsuario(rs));
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao listar permissões de usuários: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    // Mapeia ResultSet para objeto PermissoesDeUsuarios
    private OperacaoDeUsuario mapearPermissaoUsuario(ResultSet rs) throws SQLException {
        return new OperacaoDeUsuario(
                rs.getInt("id"),
                rs.getInt("id_usuario"),
                rs.getInt("id_permissao")
        );
    }
}
