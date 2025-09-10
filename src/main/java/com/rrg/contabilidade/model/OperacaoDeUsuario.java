package com.rrg.contabilidade.model;

/**
 * 
 * @author Ronaldo Rodrigues Godoi e Chat GPT
 *
 */
public class OperacaoDeUsuario {

    private Integer id;
    private Integer idUsuario;
    private Integer idOperacao;

    public OperacaoDeUsuario() {
    }

    public OperacaoDeUsuario(Integer id, Integer idUsuario, Integer idOperacao) {
        this.id = id;
        this.idUsuario = idUsuario;
        this.idOperacao = idOperacao;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Integer getIdOperacao() {
        return idOperacao;
    }

    public void setIdOperacao(Integer idOperacao) {
        this.idOperacao = idOperacao;
    }
}
