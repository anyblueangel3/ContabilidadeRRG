package com.rrg.contabilidade;

import com.rrg.contabilidade.model.Empresa;
import com.rrg.contabilidade.model.Periodo;
import com.rrg.contabilidade.model.Usuario;
import com.rrg.contabilidade.util.SessaoDeUsuario;

/**
 *
 * @author Ronaldo Rodrigues Godoi e Chat GPT
 */
public class Lancamentos {
    
    ProgramaPrincipal programaPrincipal;
    Usuario usuario;
    Empresa empresa;
    Periodo periodo;
    
    public Lancamentos( ProgramaPrincipal pP, Usuario u, Empresa e, Periodo p) {
        this.programaPrincipal = pP;
        this.usuario = u == null ? SessaoDeUsuario.getUsuarioLogado() : u;
        this.empresa = e == null ? new Empresa() : e;
        this.periodo = p == null ? new Periodo() : p;
    }
    
}
