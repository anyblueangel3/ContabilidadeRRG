package com.rrg.contabilidade.model.dao;

import com.rrg.contabilidade.model.PlanoDeContas;
import com.rrg.contabilidade.util.AbreBancoEmpresa;
import com.rrg.contabilidade.util.AbreBancoGeral;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlanoDeContasDAO {

    // Salvar plano (inserir ou atualizar)
    public boolean salvarPlano(PlanoDeContas plano, boolean usarBancoEmpresa) {
        if (plano.getId() == null) {
            // Inserir novo
            String sql = "INSERT INTO planos_de_contas (nome, descricao) VALUES (?, ?)";
            try (Connection conexao = usarBancoEmpresa ? AbreBancoEmpresa.obterConexao() : AbreBancoGeral.obterConexao();
                 PreparedStatement ps = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

                ps.setString(1, plano.getNome());
                ps.setString(2, plano.getDescricao());
                int affectedRows = ps.executeUpdate();

                if (affectedRows > 0) {
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) {
                            plano.setId(rs.getInt(1));
                        }
                    }
                    return true;
                }

            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            // Atualizar existente
            String sql = "UPDATE planos_de_contas SET nome = ?, descricao = ? WHERE id = ?";
            try (Connection conexao = usarBancoEmpresa ? AbreBancoEmpresa.obterConexao() : AbreBancoGeral.obterConexao();
                 PreparedStatement ps = conexao.prepareStatement(sql)) {

                ps.setString(1, plano.getNome());
                ps.setString(2, plano.getDescricao());
                ps.setInt(3, plano.getId());

                return ps.executeUpdate() > 0;

            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    // Excluir plano
    public boolean excluirPlano(Integer id, boolean usarBancoEmpresa) {
        String sql = "DELETE FROM planos_de_contas WHERE id = ?";
        try (Connection conexao = usarBancoEmpresa ? AbreBancoEmpresa.obterConexao() : AbreBancoGeral.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Listar todos os planos
    public List<PlanoDeContas> listarTodos(boolean usarBancoEmpresa) {
        List<PlanoDeContas> lista = new ArrayList<>();
        String sql = "SELECT * FROM planos_de_contas ORDER BY nome";
        try (Connection conexao = usarBancoEmpresa ? AbreBancoEmpresa.obterConexao() : AbreBancoGeral.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                PlanoDeContas plano = new PlanoDeContas();
                plano.setId(rs.getInt("id"));
                plano.setNome(rs.getString("nome"));
                plano.setDescricao(rs.getString("descricao"));
                lista.add(plano);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}
