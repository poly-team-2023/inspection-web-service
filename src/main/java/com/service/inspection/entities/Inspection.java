package com.service.inspection.entities;

import com.service.inspection.entities.enums.BuildingType;
import com.service.inspection.entities.enums.ProgressingStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.BatchSize;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "inspection")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Inspection extends Named {

    @Column(name = "report_name")
    private String reportName = "Технический отчет об обследовании";

    // TODO:  разобраться как правильно хранить дату
    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "address")
    private String address;

    @Column(name = "result")
    private String result;

    @Column(name = "script")
    private String script;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ProgressingStatus status = ProgressingStatus.WAIT_FILLING;

    @Column(name = "main_photo_name")
    private String mainPhotoName;

    @Column(name = "main_photo_uuid")
    private UUID mainPhotoUuid;

    @Column(name = "inspected_category_count")
    private int inspectedCategoriesCount;

    @Column(name = "report_uuid")
    private UUID reportUuid;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "employer_id")
    @ToString.Exclude
    private Employer employer;

    @ManyToMany(mappedBy = "inspections")
    @ToString.Exclude
    private Set<User> users;

    @OneToMany(mappedBy = "inspection", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @ToString.Exclude
    @BatchSize(size = 50)
    private List<Category> categories = new ArrayList<>();

    @OneToMany(mappedBy = "inspection", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    @BatchSize(size = 50)
    private Set<Audio> audios;

    @OneToMany(mappedBy = "inspection", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    @BatchSize(size = 50)
    private Set<FileInspection> fileInspections;

    @OneToMany(mappedBy = "inspection", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    @BatchSize(size = 50)
    private Set<Plan> plans;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "company_id")
    @ToString.Exclude
    private Company company;

    @Enumerated(EnumType.STRING)
    @Column(name = "building_type")
    private BuildingType buildingType;

    public void addCategory(Category category) {
        if (categories == null) {
            categories = new ArrayList<>();
        }
        categories.add(category);
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Inspection that = (Inspection) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
