package com.service.inspection.entities;

import com.service.inspection.entities.enums.ProgressingStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.Set;

@Entity
@Table(name = "inspection")
@Data
@NoArgsConstructor
public class Inspection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name", columnDefinition = "TEXT")
    private String name;

    @Column(name = "start_date")
    private OffsetDateTime startDate;

    @ManyToMany(mappedBy = "inspections")
    private Set<Engineer> engineer;

    @Column(name = "end_date")
    private OffsetDateTime endDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "object_id")
    private InspectedObject object;

    @Enumerated(EnumType.STRING)
    @Column(name = "inspection_status")
    private ProgressingStatus inspectionStatus;

    @Column(name = "inspected_categories_count")
    private int inspectedCategoriesCount;

    @OneToMany(mappedBy = "inspection")
    private Set<Category> categories;

    @OneToMany(mappedBy = "inspection", fetch = FetchType.LAZY)
    private Set<Audio> audios;

}
