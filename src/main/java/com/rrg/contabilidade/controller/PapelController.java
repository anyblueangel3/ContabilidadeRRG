package com.rrg.contabilidade.controller;

import com.rrg.contabilidade.model.Papel;
import com.rrg.contabilidade.model.dao.PapelDAO;

import javax.swing.*;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author Ronaldo Rodrigues Godoi e Chat GPT
 *
 * Controller para operações de Papel. Regras de negócio + mensagens para o
 * usuário.
 */
public class PapelController {

    private final PapelDAO papelDAO;

    public PapelController() {
        this.papelDAO = new PapelDAO();
    }

    public void criarPapel(Papel papel) {
        try {
            papelDAO.inserir(papel);
            JOptionPane.showMessageDialog(null, "Papel inserido com sucesso.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao inserir papel: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<Papel> listarTodosPapeis() {
        try {
            return papelDAO.listarTodos();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao listar papeis: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }

    public Papel buscarPapelPorId(int id) {
        try {
            return papelDAO.buscarPorId(id);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Erro ao buscar papel: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // dentro de com.rrg.contabilidade.controller.PapelController
    public Papel buscarPorNome(String nome) {
        try {
            return papelDAO.buscarPorNome(nome);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Erro ao buscar papel: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

}
