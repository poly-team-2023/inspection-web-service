package com.service.inspection.entities;

import com.service.inspection.entities.enums.ProgressingStatus;
import jakarta.persistence.*;
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
    private String audioUrl;

    @Column(name = "text", columnDefinition = "TEXT")
    private String audioText;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ProgressingStatus audioStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inspection_id_fk")
    private Inspection inspection;

    @Column(name = "audio_date")
    private OffsetDateTime audioDate;
}