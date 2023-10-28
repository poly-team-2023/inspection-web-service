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

    @Column(name = "legal_address", nullable = false, columnDefinition = "TEXT")
    private String legalAddress;

    @Column(name = "sro", nullable = false)
    private String sro;

    @Column(name = "license", nullable = false)
    private String license;

    @Column(name = "cipher", nullable = false)
    private String cipher;

    @Column(name = "logo_url", nullable = false, columnDefinition = "TEXT")
    private String logoUrl;

    @Column(name = "sro_scan_url", nullable = false, columnDefinition = "TEXT")
    private String sroScanUrl;

    @Column(name = "license_scan_url", nullable = false, columnDefinition = "TEXT")
    private String licenseScanUrl;
}