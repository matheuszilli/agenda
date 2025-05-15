package com.agenda.app.model;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;

public enum UserRole {
    ADMIN,
    PROFESSIONAL,
    MAIN_DOCTOR,
    SECOND_DOCTOR,
    NURSE,
    CUSTOM,
    RECEPTIONIST;

    /**
     * USADO PRA SEGURANÇA, DEPOIS ENTENDER COMO FUNCIONA BEM, MAS VI QUE PRECISA
     */
    public List<SimpleGrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + this.name()));
    }
}