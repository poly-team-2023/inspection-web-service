package com.service.inspection.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "defect_type")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class DefectType {

    @Column(name="uuid")
    @Id
    private UUID uuid = UUID.randomUUID();

    @ManyToOne
    @JoinColumn(name = "inspection_id", referencedColumnName = "id")
    private Inspection inspection;

    @Column(name = "name")
    private String name;

    @Column(name = "hex_code")
    private String hexCode;

    @Column(name = "last_update_date")
    private Instant lastUpdateDate = Instant.now();
}
