package com.rrg.contabilidade.controller;

import com.rrg.contabilidade.model.Operacao;
import com.rrg.contabilidade.model.dao.OperacaoDoPapelDAO;

import javax.swing.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Controller para OperacaoDoPapel.
 * Trata exceções e exibe mensagens para o usuário.
 * Autor: Ronaldo Rodrigues Godoi e Chat GPT
 */
public class OperacaoDoPapelController {

    private final OperacaoDoPapelDAO dao;

    public OperacaoDoPapelController() {
        this.dao = new OperacaoDoPapelDAO();
    }

    public List<Operacao> listarOperacaoPorPapel(int idPapel) {
        try {
            return dao.listarOperacaoPorPapel(idPapel);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Erro ao listar operações do papel: " + e.getMessage());
            e.printStackTrace();
            return List.of(); // retorna lista vazia em caso de erro
        }
    }
    
    public List<Operacao> buscarOperacoesPorPapel(int idPapel) {
        try {
            return dao.listarOperacaoPorPapel(idPapel);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Erro ao listar operações do papel: " + e.getMessage());
            e.printStackTrace();
            return List.of(); // retorna lista vazia em caso de erro
        }
    }
 
}
