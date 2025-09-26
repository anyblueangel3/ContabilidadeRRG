package com.rrg.contabilidade.util;

/**
 * Enum representando todas as operações do sistema.
 * Cada operação possui um código (usado no sistema) e uma descrição (usada para exibição ou povoamento inicial da tabela operacoes).
 */
public enum OperacoesDoSistema {

    CADASTRO_USUARIOS("CADASTRO_USUARIOS", "Cadastro de Usuários e suas Operações"),
    CADASTRO_EMPRESAS("CADASTRO_EMPRESAS", "Cadastro de Empresas"),
    CADASTRO_PAPEIS("CADASTRO_PAPEIS", "Cadastro de Papéis e suas Operações"),
    CADASTRO_PLANOS_E_CONTAS("CADASTRO_PLANOS_E_CONTAS", "Cadastro de Planos de Contas e Contas"),
    LANCAMENTOS("LANCAMENTOS", "Lançamentos"),
    ARE("ARE", "ARE"),
    BALANCETE("BALANCETE", "Balancete"),
    BALANCO("BALANCO", "Balanço"),
    DRE("DRE", "DRE"),
    PLANO_DE_CONTAS("PLANO_DE_CONTAS", "Plano de Contas"),
    BACKUP("BACKUP", "Backup"),
    INDICES("INDICES", "Índices");

    private final String operacao;
    private final String descricao;

    OperacoesDoSistema(String operacao, String descricao) {
        this.operacao = operacao;
        this.descricao = descricao;
    }

    public String getOperacao() {
        return operacao;
    }

    public String getDescricao() {
        return descricao;
    }
}
