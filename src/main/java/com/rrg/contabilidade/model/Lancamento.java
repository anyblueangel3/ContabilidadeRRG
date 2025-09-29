package com.rrg.contabilidade.model;

import java.sql.Date;
import java.util.ArrayList;

/**
 *
 * @author Ronaldo Rodrigues Godoi e Chat GPT
 * 
 * Classe central de toda contabilidade em partidas dobradas
 * 
 */
public class Lancamento {

    private Long id;
    private Integer idPeriodo;
    private Date dataLancamento;
    private String historico;
    private Integer idUsuario;
    private ArrayList<LancamentoItem> lancamentoItens;

    // Construtor padrão
    public Lancamento() {
        this.lancamentoItens = new ArrayList<>();
    }

    // Construtor com parâmetros essenciais
    public Lancamento(Integer idPeriodo, Date dataLancamento, String historico, Integer idUsuario) {
        this();
        this.idPeriodo = idPeriodo;
        this.dataLancamento = dataLancamento;
        this.historico = historico;
        this.idUsuario = idUsuario;
    }

    // Adiciona um item ao lançamento
    public void adicionarItem(LancamentoItem item) {
        if (this.lancamentoItens == null) {
            this.lancamentoItens = new ArrayList<>();
        }
        this.lancamentoItens.add(item);
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getIdPeriodo() {
        return idPeriodo;
    }

    public void setIdPeriodo(Integer idPeriodo) {
        this.idPeriodo = idPeriodo;
    }

    public Date getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(Date dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    public String getHistorico() {
        return historico;
    }

    public void setHistorico(String historico) {
        this.historico = historico;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public ArrayList<LancamentoItem> getLancamentoItens() {
        return lancamentoItens;
    }

    public void setLancamentoItens(ArrayList<LancamentoItem> lancamentoItens) {
        this.lancamentoItens = lancamentoItens;
    }
}
