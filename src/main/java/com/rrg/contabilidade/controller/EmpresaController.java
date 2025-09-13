package com.rrg.contabilidade.controller;

import com.rrg.contabilidade.model.Empresa;
import com.rrg.contabilidade.model.dao.EmpresaDAO;

import javax.swing.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class EmpresaController {

    private EmpresaDAO empresaDAO;

    public EmpresaController() {
        this.empresaDAO = new EmpresaDAO();
    }

    // Inserir empresa com tratamento de mensagens
    public void inserirEmpresa(Empresa empresa) {
        try {
            empresaDAO.inserir(empresa);
            JOptionPane.showMessageDialog(null, "Empresa inserida com sucesso.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao inserir empresa: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Atualizar empresa com tratamento de mensagens
    public void atualizarEmpresa(Empresa empresa) {
        try {
            empresaDAO.atualizar(empresa);
            JOptionPane.showMessageDialog(null, "Empresa atualizada com sucesso.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao atualizar empresa: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Buscar empresa por CNPJ
    public Optional<Empresa> buscarEmpresaPorCnpj(String cnpj) {
        try {
            return empresaDAO.buscarPorCnpj(cnpj);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao buscar empresa: " + e.getMessage());
            e.printStackTrace();
            return Optional.empty();
        }
    }

    // Listar todas as empresas
    public List<Empresa> listarTodasEmpresas() {
        try {
            return empresaDAO.listarTodos();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao listar empresas: " + e.getMessage());
            e.printStackTrace();
            return List.of(); // retorna lista vazia em caso de erro
        }
    }
}
