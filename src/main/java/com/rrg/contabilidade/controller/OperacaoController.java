package com.rrg.contabilidade.controller;

import com.rrg.contabilidade.model.Operacao;
import com.rrg.contabilidade.model.dao.OperacaoDAO;

import javax.swing.*;
import java.sql.SQLException;
import java.util.List;

/**
 * 
 * Controller de operações
 * @author Ronaldo Rodrigues Godoi e Chat GPT
 * 
 */
public class OperacaoController {

    private final OperacaoDAO operacaoDAO;

    public OperacaoController() {
        this.operacaoDAO = new OperacaoDAO();
    }

    /**
     * Busca operações de um usuário pelo seu ID
     *
     * @param usuarioId ID do usuário
     * @return lista de operações (ou lista vazia em caso de erro)
     */
    public List<Operacao> buscarOperacoesPorUsuario(Integer usuarioId) {
        try {
            return operacaoDAO.buscarOperacoesPorUsuario(usuarioId);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao buscar operações do usuário: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }
    public List<Operacao> listarTodas() {
        try {
            return operacaoDAO.listarTodas();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao listar operações: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }
}

