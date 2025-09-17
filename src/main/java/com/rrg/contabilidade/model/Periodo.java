package com.rrg.contabilidade.model;

import java.sql.Date;

/**
 *
 * @author Ronaldo Rodrigues Godoi e Chat GPT
 */
public class Periodo {
    
    private Integer id;
    private Date inicio;
    private Date fim;
    private StatusPeriodo status;
    private Integer idPlano;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getInicio() {
        return inicio;
    }

    public void setInicio(Date inicio) {
        this.inicio = inicio;
    }

    public Date getFim() {
        return fim;
    }

    public void setFim(Date fim) {
        this.fim = fim;
    }

    public StatusPeriodo getStatus() {
        return status;
    }

    public void setStatus(StatusPeriodo status) {
        this.status = status;
    }

    public Integer getIdPlano() {
        return idPlano;
    }

    public void setIdPlano(Integer idPlano) {
        this.idPlano = idPlano;
    }
    
    
    
}
