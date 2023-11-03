package com.service.inspection.entities;

import com.service.inspection.entities.enums.BuildingType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Table(name = "building")
@Data
@NoArgsConstructor
public class Building {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name", nullable = false, columnDefinition = "TEXT")
    private String name;

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @Column(name="photo_url")
    private String url;

    @Enumerated(EnumType.STRING)
    @Column(name="building_type")
    private BuildingType buildingType;

    @OneToMany(mappedBy = "building", fetch = FetchType.LAZY)
    private Set<Inspection> inspections;
}
