package com.service.inspection.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Table(name = "company")
@Data
@NoArgsConstructor
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name", nullable = false, columnDefinition = "TEXT")
    private String name;

    @Column(name = "legal_address", nullable = false, columnDefinition = "TEXT")
    private String legalAddress;

    @Column(name = "sro")
    private String sro;

    @Column(name = "license")
    private String license;

    @Column(name = "cipher")
    private String cipher;

    @Column(name = "logo_url", columnDefinition = "TEXT")
    private String logoUrl;

    @Column(name = "sro_scan_url", columnDefinition = "TEXT")
    private String sroScanUrl;

    @Column(name = "license_scan_url", columnDefinition = "TEXT")
    private String licenseScanUrl;

    @OneToMany(mappedBy = "company", fetch = FetchType.LAZY)
    private Set<Employer> employers;

    @OneToMany(mappedBy = "company", fetch = FetchType.LAZY)
    private Set<Equipment> equipments;
}
