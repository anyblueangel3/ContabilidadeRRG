package com.rrg.contabilidade.model;

import java.sql.Timestamp;

/**
 *
 * @author Ronaldo Rodrigues Godoi e Chat GPT
 * 
 */
public class Usuario {
    
    private Integer id;
    private String nome;
    private String login;
    private String senha;
    private Integer papel;
    private String status;
    private Timestamp dataCriacao;
    private Timestamp dataUltimoAcesso;
    
    public Usuario() {};
    
    // Construtor completo
    public Usuario(Integer id, String nome, String login, String senha,
                   Integer papel, String status, Timestamp dataCriacao,
                   Timestamp dataUltimoAcesso) {
        this.id = id;
        this.nome = nome;
        this.login = login;
        this.senha = senha;
        this.papel = papel;
        this.status = status;
        this.dataCriacao = dataCriacao;
        this.dataUltimoAcesso = dataUltimoAcesso;
    }    
    
    // Construtor para novo usuário (sem id e timestamps)
    public Usuario(String nome, String login, String senha, Integer papel, String status) {
        this.nome = nome;
        this.login = login;
        this.senha = senha;
        this.papel = papel;
        this.status = status;
    }

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

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Integer getPapel() {
        return papel;
    }

    public void setPapel(Integer papel) {
        this.papel = papel;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(Timestamp dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public Timestamp getDataUltimoAcesso() {
        return dataUltimoAcesso;
    }

    public void setDataUltimoAcesso(Timestamp dataUltimoAcesso) {
        this.dataUltimoAcesso = dataUltimoAcesso;
    }

    @Override
    public String toString() {
        return "Usuario{" + "id=" + id + ", nome=" + nome + ", login=" + login +
                ", senha=" + senha + ", papel=" + papel + 
                ", status=" + status + ", dataCriacao=" + dataCriacao + 
                ", dataUltimoAcesso=" + dataUltimoAcesso + '}';
    }

}