package com.service.inspection.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Entity
@Table(name = "equipment")
@Data
@NoArgsConstructor
public class Equipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "serial_number", nullable = false)
    private String serialNumber;

    @Column(name = "verification_date")
    private OffsetDateTime verificationDate;

    @Column(name = "verification_scan_url", columnDefinition = "TEXT")
    private String verificationScanUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;
}
