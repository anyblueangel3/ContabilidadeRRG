package com.rrg.contabilidade.util;

import com.rrg.contabilidade.model.Empresa;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 *
 * @author Ronaldo Rodrigues Godoi e Chat GPT
 * 
 * Classe responsável por abrir a conexão com o banco específico da empresa logada,
 * identificado pelo CNPJ. Análoga à classe AbreBancoGeral.
 */
public class AbreBancoEmpresa {

    public static Connection obterConexao() throws SQLException {
        // Verifica se há empresa logada
        if (!SessaoDeUsuario.isEmpresaLogada()) {
            JOptionPane.showMessageDialog(null, 
                    "Nenhuma empresa está logada no momento!");
            throw new SQLException("Tentativa de abrir banco sem empresa logada.");
        }

        Empresa empresa = SessaoDeUsuario.getEmpresaLogada();
        String cnpj = empresa.getCnpj();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Driver JDBC não encontrado no classpath!");
            throw new SQLException("Driver JDBC não encontrado no classpath!", e);
        }

        // Monta a URL do banco da empresa (exemplo: jdbc:mysql://localhost:3306/99999999999999)
        String urlBase = ConfiguracaoBanco.getUrl();
        // Remove possível banco padrão da URL
        if (urlBase.endsWith("/")) {
            urlBase = urlBase.substring(0, urlBase.length() - 1);
        }
        // Adiciona o CNPJ como nome do banco
        String urlEmpresa = urlBase + "/" + cnpj;

        return DriverManager.getConnection(
                urlEmpresa,
                ConfiguracaoBanco.getUsuario(),
                ConfiguracaoBanco.getSenha()
        );
    }
}
