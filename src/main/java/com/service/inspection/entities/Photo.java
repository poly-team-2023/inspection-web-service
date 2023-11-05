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
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "photo")
@Data
@NoArgsConstructor
public class Photo {

    @Id
    private UUID uuid;

    @JoinColumn(name = "plan_id")
    @ManyToOne
    private Plan plan;

    @Column(name = "location")
    private String location;

    @Column(name = "url", nullable = false, columnDefinition = "TEXT")
    private String url;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

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
        @JoinColumn(name = "photo_uuid")
        private Photo photo;

        @Embedded
        private Coord coords;
    }

}
