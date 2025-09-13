package com.rrg.contabilidade.controller;

import com.rrg.contabilidade.model.Usuario;
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

    // Buscar usuário por login
    public Usuario buscarUsuarioPorLogin(String login) {
        try {
            return usuarioDAO.buscarPorLogin(login);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao buscar usuário: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // Listar todos os usuários
    public List<Usuario> listarTodosUsuarios() {
        try {
            return usuarioDAO.listarTodos();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao listar usuários: " + e.getMessage());
            e.printStackTrace();
            return List.of(); // retorna lista vazia em caso de erro
        }
    }
}
