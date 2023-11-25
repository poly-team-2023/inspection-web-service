package com.service.inspection.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "employer")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Employer extends Named {

    @Column(name = "position_name")
    private String positionName;

    @Column(name = "signature_uuid", nullable = false)
    private UUID signatureUuid;

    @Column(name = "signature_name")
    private String signatureName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    @ToString.Exclude
    private Company company;

    @OneToMany(mappedBy = "employer")
    private Set<Inspection> inspections;

    @PreRemove
    public void preRemove() {
        Optional.ofNullable(company)
                .map(Company::getEmployers)
                .ifPresent(employers -> employers.remove(this));

        this.setCompany(null);
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Employer employer = (Employer) o;
        return getId() != null && Objects.equals(getId(), employer.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
