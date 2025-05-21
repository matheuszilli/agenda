package com.agenda.app.util;

import java.util.regex.Pattern;

/**
 * Utilitário para manipulação e validação de números de telefone
 */
public class PhoneUtils {

    /**
     * Formata um número de telefone para o padrão (XX) XXXXX-XXXX ou (XX) XXXX-XXXX
     * dependendo se é celular ou telefone fixo
     *
     * @param phone Número de telefone a ser formatado (pode estar em qualquer formato)
     * @return Telefone formatado ou null se o input for inválido
     */
    public static String formatPhone(String phone) {
        if (phone == null) {
            return null;
        }

        // Remove todos os caracteres não numéricos
        String numericPhone = phone.replaceAll("[^0-9]", "");

        // Se não tem DDD, pode ser um número local - não formatamos para não fazer suposições
        if (numericPhone.length() < 10) {
            return numericPhone;
        }

        // Se tem 11 dígitos, provavelmente é celular (com 9 na frente)
        if (numericPhone.length() == 11) {
            return String.format("(%s) %s-%s",
                    numericPhone.substring(0, 2),
                    numericPhone.substring(2, 7),
                    numericPhone.substring(7, 11));
        }

        // Se tem 10 dígitos, provavelmente é fixo
        if (numericPhone.length() == 10) {
            return String.format("(%s) %s-%s",
                    numericPhone.substring(0, 2),
                    numericPhone.substring(2, 6),
                    numericPhone.substring(6, 10));
        }

        // Se tem mais de 11 dígitos, pode ser com código de país ou outro formato
        // Retornamos sem formatação para não fazer suposições incorretas
        return numericPhone;
    }

    /**
     * Verifica se o telefone fornecido é válido (apenas sintaxe básica)
     *
     * @param phone Número de telefone a ser validado
     * @return true se estiver no formato válido, false caso contrário
     */
    public static boolean isValid(String phone) {
        if (phone == null) {
            return false;
        }

        // Remove todos os caracteres não numéricos
        String numericPhone = phone.replaceAll("[^0-9]", "");

        // Telefone brasileiro precisa ter 10 ou 11 dígitos (com 9 na frente)
        return numericPhone.length() >= 10 && numericPhone.length() <= 11;
    }

    /**
     * Extrai apenas os números de um telefone
     *
     * @param phone Número de telefone
     * @return Apenas os dígitos
     */
    public static String extractNumbers(String phone) {
        if (phone == null) {
            return null;
        }
        return phone.replaceAll("[^0-9]", "");
    }
}