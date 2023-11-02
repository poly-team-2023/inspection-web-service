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

    @Column(name = "report_name")
    private String reportName;

    @Column(name = "start_date")
    private OffsetDateTime startDate;

    @Column(name = "end_date")
    private OffsetDateTime endDate;

    @Column(name="tor_url")
    private String torUrl;

    @Column(name = "work_plan_url")
    private String workPlan;

    @Column(name = "inspection_result")
    private String inspectionResult;

    @Column(name = "inspection_script")
    private String script;

    @Enumerated(EnumType.STRING)
    @Column(name = "inspection_status")
    private ProgressingStatus status;

    @Column(name = "inspected_categories_count")
    private int inspectedCategoriesCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "building_id", referencedColumnName = "id")
    private Building building;

    @ManyToMany(mappedBy = "inspections")
    private Set<Engineer> engineer;

    @OneToMany(mappedBy = "inspection")
    private Set<Category> categories;

    @OneToMany(mappedBy = "inspection", fetch = FetchType.LAZY)
    private Set<Audio> audios;
}
