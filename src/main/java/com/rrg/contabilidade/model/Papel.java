package com.rrg.contabilidade.model;

/**
 *
 * @author Ronaldo Rodrigues Godoi e Chat GPT
 *
 */
public class Papel {

    private Integer id;
    private String nomePapel;

    public Papel() {
    }

    public Papel(Integer id, String nomePapel) {
        this.id = id;
        this.nomePapel = nomePapel;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNomePapel() {
        return nomePapel;
    }

    public void setNomePapel(String nomePapel) {
        this.nomePapel = nomePapel;
    }
}