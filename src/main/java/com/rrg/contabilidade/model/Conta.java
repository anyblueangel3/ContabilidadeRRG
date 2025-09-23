package com.rrg.contabilidade.model;

/**
 *
 * @author Ronaldo Rodrigues Godoi e Chat GPT
 * 
 * Classe da entidade Conta do banco geral e empresa
 * 
 */
public class Conta {
    
    private String id;
    private Integer codigoSPED;
    private String descricao;
    private Classificacao classificacao;
    private Natureza natureza;
    private Integer idPlano;
    private boolean obrigatorioECD;
    private boolean obrigatorioECF;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getCodigoSPED() {
        return codigoSPED;
    }

    public void setCodigoSPED(Integer codigoSPED) {
        this.codigoSPED = codigoSPED;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Classificacao getClassificacao() {
        return classificacao;
    }

    public void setClassificacao(Classificacao classificacao) {
        this.classificacao = classificacao;
    }

    public Natureza getNatureza() {
        return natureza;
    }

    public void setNatureza(Natureza natureza) {
        this.natureza = natureza;
    }

    public Integer getIdPlano() {
        return idPlano;
    }

    public void setIdPlano(Integer idPlano) {
        this.idPlano = idPlano;
    }

    public boolean isObrigatorioECD() {
        return obrigatorioECD;
    }

    public void setObrigatorioECD(boolean obrigatorioECD) {
        this.obrigatorioECD = obrigatorioECD;
    }

    public boolean isObrigatorioECF() {
        return obrigatorioECF;
    }

    public void setObrigatorioECF(boolean obrigatorioECF) {
        this.obrigatorioECF = obrigatorioECF;
    }    
    
}
