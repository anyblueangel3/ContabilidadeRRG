package com.rrg.contabilidade.controller;

import com.rrg.contabilidade.model.Usuario;
import com.rrg.contabilidade.model.Operacao;
import com.rrg.contabilidade.model.dao.UsuarioDAO;

import javax.swing.*;
import java.sql.SQLException;
import java.util.List;

/*
 * @author Ronaldo Rodrigues Godoi e Chat GPT
 *
 * Controller de usuários...
 */
public class UsuarioController {

    private final UsuarioDAO usuarioDAO;

    public UsuarioController() {
        this.usuarioDAO = new UsuarioDAO();
    }

    // Criar usuário
    public void criarUsuario(Usuario usuario) {
        if (usuario == null) {
            JOptionPane.showMessageDialog(null, "Usuário inválido.");
            return;
        }
        try {
            usuarioDAO.inserir(usuario);
            JOptionPane.showMessageDialog(null, "Usuário inserido com sucesso.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao inserir usuário: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Atualizar usuário
    public void atualizarUsuario(Usuario usuario) {
        if (usuario == null || usuario.getId() == null) {
            JOptionPane.showMessageDialog(null, "Usuário inválido ou sem ID.");
            return;
        }
        try {
            usuarioDAO.atualizar(usuario);
            JOptionPane.showMessageDialog(null, "Usuário atualizado com sucesso.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao atualizar usuário: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Buscar usuário por login (carrega também as operações via OperacaoController)
    public Usuario buscarUsuarioPorLogin(String login) {
        try {
            Usuario usuario = usuarioDAO.buscarPorLogin(login);
            if (usuario != null) {
                try {
                    OperacaoController operacaoController = new OperacaoController();
                    List<Operacao> ops = operacaoController.buscarOperacoesPorUsuario(usuario.getId());
                    usuario.setOperacoes(ops);
                } catch (Exception ex) {
                    // se ocorrer erro ao carregar operações, registra e continua
                    JOptionPane.showMessageDialog(null, "Aviso: falha ao carregar operações do usuário: " + ex.getMessage());
                    ex.printStackTrace();
                    usuario.setOperacoes(List.of());
                }
            }
            return usuario;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao buscar usuário: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // Listar todos os usuários (cada usuário terá suas operações carregadas)
    public List<Usuario> listarTodosUsuarios() {
        try {
            List<Usuario> usuarios = usuarioDAO.listarTodos();
            OperacaoController operacaoController = new OperacaoController();
            for (Usuario u : usuarios) {
                try {
                    List<Operacao> ops = operacaoController.buscarOperacoesPorUsuario(u.getId());
                    u.setOperacoes(ops);
                } catch (Exception ex) {
                    // em caso de erro em uma conta, garantir que não fique nulo
                    ex.printStackTrace();
                    u.setOperacoes(List.of());
                }
            }
            return usuarios;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao listar usuários: " + e.getMessage());
            e.printStackTrace();
            return List.of(); // retorna lista vazia em caso de erro
        }
    }

    public void atribuirOperacoes(Usuario usuario) {
        try {
            usuarioDAO.atribuirOperacoes(usuario);
            JOptionPane.showMessageDialog(null, "Operações atribuídas com sucesso ao usuário.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao atribuir operações: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
