package com.rrg.contabilidade.util;

import com.rrg.contabilidade.model.Usuario;

public class PermissaoUtil {

    /**
     * Verifica se o usuário logado possui a operação solicitada.
     * Comparação estrita: código exato.
     */
    public static boolean temPermissao(String codigoOperacao) {
        Usuario usuarioLogado = SessaoDeUsuario.getUsuarioLogado();
        if (usuarioLogado == null) {
            return false;
        }

        return usuarioLogado.getOperacoes()
                .stream()
                .anyMatch(op -> op.getOperacao().equals(codigoOperacao));
    }

    /**
     * Sobrecarga que aceita o enum OperacoesDoSistema.
     */
    public static boolean temPermissao(OperacoesDoSistema operacaoEnum) {
        return temPermissao(operacaoEnum.getOperacao());
    }
}
