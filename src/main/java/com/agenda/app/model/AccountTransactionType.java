package com.agenda.app.model;

public enum AccountTransactionType {
    CREDIT,          // Add money to free balance
    DEBIT,           // Remove money from free balance
    HOLD,            // Move money from free to held balance
    RELEASE_HOLD,    // Move money from held to free balance
    CONFIRM_HOLD,    // Debit held balance (for completed service)
    ADJUSTMENT       // Manual adjustment by admin
}