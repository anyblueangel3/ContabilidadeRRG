package com.rrg.contabilidade.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 *
 * @author Ronaldo Rodrigues Godoi e Chat GPT
 * 
 *  Utilitário para hash e verificação de senhas com BCrypt.
 * 
 */
public class PasswordUtils {

    private static final int STRENGTH = 12; // fator de custo (10–14 recomendado)
    private static final PasswordEncoder encoder = new BCryptPasswordEncoder(STRENGTH);

    // Gera o hash da senha em texto plano
    public static String hash(String senhaPlana) {
        return encoder.encode(senhaPlana);
    }

    // Verifica se a senha corresponde ao hash armazenado
    public static boolean verificar(String senhaPlana, String hashArmazenado) {
        if (senhaPlana == null || hashArmazenado == null) return false;
        return encoder.matches(senhaPlana, hashArmazenado);
    }

    // Detecta se um valor já está no formato BCrypt
    public static boolean pareceBCrypt(String valor) {
        return valor != null && 
                (valor.startsWith("$2a$") ||
                 valor.startsWith("$2b$") || 
                 valor.startsWith("$2y$"));
    }
}
