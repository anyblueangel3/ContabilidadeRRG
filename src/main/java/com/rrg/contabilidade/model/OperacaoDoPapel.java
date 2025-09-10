package com.rrg.contabilidade.model;

/**
 * 
 * @author Ronaldo Rodrigues Godoi e Chat GPT
 *
 */
public class OperacaoDoPapel {

    private Integer id;
    private Integer idPapel;
    private Integer idOperacao;

    public OperacaoDoPapel() {
    }

    public OperacaoDoPapel(Integer id, Integer idPapel, Integer idOperacao) {
        this.id = id;
        this.idPapel = idPapel;
        this.idOperacao = idOperacao;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIdPapel() {
        return idPapel;
    }

    public void setIdPapel(Integer idPapel) {
        this.idPapel = idPapel;
    }

    public Integer getIdOperacao() {
        return idOperacao;
    }

    public void setIdOperacao(Integer idOperacao) {
        this.idOperacao = idOperacao;
    }
}
