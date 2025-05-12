package com.agenda.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

/**
 * Representa a empresa no sistema.
 * A empresa pode ter várias filiais e serviços associados.
 * Cada empresa pode ter vários profissionais e clientes associados.
 *
 * Saber sobre esse trecho:
 * cascade = CascadeType.ALL -> significa que se eu apagar a company todas as subsidiaries são apagadas
 * caso não queira o exemplo acima seria cascade = CascadeType.REMOVE
 */

@Entity
@Table(name = "companies")
@Getter @Setter @NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Company extends BaseEntity {

    @NotBlank
    @Column(nullable = false, length = 100)
    private String name;

    @Embedded
    private Address address;

    @Column(length = 20)
    private String phone;

    @NotBlank
    @Column(name = "document_number", length = 20)
    private String documentNumber;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Subsidiary> subsidiaries;
}