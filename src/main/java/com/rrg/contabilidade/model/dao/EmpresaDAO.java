package com.rrg.contabilidade.model.dao;

import com.rrg.contabilidade.model.Empresa;
import com.rrg.contabilidade.util.AbreBancoGeral;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO para gerenciar empresas no banco geral.
 */
public class EmpresaDAO {

    // Inserir nova empresa
    public void inserir(Empresa empresa) throws SQLException {
        String sql = "INSERT INTO empresas (cnpj, razao, endereco, responsavel, telefone_empresa, telefone_responsavel) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conexao = AbreBancoGeral.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(sql)) {

            ps.setString(1, empresa.getCnpj());
            ps.setString(2, empresa.getRazao());
            ps.setString(3, empresa.getEndereco());
            ps.setString(4, empresa.getResponsavel());
            ps.setString(5, empresa.getTelefoneEmpresa());
            ps.setString(6, empresa.getTelefoneResponsavel());

            ps.executeUpdate();
        }
    }

    // Atualizar empresa
    public void atualizar(Empresa empresa) throws SQLException {
        String sql = "UPDATE empresas SET razao=?, endereco=?, responsavel=?, telefone_empresa=?, telefone_responsavel=? "
                   + "WHERE cnpj=?";
        try (Connection conexao = AbreBancoGeral.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(sql)) {

            ps.setString(1, empresa.getRazao());
            ps.setString(2, empresa.getEndereco());
            ps.setString(3, empresa.getResponsavel());
            ps.setString(4, empresa.getTelefoneEmpresa());
            ps.setString(5, empresa.getTelefoneResponsavel());
            ps.setString(6, empresa.getCnpj());

            ps.executeUpdate();
        }
    }

    // Buscar empresa por CNPJ
    public Optional<Empresa> buscarPorCnpj(String cnpj) throws SQLException {
        String sql = "SELECT * FROM empresas WHERE cnpj=?";
        try (Connection conexao = AbreBancoGeral.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(sql)) {

            ps.setString(1, cnpj);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Empresa empresa = new Empresa(
                            rs.getString("cnpj"),
                            rs.getString("razao"),
                            rs.getString("endereco"),
                            rs.getString("responsavel"),
                            rs.getString("telefone_empresa"),
                            rs.getString("telefone_responsavel")
                    );
                    return Optional.of(empresa);
                }
            }
        }
        return Optional.empty();
    }

    // Listar todas as empresas
    public List<Empresa> listarTodos() throws SQLException {
        List<Empresa> lista = new ArrayList<>();
        String sql = "SELECT * FROM empresas ORDER BY razao";
        try (Connection conexao = AbreBancoGeral.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new Empresa(
                        rs.getString("cnpj"),
                        rs.getString("razao"),
                        rs.getString("endereco"),
                        rs.getString("responsavel"),
                        rs.getString("telefone_empresa"),
                        rs.getString("telefone_responsavel")
                ));
            }
        }
        return lista;
    }
}
