package com.service.inspection.entities;

import com.service.inspection.entities.enums.BuildingType;
import com.service.inspection.entities.enums.ProgressingStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.OffsetDateTime;
import java.util.Set;

@Entity
@Table(name = "inspection")
@Data
@NoArgsConstructor
public class Inspection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name", columnDefinition = "TEXT")
    private String name;

    @Column(name = "report_name")
    private String reportName;

    @Column(name = "start_date")
    private OffsetDateTime startDate;

    @Column(name = "end_date")
    private OffsetDateTime endDate;

    @Embedded
    private Building building;

    @Column(name = "tor_url")
    private String torUrl;

    @Column(name = "result")
    private String result;

    @Column(name = "script")
    private String script;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ProgressingStatus status;

    @Column(name = "inspected_category_count")
    private int inspectedCategoriesCount;

    @OneToMany(mappedBy = "inspection")
    private Set<WorkPlan> workPlans;

    @ManyToMany(mappedBy = "inspections")
    private Set<User> users;

    @OneToMany(mappedBy = "inspection")
    private Set<Category> categories;

    @OneToMany(mappedBy = "inspection", fetch = FetchType.LAZY)
    private Set<Audio> audios;

    @Embeddable
    @Data
    public static class Building {

        @Column(name = "address", columnDefinition = "TEXT")
        private String address;

        @Enumerated(EnumType.STRING)
        @Column(name = "building_type")
        private BuildingType buildingType;

        @OneToMany(mappedBy = "inspection")
        private Set<BuildingPhoto> photos;
    }

    @Entity
    @Table(name = "work_plan")
    @Data
    @NoArgsConstructor
    public static class WorkPlan {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private long id;
        private String url;

        @ManyToOne
        @JoinColumn(name = "inspection_id")
        @ToString.Exclude
        @EqualsAndHashCode.Exclude
        private Inspection inspection;
    }

    @Entity
    @Table(name = "building_photo")
    @Data
    @NoArgsConstructor
    public static class BuildingPhoto {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private long id;
        private String url;

        @ManyToOne
        @JoinColumn(name = "inspection_id")
        @ToString.Exclude
        @EqualsAndHashCode.Exclude
        private Inspection inspection;
    }
}
