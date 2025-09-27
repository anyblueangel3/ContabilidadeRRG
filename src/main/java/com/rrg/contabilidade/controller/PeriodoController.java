package com.rrg.contabilidade.controller;

import com.rrg.contabilidade.model.Periodo;
import com.rrg.contabilidade.model.dao.PeriodoDAO;

import javax.swing.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * Controller para Periodos — recebe a Connection do banco da empresa no construtor.
 * Trata SQLException do DAO e apresenta mensagens ao usuário via JOptionPane.
 */
public class PeriodoController {

    private final PeriodoDAO periodoDAO;

    public PeriodoController(Connection connEmpresa) {
        this.periodoDAO = new PeriodoDAO(connEmpresa);
    }

    public boolean cadastrarPeriodo(Periodo periodo) {
        try {
            periodoDAO.inserir(periodo);
            JOptionPane.showMessageDialog(null, "Período cadastrado com sucesso.");
            return true;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao cadastrar período: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Abre período e importa plano/contas do banco geral (multi-step).
     * idPlanoGeral: id do plano no banco geral que será copiado para o banco empresa.
     */
    public boolean abrirPeriodoComPlanoImportado(Periodo periodo, int idPlanoGeral) {
        try {
            periodoDAO.abrirPeriodoComPlanoImportado(periodo, idPlanoGeral);
            JOptionPane.showMessageDialog(null, "Período aberto e plano/contas importados com sucesso.");
            return true;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao abrir período/importar plano: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        }
    }

    public List<Periodo> listarPeriodos() {
        try {
            return periodoDAO.listarTodos();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao listar períodos: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<Periodo> listarAbertos() {
        try {
            return periodoDAO.listarAbertos();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao listar períodos abertos: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public Periodo buscarPorId(int id) {
        try {
            return periodoDAO.buscarPorId(id);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao buscar período: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return null;
        }
    }

    public boolean fecharPeriodo(int idPeriodo) {
        try {
            boolean ok = periodoDAO.fecharPeriodo(idPeriodo);
            if (ok) {
                JOptionPane.showMessageDialog(null, "Período fechado com sucesso.");
            } else {
                JOptionPane.showMessageDialog(null, "Período não encontrado.", "Atenção", JOptionPane.WARNING_MESSAGE);
            }
            return ok;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao fechar período: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        }
    }

    public boolean excluirPeriodo(int id) {
        try {
            periodoDAO.excluir(id);
            JOptionPane.showMessageDialog(null, "Período excluído com sucesso.");
            return true;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao excluir período: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        }
    }
}
