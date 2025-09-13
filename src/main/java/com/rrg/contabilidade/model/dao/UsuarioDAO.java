package com.rrg.contabilidade.model.dao;

import com.rrg.contabilidade.model.Usuario;
import com.rrg.contabilidade.util.AbreBancoGeral;
import com.rrg.contabilidade.util.PasswordUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author Ronaldo Rodrigues Godoi e Chat GPT
 */
public class UsuarioDAO {

    // Inserir novo usuário
    public void inserir(Usuario usuario) {
        String sql = "INSERT INTO usuarios (nome, login, senha, papel, status) VALUES (?, ?, ?, ?, ?)";

        try (Connection conexao = AbreBancoGeral.obterConexao(); PreparedStatement ps = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, usuario.getNome());
            ps.setString(2, usuario.getLogin());
            ps.setString(3, usuario.getSenha()); // já deve vir hash se necessário
            ps.setInt(4, usuario.getPapel());
            ps.setString(5, usuario.getStatus());

            int linhas = ps.executeUpdate();

            if (linhas > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        usuario.setId(rs.getInt(1));
                    }
                }
                JOptionPane.showMessageDialog(null, "Usuário inserido com sucesso.");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao inserir usuário: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Atualizar usuário
    public void atualizar(Usuario usuario) {
        String sql = "UPDATE usuarios SET nome=?, login=?, senha=?, papel=?, status=? WHERE id=?";
        try (Connection conexao = AbreBancoGeral.obterConexao(); PreparedStatement ps = conexao.prepareStatement(sql)) {

            ps.setString(1, usuario.getNome());
            ps.setString(2, usuario.getLogin());
            ps.setString(3, usuario.getSenha()); // já deve vir hash se necessário
            ps.setInt(4, usuario.getPapel());
            ps.setString(5, usuario.getStatus());
            ps.setInt(6, usuario.getId());

            int linhas = ps.executeUpdate();
            if (linhas > 0) {
                JOptionPane.showMessageDialog(null, "Usuário atualizado com sucesso.");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao atualizar usuário: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Deletar usuário
    public void deletar(int id) {
        String sql = "DELETE FROM usuarios WHERE id=?";
        try (Connection conexao = AbreBancoGeral.obterConexao(); PreparedStatement ps = conexao.prepareStatement(sql)) {

            ps.setInt(1, id);
            int linhas = ps.executeUpdate();
            if (linhas > 0) {
                JOptionPane.showMessageDialog(null, "Usuário deletado com sucesso.");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao deletar usuário: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Buscar usuário por ID
    public Usuario buscarPorId(int id) {
        String sql = "SELECT * FROM usuarios WHERE id=?";
        try (Connection conexao = AbreBancoGeral.obterConexao(); PreparedStatement ps = conexao.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearUsuario(rs);
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao buscar usuário: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // Buscar usuário por login
    public Usuario buscarPorLogin(String login) {
        String sql = "SELECT * FROM usuarios WHERE login=?";
        try (Connection conexao = AbreBancoGeral.obterConexao(); PreparedStatement ps = conexao.prepareStatement(sql)) {

            ps.setString(1, login);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearUsuario(rs);
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao buscar usuário: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // Listar todos os usuários
    public List<Usuario> listarTodos() {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuarios ORDER BY nome";
        try (Connection conexao = AbreBancoGeral.obterConexao(); PreparedStatement ps = conexao.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearUsuario(rs));
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao listar usuários: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    // Mapeia ResultSet para objeto Usuario
    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        return new Usuario(
                rs.getInt("id"),
                rs.getString("nome"),
                rs.getString("login"),
                rs.getString("senha"),
                rs.getInt("papel"),
                rs.getString("status"),
                rs.getTimestamp("data_criacao"),
                rs.getTimestamp("data_ultimo_acesso")
        );
    }

}
