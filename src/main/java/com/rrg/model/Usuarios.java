package com.rrg.model;

/**
 *
 * @author Usuario
 */
public class Usuarios {
    
    private int id;
    private String nome;
    private String senha;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
    
}

/*

Tabela do banco de dados Geral

Nome da tabela: usuarios

Campo e tipo
id, integer
nome, varchar(100)
senha, varchar(255)
*/