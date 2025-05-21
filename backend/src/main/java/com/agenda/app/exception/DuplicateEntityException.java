package com.agenda.app.exception;

/**
 * Exceção lançada quando uma tentativa de criar ou atualizar uma entidade
 * viola uma restrição de unicidade (ex: CNPJ ou nome duplicado)
 */
public class DuplicateEntityException extends RuntimeException {

    private final String field;
    private final String value;

    public DuplicateEntityException(String message) {
        super(message);
        this.field = null;
        this.value = null;
    }

    public DuplicateEntityException(String message, String field, String value) {
        super(message);
        this.field = field;
        this.value = value;
    }

    public String getField() {
        return field;
    }

    public String getValue() {
        return value;
    }
}