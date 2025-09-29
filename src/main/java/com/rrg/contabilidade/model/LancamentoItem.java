package com.rrg.contabilidade.model;

import com.rrg.contabilidade.model.TipoLancamento;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 *
 * @author Ronaldo Rodrigues Godoi e Chat GPT
 */
public class LancamentoItem {
    
    private Long id;
    private Long idLancamento;
    private Integer idPlano;
    private String idConta;
    private TipoLancamento tipo;
    private BigDecimal valor;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdLancamento() {
        return idLancamento;
    }

    public void setIdLancamento(Long idLancamento) {
        this.idLancamento = idLancamento;
    }

    public Integer getIdPlano() {
        return idPlano;
    }

    public void setIdPlano(Integer idPlano) {
        this.idPlano = idPlano;
    }

    public String getIdConta() {
        return idConta;
    }

    public void setIdConta(String idConta) {
        this.idConta = idConta;
    }

    public TipoLancamento getTipo() {
        return tipo;
    }

    public void setTipo(TipoLancamento tipo) {
        this.tipo = tipo;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }    
    
}