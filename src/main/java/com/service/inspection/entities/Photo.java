package com.service.inspection.entities;

import com.service.inspection.entities.enums.ProgressingStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "photos")
@Data
@NoArgsConstructor
public class Photo {

    @Id
    private UUID uuid;

    @Column(name = "name", columnDefinition = "TEXT")
    private String name;

    @Column(name = "photo_url", nullable = false, columnDefinition = "TEXT")
    private String photoUrl;

    @Column(name = "photo_inspection_url", columnDefinition = "TEXT")
    private String photoInspectionUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "inspection_result", columnDefinition = "TEXT")
    private String inspectionResult;

    @Enumerated(EnumType.STRING)
    @Column(name = "inspection_status")
    private ProgressingStatus inspectionStatus;

    @Column(name = "photo_date")
    private OffsetDateTime photoDate;

    @OneToMany(mappedBy = "photo", fetch = FetchType.LAZY)
    private Set<Defect> defects;

}
