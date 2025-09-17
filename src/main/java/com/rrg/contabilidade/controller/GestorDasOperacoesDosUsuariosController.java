package com.rrg.contabilidade.controller;

import com.rrg.contabilidade.model.Operacao;
import com.rrg.contabilidade.model.dao.GestorDasOperacoesDosUsuariosDAO;

import javax.swing.*;
import java.sql.SQLException;
import java.util.List;

/**
 * 
 * Controller das operações de usuários
 * 
 * @author Ronaldo Rodrigues Godoi e Chat GPT
 * 
 */
public class GestorDasOperacoesDosUsuariosController {

    private final GestorDasOperacoesDosUsuariosDAO dao;

    public GestorDasOperacoesDosUsuariosController() {
        this.dao = new GestorDasOperacoesDosUsuariosDAO();
    }

    public List<Operacao> listarTodasOperacoes() {
        try {
            return dao.listarTodasOperacoes();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao listar operações: " + e.getMessage());
            return List.of();
        }
    }

    public List<Integer> listarOperacoesDoUsuario(int usuarioId) {
        try {
            return dao.listarOperacoesDoUsuario(usuarioId);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao listar operações do usuário: " + e.getMessage());
            return List.of();
        }
    }

    public void salvarOperacoesDoUsuario(int usuarioId, List<Integer> operacoesSelecionadas) {
        try {
            dao.removerOperacoesDoUsuario(usuarioId);
            for (Integer opId : operacoesSelecionadas) {
                dao.adicionarOperacaoAoUsuario(usuarioId, opId);
            }
            JOptionPane.showMessageDialog(null, "Operações do usuário atualizadas com sucesso.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao salvar operações do usuário: " + e.getMessage());
        }
    }
}
