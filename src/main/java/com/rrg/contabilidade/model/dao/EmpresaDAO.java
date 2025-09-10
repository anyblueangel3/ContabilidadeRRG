package com.rrg.contabilidade.model.dao;

import com.rrg.contabilidade.model.Empresa;
import com.rrg.contabilidade.util.AbreBancoGeral;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 * DAO para gerenciar empresas no banco geral.
 */
public class EmpresaDAO {

    // Inserir nova empresa
    public void inserir(Empresa empresa) {
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
            JOptionPane.showMessageDialog(null, "Empresa inserida com sucesso.");

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao inserir empresa: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Atualizar empresa
    public void atualizar(Empresa empresa) {
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
            JOptionPane.showMessageDialog(null, "Empresa atualizada com sucesso.");

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao atualizar empresa: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Buscar empresa por CNPJ
    public Empresa buscarPorCnpj(String cnpj) {
        String sql = "SELECT * FROM empresas WHERE cnpj=?";
        try (Connection conexao = AbreBancoGeral.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(sql)) {

            ps.setString(1, cnpj);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Empresa(
                            rs.getString("cnpj"),
                            rs.getString("razao"),
                            rs.getString("endereco"),
                            rs.getString("responsavel"),
                            rs.getString("telefone_empresa"),
                            rs.getString("telefone_responsavel")
                    );
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao buscar empresa: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // Listar todas as empresas
    public List<Empresa> listarTodos() {
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

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao listar empresas: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }
}
