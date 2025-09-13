package com.rrg.contabilidade.model.dao;

import com.rrg.contabilidade.model.Papel;
import com.rrg.contabilidade.util.AbreBancoGeral;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Ronaldo Rodrigues Godoi e Chat GPT
 *
 * DAO para operações de Papeis.
 *
 * DAO para persistência de objetos Papel. Responsável apenas por acessar o
 * banco.
 */
public class PapelDAO {

    // Inserir novo papel
    public void inserir(Papel papel) throws SQLException {
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
            }
        }
    }

    // Listar todos os papeis
    public List<Papel> listarTodos() throws SQLException {
        List<Papel> lista = new ArrayList<>();
        String sql = "SELECT * FROM papeis ORDER BY nome_papel";

        try (Connection conexao = AbreBancoGeral.obterConexao(); PreparedStatement ps = conexao.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Papel p = new Papel(rs.getInt("id"), rs.getString("nome_papel"));
                lista.add(p);
            }
        }
        return lista;
    }

    // Buscar papel por id
    public Papel buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM papeis WHERE id=?";
        try (Connection conexao = AbreBancoGeral.obterConexao(); PreparedStatement ps = conexao.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Papel(rs.getInt("id"), rs.getString("nome_papel"));
                }
            }
        }
        return null;
    }

    // dentro de com.rrg.contabilidade.model.dao.PapelDAO
    public Papel buscarPorNome(String nome) throws SQLException {
        String sql = "SELECT * FROM papeis WHERE nome_papel = ?";
        try (Connection conexao = AbreBancoGeral.obterConexao(); PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setString(1, nome);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Papel(rs.getInt("id"), rs.getString("nome_papel"));
                }
            }
        }
        return null;
    }

}
