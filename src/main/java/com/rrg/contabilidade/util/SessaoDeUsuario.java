package com.rrg.contabilidade.util;

import com.rrg.contabilidade.model.Empresa;
import com.rrg.contabilidade.model.Periodo;
import com.rrg.contabilidade.model.Usuario;

/**
 * Controla o usuário atualmente logado no sistema.
 */
public class SessaoDeUsuario {
    private static Usuario usuarioLogado;
    private static Empresa empresaLogada;
    private static Periodo periodoLogado;

    public static void logar(Usuario usuario) {
        usuarioLogado = usuario;
    }

    public static void logout() {
        usuarioLogado = null;
    }

    public static boolean isLogado() {
        return usuarioLogado != null;
    }

    public static Usuario getUsuarioLogado() {
        return usuarioLogado;
    }
    
    public static void logarEmpresa(Empresa empresa) {
        empresaLogada = empresa;
    }

    public static void logoutEmpresa() {
        empresaLogada = null;
    }

    public static boolean isEmpresaLogada() {
        return empresaLogada != null;
    }

    public static Empresa getEmpresaLogada() {
        return empresaLogada;
    }
    
    public static void logarPeriodo(Periodo periodo) {
        periodoLogado = periodo;
    }

    public static void logoutPeriodo() {
        periodoLogado = null;
    }

    public static boolean isPeriodoLogado() {
        return periodoLogado != null;
    }

    public static Periodo getPeriodoLogado() {
        return periodoLogado;
    }
    
}
