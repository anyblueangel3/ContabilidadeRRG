package com.rrg.contabilidade.model.dao;

import com.rrg.contabilidade.model.Usuario;
import com.rrg.contabilidade.model.Operacao;
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
        try (Connection conexao = AbreBancoGeral.obterConexao(); PreparedStatement ps = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

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
        try (Connection conexao = AbreBancoGeral.obterConexao(); PreparedStatement ps = conexao.prepareStatement(sql)) {

            ps.setString(1, usuario.getNome());
            ps.setString(2, usuario.getLogin());
            ps.setString(3, usuario.getSenha());
            ps.setInt(4, usuario.getPapel());
            ps.setString(5, usuario.getStatus());
            ps.setInt(6, usuario.getId());

            ps.executeUpdate();
        }
    }

    // Buscar usuário por login (já carregando operações)
    public Usuario buscarPorLogin(String login) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE login=?";
        try (Connection conexao = AbreBancoGeral.obterConexao(); PreparedStatement ps = conexao.prepareStatement(sql)) {

            ps.setString(1, login);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Usuario usuario = mapearUsuario(rs);
                    usuario.setOperacoes(buscarOperacoesDoUsuario(usuario.getId()));
                    return usuario;
                }
            }
        }
        return null;
    }

    // Listar todos os usuários (com operações)
    public List<Usuario> listarTodos() throws SQLException {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuarios ORDER BY nome";
        try (Connection conexao = AbreBancoGeral.obterConexao(); PreparedStatement ps = conexao.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Usuario usuario = mapearUsuario(rs);
                usuario.setOperacoes(buscarOperacoesDoUsuario(usuario.getId()));
                lista.add(usuario);
            }
        }
        return lista;
    }

    // 🔹 Buscar operações do usuário
    private List<Operacao> buscarOperacoesDoUsuario(int usuarioId) throws SQLException {
        List<Operacao> operacoes = new ArrayList<>();
        String sql = """
                SELECT o.id, o.descricao
                FROM operacoes_usuarios ou
                JOIN operacoes o ON o.id = ou.id_operacao
                WHERE ou.id = ?
                """;

        try (Connection conexao = AbreBancoGeral.obterConexao(); PreparedStatement ps = conexao.prepareStatement(sql)) {

            ps.setInt(1, usuarioId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Operacao op = new Operacao();
                    op.setId(rs.getInt("id"));
                    op.setDescricao(rs.getString("descricao"));
                    operacoes.add(op);
                }
            }
        }
        return operacoes;
    }

    // 🔹 Monta um objeto Usuario a partir de ResultSet
    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario(
                rs.getInt("id"),
                rs.getString("nome"),
                rs.getString("login"),
                rs.getString("senha"),
                rs.getInt("papel"),
                rs.getString("status"),
                rs.getTimestamp("data_criacao"),
                rs.getTimestamp("data_ultimo_acesso")
        );
        return usuario;
    }

    public void atribuirOperacoes(Usuario usuario) throws SQLException {
        if (usuario.getOperacoes() == null || usuario.getOperacoes().isEmpty()) {
            return;
        }

        String deleteSQL = "DELETE FROM operacoes_usuarios WHERE id_usuario = ?";
        String insertSQL = "INSERT INTO operacoes_usuarios (id_usuario, id_operacao) VALUES (?, ?)";

        try (Connection conexao = AbreBancoGeral.obterConexao()) {
            try (PreparedStatement psDelete = conexao.prepareStatement(deleteSQL)) {
                psDelete.setInt(1, usuario.getId());
                psDelete.executeUpdate();
            }

            try (PreparedStatement psInsert = conexao.prepareStatement(insertSQL)) {
                for (Operacao op : usuario.getOperacoes()) {
                    psInsert.setInt(1, usuario.getId());
                    psInsert.setInt(2, op.getId());
                    psInsert.addBatch();
                }
                psInsert.executeBatch();
            }
        }
    }

}
