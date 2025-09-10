package com.rrg.contabilidade.controller;

import com.rrg.contabilidade.model.Usuario;
import com.rrg.contabilidade.model.dao.UsuarioDAO;

import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author Ronaldo Rodrigues Godoi e Chat GPT
 * 
 * Controller para operações de usuário.
 */
public class UsuarioController {

    private final UsuarioDAO usuarioDAO;

    public UsuarioController() {
        this.usuarioDAO = new UsuarioDAO();
    }

    // Inserir novo usuário
    public void criarUsuario(Usuario usuario) {
        if (usuario != null) {
            usuarioDAO.inserir(usuario);
        } else {
            JOptionPane.showMessageDialog(null, "Usuário inválido.");
        }
    }

    // Atualizar usuário existente
    public void atualizarUsuario(Usuario usuario) {
        if (usuario != null && usuario.getId() != null) {
            usuarioDAO.atualizar(usuario);
        } else {
            JOptionPane.showMessageDialog(null, "Usuário inválido ou sem ID.");
        }
    }

    // Deletar usuário pelo ID
    public void deletarUsuario(int id) {
        usuarioDAO.deletar(id);
    }

    // Buscar usuário por ID
    public Usuario buscarUsuarioPorId(int id) {
        return usuarioDAO.buscarPorId(id);
    }

    // Buscar usuário por login
    public Usuario buscarUsuarioPorLogin(String login) {
        return usuarioDAO.buscarPorLogin(login);
    }

    // Listar todos os usuários
    public List<Usuario> listarTodosUsuarios() {
        return usuarioDAO.listarTodos();
    }
}
