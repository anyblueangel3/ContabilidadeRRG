package com.rrg.contabilidade.util;

import com.rrg.contabilidade.model.Empresa;
import com.rrg.contabilidade.model.Periodo;
import com.rrg.contabilidade.model.Usuario;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Ronaldo Rodrigues Godoi e Chat GPT
 *
 * Controla o usuário atualmente logado no sistema.
 */
public class SessaoDeUsuario {

    private static Usuario usuarioLogado;
    private static Empresa empresaLogada;
    private static Periodo periodoLogado;

    private static final List<SessaoListener> listeners = new ArrayList<>();

    public static void addListener(SessaoListener listener) {
        listeners.add(listener);
    }

    private static void notificarLogar() {
        for (SessaoListener l : listeners) {
            l.aoLogar();
        }
    }

    private static void notificarLogout() {
        for (SessaoListener l : listeners) {
            l.aoLogout();
        }
    }

    public static void logar(Usuario usuario) {
        usuarioLogado = usuario;
        notificarLogar();
    }

    public static void logout() {
        usuarioLogado = null;
        notificarLogout();
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
        notificarLogar();
    }

    public static void logoutPeriodo() {

        if (periodoLogado == null) {
            // Já está deslogado, não faz nada
            return;
        }

        periodoLogado = null;
        notificarLogout();
    }

    public static boolean isPeriodoLogado() {
        return periodoLogado != null;
    }

    public static Periodo getPeriodoLogado() {
        return periodoLogado;
    }
}
