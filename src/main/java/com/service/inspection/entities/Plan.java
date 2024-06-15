package com.service.inspection.entities;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.proxy.HibernateProxy;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "plan")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AttributeOverride(name = "fileUuid", column = @Column(name = "uuid"))
public class Plan extends FileEntity {

    @OneToMany(mappedBy = "plan")
    @ToString.Exclude
    private Set<PhotoPlan> photos;

    @ManyToOne
    @JoinColumn(name = "inspection_id", referencedColumnName = "id")
    @EqualsAndHashCode.Exclude
    private Inspection inspection;

    @OneToMany(mappedBy = "plan")
    private List<PlanDefect> planDefects = new ArrayList<>();

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Plan plan = (Plan) o;
        return getId() != null && Objects.equals(getId(), plan.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}