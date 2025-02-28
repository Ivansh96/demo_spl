package ru.test.demo.spl.demo_spl.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.test.demo.spl.demo_spl.entity.EventClassId;


import java.util.Set;
import java.util.UUID;

@Repository
public interface EventClassIdRepository extends JpaRepository<EventClassId, UUID> {
    Set<EventClassId> findAllByNameIn(Set<String> names);
    Set<EventClassId> findAllByNameNotIn(Set<String> names);
    Set<EventClassId> findAllByNameLikeIgnoreCase(String names);
}
