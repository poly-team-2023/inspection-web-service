package com.service.inspection.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "plan_defect")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class PlanDefect {

    @Column(name="uuid")
    @Id
    private UUID uuid = UUID.randomUUID();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", referencedColumnName = "id")
    private Plan plan;

    @Column(name = "closed")
    private Boolean isClosed;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "coords")
    private List<PlanDefectCoord> coords;

    @Column(name = "last_update_date")
    private Instant lastUpdateDate = Instant.now();

    @Data
    public static class PlanDefectCoord {
        private Integer position;
        private Double x;
        private Double y;
    }
}
