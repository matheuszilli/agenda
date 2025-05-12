package com.agenda.app.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable          // <-- avisa ao JPA que é um Value Object
@Getter @Setter @NoArgsConstructor
public class Address {

    @Column(nullable = false, length = 120)
    private String street;

    @Column(nullable = false, length = 10)
    private String number;

    private String complement;
    private String neighbourhood;

    @Column(nullable = false, length = 60)
    private String city;

    @Column(nullable = false, length = 2)
    private String state;

    @Column(name = "zip_code", nullable = false, length = 10)
    private String zipCode;
}
