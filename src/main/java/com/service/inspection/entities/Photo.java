package com.service.inspection.entities;

import com.service.inspection.entities.enums.ProgressingStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.List;
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

    @JoinColumn(name="plan_id")
    @ManyToOne
    private Plan plan;

    @Column(name = "place")
    private String photoLocation;

    @Column(name = "url", nullable = false, columnDefinition = "TEXT")
    private String photoUrl;

    @Column(name = "inspection_url", columnDefinition = "TEXT")
    private String photoInspectionUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "recommendation", columnDefinition = "TEXT")
    private String recommendation;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ProgressingStatus inspectionStatus;

    @Column(name = "photo_date")
    private OffsetDateTime photoDate;

    @JdbcTypeCode(SqlTypes.JSON)
    private Set<Defect> defectsCoord;

    @JdbcTypeCode(SqlTypes.JSON)
    private Set<DefectsElimination> defectsEliminations;

    @OneToOne(mappedBy = "photo")
    private PhotoCoord photoCoord;


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
