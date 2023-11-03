package com.service.inspection.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "engineers")
@Data
@NoArgsConstructor
public class Engineer {

    @EmbeddedId
    private EngineerId engineerId;

    @Column(name = "position_name")
    private String positionName;

    @ManyToMany
    @JoinTable(
            name = "engineer_inspector",
            joinColumns = {
                    @JoinColumn(name="user_id", referencedColumnName="user_id"),
                    @JoinColumn(name="company_id", referencedColumnName="company_id")
            },
            inverseJoinColumns = @JoinColumn(name = "inspection_id")
    )
    private Set<Inspection> inspections;

    @Embeddable
    @Data
    @NoArgsConstructor
    class EngineerId implements Serializable {

        @ManyToOne
        @JoinColumn(name = "user_id")
        private User user;

        @ManyToOne
        @JoinColumn(name = "company_id")
        private Company company;
    }
}