package com.rrg.contabilidade.model;

/**
 *
 * @author Ronaldo Rodrigues Godoi e Chat GPT
 * 
 * Entidade Plano de contas tanto de geral quanto de empresa
 * 
 */
public class PlanoDeContas {
    
    private Integer id;
    private String nome;
    private String descricao;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    
}
