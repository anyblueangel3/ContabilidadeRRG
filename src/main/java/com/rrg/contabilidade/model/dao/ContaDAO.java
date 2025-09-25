package com.rrg.contabilidade.model.dao;

import com.rrg.contabilidade.model.Conta;
import com.rrg.contabilidade.util.AbreBancoEmpresa;
import com.rrg.contabilidade.util.AbreBancoGeral;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ContaDAO {

    // Inserir conta
    public boolean inserirConta(Conta conta, boolean usarBancoEmpresa) {
        String sql = "INSERT INTO contas (id, codigo_sped, descricao, classificacao, natureza, id_plano, obrigatorio_ecd, obrigatorio_ecf) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conexao = usarBancoEmpresa ? AbreBancoEmpresa.obterConexao() : AbreBancoGeral.obterConexao(); PreparedStatement ps = conexao.prepareStatement(sql)) {

            ps.setString(1, conta.getId());
            ps.setInt(2, conta.getCodigoSPED() != null ? conta.getCodigoSPED() : 0);
            ps.setString(3, conta.getDescricao());
            ps.setString(4, conta.getClassificacao() != null ? conta.getClassificacao().name() : null);
            ps.setString(5, conta.getNatureza() != null ? conta.getNatureza().name() : null);
            ps.setInt(6, conta.getIdPlano());
            ps.setBoolean(7, conta.isObrigatorioECD());
            ps.setBoolean(8, conta.isObrigatorioECF());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Alterar conta
    public boolean alterarConta(Conta conta, boolean usarBancoEmpresa) {
        String sql = "UPDATE contas SET codigo_sped = ?, descricao = ?, classificacao = ?, natureza = ?, "
                + "obrigatorio_ecd = ?, obrigatorio_ecf = ? "
                + "WHERE id = ? AND id_plano = ?";
        try (Connection conexao = usarBancoEmpresa ? AbreBancoEmpresa.obterConexao() : AbreBancoGeral.obterConexao(); PreparedStatement ps = conexao.prepareStatement(sql)) {

            ps.setInt(1, conta.getCodigoSPED() != null ? conta.getCodigoSPED() : 0);
            ps.setString(2, conta.getDescricao());
            ps.setString(3, conta.getClassificacao() != null ? conta.getClassificacao().name() : null);
            ps.setString(4, conta.getNatureza() != null ? conta.getNatureza().name() : null);
            ps.setBoolean(5, conta.isObrigatorioECD());
            ps.setBoolean(6, conta.isObrigatorioECF());
            ps.setString(7, conta.getId());
            ps.setInt(8, conta.getIdPlano());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Excluir conta
    public boolean excluirConta(String id, Integer idPlano, boolean usarBancoEmpresa) {
        String sql = "DELETE FROM contas WHERE id = ? AND id_plano = ?";
        try (Connection conexao = usarBancoEmpresa ? AbreBancoEmpresa.obterConexao() : AbreBancoGeral.obterConexao(); PreparedStatement ps = conexao.prepareStatement(sql)) {

            ps.setString(1, id);
            ps.setInt(2, idPlano);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Listar contas por plano
    public List<Conta> listarContasPorPlano(Integer idPlano, boolean usarBancoEmpresa) {
        List<Conta> lista = new ArrayList<>();
        String sql = "SELECT * FROM contas WHERE id_plano = ? ORDER BY id";
        try (Connection conexao = usarBancoEmpresa ? AbreBancoEmpresa.obterConexao() : AbreBancoGeral.obterConexao(); PreparedStatement ps = conexao.prepareStatement(sql)) {

            ps.setInt(1, idPlano);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Conta conta = new Conta();
                    conta.setId(rs.getString("id"));
                    conta.setCodigoSPED(rs.getInt("codigo_sped"));
                    conta.setDescricao(rs.getString("descricao"));
                    conta.setClassificacao(rs.getString("classificacao") != null
                            ? Enum.valueOf(com.rrg.contabilidade.model.Classificacao.class, rs.getString("classificacao")) : null);
                    conta.setNatureza(rs.getString("natureza") != null
                            ? Enum.valueOf(com.rrg.contabilidade.model.Natureza.class, rs.getString("natureza")) : null);
                    conta.setIdPlano(rs.getInt("id_plano"));
                    conta.setObrigatorioECD(rs.getBoolean("obrigatorio_ecd"));
                    conta.setObrigatorioECF(rs.getBoolean("obrigatorio_ecf"));

                    lista.add(conta);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}
