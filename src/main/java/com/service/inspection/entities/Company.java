package com.service.inspection.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Set;

@Entity
@Table(name = "companies")
@Data
@NoArgsConstructor
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name", nullable = false, columnDefinition = "TEXT")
    private String name;

    @OneToOne(mappedBy = "company", cascade = CascadeType.ALL)
    private CompanyDetails companyDetails;

    @OneToMany(mappedBy = "engineerId.company", fetch = FetchType.LAZY)
    private Set<Engineer> engineers;

    @OneToMany(mappedBy = "company", fetch = FetchType.LAZY)
    private Set<Employer> employers;

    @OneToMany(mappedBy = "company", fetch = FetchType.LAZY)
    private Set<Equipment> equipments;
}
