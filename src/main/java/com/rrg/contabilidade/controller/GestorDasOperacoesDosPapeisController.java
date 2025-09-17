package com.rrg.contabilidade.controller;

import com.rrg.contabilidade.model.Operacao;
import com.rrg.contabilidade.model.dao.GestorDasOperacoesDosPapeisDAO;

import javax.swing.*;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author Ronaldo Rodrigues Godoi e Chat GPT
 * 
 * gestor de operações de papéis
 * 
 */
public class GestorDasOperacoesDosPapeisController {

    private final GestorDasOperacoesDosPapeisDAO dao;

    public GestorDasOperacoesDosPapeisController() {
        this.dao = new GestorDasOperacoesDosPapeisDAO();
    }

    public List<Operacao> listarTodasOperacoes() {
        try {
            return dao.listarTodasOperacoes();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao listar operações: " + e.getMessage());
            return List.of();
        }
    }

    public List<Integer> listarOperacoesDoPapel(int papelId) {
        try {
            return dao.listarOperacoesDoPapel(papelId);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao listar operações do papel: " + e.getMessage());
            return List.of();
        }
    }

    public void salvarOperacoesDoPapel(int papelId, List<Integer> operacoesSelecionadas) {
        try {
            // Remove todas as operações atuais
            dao.removerOperacoesDoPapel(papelId);

            // Insere as novas operações selecionadas
            for (Integer opId : operacoesSelecionadas) {
                dao.adicionarOperacaoAoPapel(papelId, opId);
            }

            JOptionPane.showMessageDialog(null, "Operações do papel atualizadas com sucesso.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao salvar operações do papel: " + e.getMessage());
        }
    }
}
