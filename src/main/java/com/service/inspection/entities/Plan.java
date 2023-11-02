package com.service.inspection.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Table(name = "plan")
@Data
@NoArgsConstructor
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "url", columnDefinition = "TEXT")
    private String planUrl;

    @OneToMany(mappedBy = "plan")
    private Set<Photo> photos;
}

