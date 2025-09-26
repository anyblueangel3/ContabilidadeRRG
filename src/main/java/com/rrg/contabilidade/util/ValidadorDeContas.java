package com.rrg.contabilidade.util;

import com.rrg.contabilidade.controller.ContaController;
import com.rrg.contabilidade.model.Conta;

import java.util.List;

/**
 * Classe utilitária para validar IDs de contas contábeis.
 * 
 * Regras:
 * - ID deve conter apenas dígitos e pontos (ex: "1", "1.1", "1.1.1").
 * - Cada conta filha só pode existir se sua conta pai já estiver cadastrada.
 * - A conta raiz (ex: "1") não tem pai, então pode ser cadastrada direto.
 */
public class ValidadorDeContas {

    private final ContaController contaController;
    private final boolean usarBancoEmpresa;

    public ValidadorDeContas(ContaController contaController, boolean usarBancoEmpresa) {
        this.contaController = contaController;
        this.usarBancoEmpresa = usarBancoEmpresa;
    }

    /**
     * Verifica se uma conta pode ser cadastrada.
     * @param idConta ID da conta (ex: "1.1.1").
     * @param idPlano ID do plano de contas.
     * @return true se pode cadastrar, false caso contrário.
     */
    public boolean podeCadastrarConta(String idConta, Integer idPlano) {
        // Verifica formato
        if (!idConta.matches("^[0-9]+(\\.[0-9]+)*$")) {
            return false;
        }

        // Conta raiz pode ser cadastrada direto
        if (!idConta.contains(".")) {
            return !estaContaCadastrada(idConta, idPlano);
        }

        // Para contas filhas, verifica se o pai existe
        String idPai = idConta.substring(0, idConta.lastIndexOf('.'));
        return estaContaCadastrada(idPai, idPlano) && !estaContaCadastrada(idConta, idPlano);
    }

    /**
     * Verifica se uma conta já está cadastrada.
     * @param idConta ID da conta.
     * @param idPlano ID do plano de contas.
     * @return true se já existe, false caso contrário.
     */
    public boolean estaContaCadastrada(String idConta, Integer idPlano) {
        List<Conta> contas = contaController.listarContasPorPlano(idPlano, usarBancoEmpresa);
        return contas.stream().anyMatch(c -> c.getId().equals(idConta));
    }
}
