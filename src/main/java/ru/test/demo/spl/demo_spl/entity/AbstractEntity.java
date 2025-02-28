package ru.test.demo.spl.demo_spl.entity;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;
import org.hibernate.Hibernate;
import org.hibernate.annotations.GenericGenerator;

import java.util.Objects;
import java.util.UUID;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Accessors(fluent = false, chain = true)
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@ToString
@MappedSuperclass
public abstract class AbstractEntity {
    @Id
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @GeneratedValue(generator = "UUID")
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (isNull(o) || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        var that = (AbstractEntity) Hibernate.getClass(this).cast(o);

        return nonNull(getId()) && getId().equals(that.getId());
    }

    @Override
    public final int hashCode() {
        return isNull(getId())
               ? Hibernate.getClass(this).hashCode()
               : Objects.hashCode(getId());
    }
}
