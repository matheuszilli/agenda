package com.agenda.app.util;

import java.util.regex.Pattern;

/**
 * Utilitário para manipulação e validação de CNPJs
 */
public class CnpjUtils {

    /**
     * Formata um CNPJ para o padrão XX.XXX.XXX/XXXX-XX
     * Remove todos os caracteres não numéricos e depois formata
     *
     * @param cnpj CNPJ a ser formatado (pode estar em qualquer formato)
     * @return CNPJ formatado ou null se o input for inválido
     */
    public static String formatCnpj(String cnpj) {
        if (cnpj == null) {
            return null;
        }

        // Remove todos os caracteres não numéricos
        String numericCnpj = cnpj.replaceAll("[^0-9]", "");

        // Verifica se tem 14 dígitos
        if (numericCnpj.length() != 14) {
            throw new IllegalArgumentException("CNPJ deve ter 14 dígitos numéricos");
        }

        // Aplica a máscara XX.XXX.XXX/XXXX-XX
        return String.format("%s.%s.%s/%s-%s",
                numericCnpj.substring(0, 2),
                numericCnpj.substring(2, 5),
                numericCnpj.substring(5, 8),
                numericCnpj.substring(8, 12),
                numericCnpj.substring(12, 14));
    }

    /**
     * Extrai os 8 primeiros dígitos de um CNPJ (raiz do CNPJ)
     * Esses dígitos identificam a empresa principal e são comuns a todas as filiais
     *
     * @param cnpj CNPJ (formatado ou não)
     * @return Raiz do CNPJ (8 primeiros dígitos sem formatação)
     */
    public static String extractCnpjRoot(String cnpj) {
        if (cnpj == null) {
            return null;
        }

        // Remove todos os caracteres não numéricos
        String numericCnpj = cnpj.replaceAll("[^0-9]", "");

        // Verifica se tem 14 dígitos
        if (numericCnpj.length() != 14) {
            throw new IllegalArgumentException("CNPJ deve ter 14 dígitos numéricos");
        }

        // Retorna os 8 primeiros dígitos
        return numericCnpj.substring(0, 8);
    }

    /**
     * Verifica se dois CNPJs pertencem à mesma empresa (têm a mesma raiz)
     *
     * @param cnpj1 Primeiro CNPJ
     * @param cnpj2 Segundo CNPJ
     * @return true se os CNPJs são da mesma empresa, false caso contrário
     */
    public static boolean isSameCompany(String cnpj1, String cnpj2) {
        return extractCnpjRoot(cnpj1).equals(extractCnpjRoot(cnpj2));
    }
}