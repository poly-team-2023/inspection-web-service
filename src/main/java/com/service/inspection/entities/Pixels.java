package com.service.inspection.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "pixels")
@Data
@NoArgsConstructor
public class Pixels {

    @Id
    private UUID uuid;

    @Column(name = "x")
    private double x;

    @Column(name = "y")
    private double y;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "defect_uuid")
    private Defect defect;

}
