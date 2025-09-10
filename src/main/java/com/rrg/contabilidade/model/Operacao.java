package com.rrg.contabilidade.model;

/**
 * 
 * @author Ronaldo Rodrigues Godoi e Chat GPT
 *
 */
public class Operacao {

    private Integer id;
    private String operacao;

    public Operacao() {
    }

    public Operacao(Integer id, String operacao) {
        this.id = id;
        this.operacao = operacao;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOperacao() {
        return operacao;
    }

    public void setOperacao(String operacao) {
        this.operacao = operacao;
    }
}
