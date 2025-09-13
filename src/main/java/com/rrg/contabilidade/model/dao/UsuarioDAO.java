package com.rrg.contabilidade.model.dao;

import com.rrg.contabilidade.model.Usuario;
import com.rrg.contabilidade.util.AbreBancoGeral;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/*
 * @author Ronaldo Rodrigues Godoi e Chat GPT
 *
 * DAO de usuários (sem JOptionPane)...
 */
public class UsuarioDAO {

    // Inserir novo usuário
    public void inserir(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO usuarios (nome, login, senha, papel, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conexao = AbreBancoGeral.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, usuario.getNome());
            ps.setString(2, usuario.getLogin());
            ps.setString(3, usuario.getSenha());
            ps.setInt(4, usuario.getPapel());
            ps.setString(5, usuario.getStatus());

            int linhas = ps.executeUpdate();
            if (linhas > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        usuario.setId(rs.getInt(1));
                    }
                }
            }
        }
    }

    // Atualizar usuário existente
    public void atualizar(Usuario usuario) throws SQLException {
        String sql = "UPDATE usuarios SET nome=?, login=?, senha=?, papel=?, status=? WHERE id=?";
        try (Connection conexao = AbreBancoGeral.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(sql)) {

            ps.setString(1, usuario.getNome());
            ps.setString(2, usuario.getLogin());
            ps.setString(3, usuario.getSenha());
            ps.setInt(4, usuario.getPapel());
            ps.setString(5, usuario.getStatus());
            ps.setInt(6, usuario.getId());

            ps.executeUpdate();
        }
    }

    // Buscar usuário por login
    public Usuario buscarPorLogin(String login) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE login=?";
        try (Connection conexao = AbreBancoGeral.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(sql)) {

            ps.setString(1, login);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearUsuario(rs);
                }
            }
        }
        return null;
    }

    // Listar todos os usuários
    public List<Usuario> listarTodos() throws SQLException {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuarios ORDER BY nome";
        try (Connection conexao = AbreBancoGeral.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearUsuario(rs));
            }
        }
        return lista;
    }

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
