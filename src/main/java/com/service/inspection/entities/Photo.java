package com.service.inspection.entities;

import com.service.inspection.entities.enums.ProgressingStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "photo")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Photo extends FileEntity {

    @Column(name = "location")
    private String location;

    @Column(name = "recommendation", columnDefinition = "TEXT")
    private String recommendation;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ProgressingStatus status;

    @Column(name = "date")
    private OffsetDateTime date;

    @Column(name = "photo_num")
    private Long photoNum;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "defects_coord")
    private Set<Defect> defectsCoords;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "defects_eliminations")
    private Set<DefectsElimination> defectsEliminations;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Category category;

    @ManyToOne
    @JoinColumn(name = "photo_plan_id")
    private PhotoPlan originPhoto;

    @Embeddable
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Coord {
        private Integer x;
        private Integer y;
    }

    @Data
    public static class Defect {
        private String name;
        private List<Coord> coords;
    }

    @Data
    public static class DefectsElimination {
        private String defectName;
        private String defectEliminationRecommendation;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Photo photo = (Photo) o;
        return getId() != null && Objects.equals(getId(), photo.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
