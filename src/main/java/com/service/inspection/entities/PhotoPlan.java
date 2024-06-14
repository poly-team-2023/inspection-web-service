package com.service.inspection.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "photo_plan")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class PhotoPlan extends FileEntity {

    @JoinColumn(name = "plan_id")
    @ManyToOne
    private Plan plan;

    @Column(name = "x")
    private Double x;

    @Column(name = "y")
    private Double y;

    @OneToMany(mappedBy = "originPhoto")
    private List<Photo> connected;

    @Column(name = "last_update_date")
    private Instant lastUpdateTime;
}
