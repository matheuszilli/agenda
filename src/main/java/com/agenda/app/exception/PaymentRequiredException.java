package com.agenda.app.exception;

public class PaymentRequiredException extends RuntimeException {
    public PaymentRequiredException(String message) { super(message); }
}
