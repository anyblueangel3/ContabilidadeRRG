package com.rrg.contabilidade.util;

import com.rrg.contabilidade.model.Usuario;

/**
 * Controla o usuário atualmente logado no sistema.
 */
public class SessaoDeUsuario {
    private static Usuario usuarioLogado;

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
}
