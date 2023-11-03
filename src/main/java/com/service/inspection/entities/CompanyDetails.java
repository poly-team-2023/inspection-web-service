package com.service.inspection.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "company_details")
@Data
@NoArgsConstructor
public class CompanyDetails {

    @Id
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    @Column(name = "legal_address", columnDefinition = "TEXT")
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
}