package com.service.inspection.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "equipment")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Equipment extends Named {

    @Column(name = "serial_number", nullable = false)
    private String serialNumber;

    @Column(name = "verification_number")
    private String verificationNumber;

    @Column(name = "verification_date", nullable = false)
    private LocalDate verificationDate;

    @Column(name = "verification_scan_name")
    private String verificationScanName;

    @Column(name = "verification_scan_uuid")
    private UUID verificationScanUuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private User user;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Equipment equipment = (Equipment) o;
        return getId() != null && Objects.equals(getId(), equipment.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
