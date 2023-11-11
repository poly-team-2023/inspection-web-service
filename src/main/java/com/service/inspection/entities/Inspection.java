package com.service.inspection.entities;

import com.service.inspection.entities.enums.ProgressingStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.proxy.HibernateProxy;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "inspection")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Inspection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "report_name")
    private String reportName;

    @Column(name = "start_date")
    private OffsetDateTime startDate;

    @Column(name = "end_date")
    private OffsetDateTime endDate;

    @Column(name = "address")
    private String address;

    @Column(name = "result")
    private String result;

    @Column(name = "script")
    private String script;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ProgressingStatus status;

    @Column(name = "main_photo_name")
    private String mainPhotoName;

    @Column(name = "main_photo_uuid")
    private UUID mainPhotoUuid;

    @Column(name = "inspected_category_count")
    private int inspectedCategoriesCount;

    @ManyToMany(mappedBy = "inspections")
    @ToString.Exclude
    private Set<User> users;

    @OneToMany(mappedBy = "inspection", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @ToString.Exclude
    private Set<Category> categories;

    @OneToMany(mappedBy = "inspection", cascade = CascadeType.ALL,  fetch = FetchType.LAZY)
    @ToString.Exclude
    private Set<Audio> audios;

    @OneToMany(mappedBy = "inspection", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private Set<FileInspection> fileInspections;

    @OneToMany(mappedBy = "inspection", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private Set<Plan> plans;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    @ToString.Exclude
    private Company company;

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
