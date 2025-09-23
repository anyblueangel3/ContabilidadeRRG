package com.rrg.contabilidade.controller;

import com.rrg.contabilidade.model.PlanoDeContas;
import com.rrg.contabilidade.model.dao.PlanoDeContasDAO;

import java.util.List;

public class PlanoDeContasController {

    private final PlanoDeContasDAO planoDAO;

    public PlanoDeContasController() {
        this.planoDAO = new PlanoDeContasDAO();
    }

    // Salvar plano (novo ou atualização)
    public boolean salvarPlano(PlanoDeContas plano, boolean usarBancoEmpresa) {
        return planoDAO.salvarPlano(plano, usarBancoEmpresa);
    }

    // Excluir plano
    public boolean excluirPlano(Integer id, boolean usarBancoEmpresa) {
        return planoDAO.excluirPlano(id, usarBancoEmpresa);
    }

    // Listar todos os planos
    public List<PlanoDeContas> listarTodos(boolean usarBancoEmpresa) {
        return planoDAO.listarTodos(usarBancoEmpresa);
    }
}
