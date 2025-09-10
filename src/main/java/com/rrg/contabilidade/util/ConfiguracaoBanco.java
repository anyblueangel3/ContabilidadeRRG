package com.rrg.contabilidade.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import javax.swing.JOptionPane;

/**
 *
 * @author Chat GPT e Ronaldo Rodrigues Godoi
 *
 * Classe responsável por carregar as configurações do banco de dados a partir
 * do arquivo 'config.properties' localizado na pasta pública do Windows:
 * %PUBLIC%\ContabilidadeRRG.
 */
public class ConfiguracaoBanco {

    private static final Properties propriedades = new Properties();

    static {
        try (InputStream input = ConfiguracaoBanco.class
                .getClassLoader()
                .getResourceAsStream("config.properties")) {

            if (input == null) {
                JOptionPane.showMessageDialog(null, 
                        "ERRO: config.properties não encontrado dentro do JAR!");
            } else {
                propriedades.load(input);
            }

            propriedades.load(input);

        } catch (IOException e) {
            throw new RuntimeException("Erro ao carregar 'config.properties' interno: " + e.getMessage(), e);
        }
    }

    public static String getUrl() {
        return propriedades.getProperty("banco.url");
    }

    public static String getUsuario() {
        return propriedades.getProperty("banco.usuario");
    }

    public static String getSenha() {
        return propriedades.getProperty("banco.senha");
    }
}
