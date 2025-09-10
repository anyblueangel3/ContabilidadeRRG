package com.rrg.contabilidade.model;

import java.io.Serializable;

/**
 * 
 * @author Ronaldo Rodrigues Godoi e Chat GPT
 * 
 * Representa uma empresa cliente do sistema.
 * 
 */
public class Empresa implements Serializable {

    private String cnpj;                 // PK
    private String razao;
    private String endereco;
    private String responsavel;
    private String telefoneEmpresa;
    private String telefoneResponsavel;

    public Empresa() {}

    public Empresa(String cnpj, String razao, String endereco, String responsavel,
                   String telefoneEmpresa, String telefoneResponsavel) {
        this.cnpj = cnpj;
        this.razao = razao;
        this.endereco = endereco;
        this.responsavel = responsavel;
        this.telefoneEmpresa = telefoneEmpresa;
        this.telefoneResponsavel = telefoneResponsavel;
    }

    // Getters e Setters
    public String getCnpj() { return cnpj; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }

    public String getRazao() { return razao; }
    public void setRazao(String razao) { this.razao = razao; }

    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }

    public String getResponsavel() { return responsavel; }
    public void setResponsavel(String responsavel) { this.responsavel = responsavel; }

    public String getTelefoneEmpresa() { return telefoneEmpresa; }
    public void setTelefoneEmpresa(String telefoneEmpresa) { this.telefoneEmpresa = telefoneEmpresa; }

    public String getTelefoneResponsavel() { return telefoneResponsavel; }
    public void setTelefoneResponsavel(String telefoneResponsavel) { this.telefoneResponsavel = telefoneResponsavel; }
}
