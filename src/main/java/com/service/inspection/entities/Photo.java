package com.service.inspection.entities;

import com.service.inspection.entities.enums.ProgressingStatus;
import jakarta.persistence.*;
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

    @JoinColumn(name = "plan_id")
    @ManyToOne
    private Plan plan;

    @OneToOne(mappedBy = "photo")
    private PhotoCoord coords;

    @Embeddable
    @Data
    public static class Coord {
        private Double x;
        private Double y;
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

    @Entity
    @Data
    @Table(name = "photo_coord")
    public static class PhotoCoord {

        @Id
        @OneToOne
        @JoinColumn(name = "photo_id", referencedColumnName = "id")
        private Photo photo;

        @Embedded
        private Coord coords;
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
