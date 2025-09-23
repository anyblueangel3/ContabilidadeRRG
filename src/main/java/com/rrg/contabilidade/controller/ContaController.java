package com.rrg.contabilidade.controller;

import com.rrg.contabilidade.model.Conta;
import com.rrg.contabilidade.model.dao.ContaDAO;

import java.util.List;

public class ContaController {

    private final ContaDAO contaDAO;

    public ContaController() {
        this.contaDAO = new ContaDAO();
    }

    // Inserir conta
    public boolean inserirConta(Conta conta, boolean usarBancoEmpresa) {
        return contaDAO.inserirConta(conta, usarBancoEmpresa);
    }

    // Excluir conta
    public boolean excluirConta(String id, Integer idPlano, boolean usarBancoEmpresa) {
        return contaDAO.excluirConta(id, idPlano, usarBancoEmpresa);
    }

    // Listar contas de um plano
    public List<Conta> listarContasPorPlano(Integer idPlano, boolean usarBancoEmpresa) {
        return contaDAO.listarContasPorPlano(idPlano, usarBancoEmpresa);
    }
}
