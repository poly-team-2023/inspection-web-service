package com.service.inspection.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Entity
@Table(name = "defects")
@Data
@NoArgsConstructor
public class Defect {

    @Id
    private UUID uuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "photo_id")
    private Photo photo;

    @Column(name = "name")
    private String name;

    @Column(name = "location")
    private String location;

    @Column(name = "elumanation_way")
    private String elumanationWay;

}
