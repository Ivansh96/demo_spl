package ru.test.demo.spl.demo_spl.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.test.demo.spl.demo_spl.entity.MetadataField;


import java.util.Set;
import java.util.UUID;

@Repository
public interface MetadataFieldRepository extends JpaRepository<MetadataField, UUID> {
    Set<MetadataField> findAllByNameIn(Set<String> names);
    Set<MetadataField> findAllByNameNotIn(Set<String> names);
    Set<MetadataField> findAllByNameLikeIgnoreCase(String names);
}
