package com.service.inspection.entities;

import com.service.inspection.entities.enums.ProgressingStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Entity
@Table(name = "audio")
@Data
@NoArgsConstructor
public class Audio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "url", columnDefinition = "TEXT")
    private String url;

    @Column(name = "text", columnDefinition = "TEXT")
    private String text;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ProgressingStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inspection_id_fk")
    private Inspection inspection;

    @Column(name = "date")
    private OffsetDateTime date;
}